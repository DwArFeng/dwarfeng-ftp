package com.dwarfeng.ftp.handler;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.exception.*;
import com.dwarfeng.ftp.util.Constants;
import com.dwarfeng.subgrade.sdk.interceptor.analyse.BehaviorAnalyse;
import com.dwarfeng.subgrade.sdk.interceptor.analyse.SkipRecord;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.*;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FtpHandlerImpl implements FtpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpHandlerImpl.class);

    /**
     * FTP协议所使用的标准字符集。
     */
    private static final String STANDARD_FILE_NAME_CHARSET = "ISO-8859-1";
    /**
     * 最小超时时间。
     */
    private static final int MIN_CONNECT_TIMEOUT = 1000;

    private final ThreadPoolTaskScheduler scheduler;

    private final String ftpHost;
    private final int ftpPort;
    private final String ftpUserName;
    private final String ftpPassword;
    private final String serverCharset;
    private final int connectTimeout;
    private final long noopInterval;

    private final Lock lock = new ReentrantLock();

    private FTPClient ftpClient = null;
    private ScheduledFuture<?> noopSendTaskFuture;
    private boolean startedFlag = false;

    public FtpHandlerImpl(
            ThreadPoolTaskScheduler scheduler, String ftpHost, int ftpPort, String ftpUserName, String ftpPassword,
            String serverCharset, int connectTimeout, long noopInterval
    ) {
        this.scheduler = scheduler;
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.serverCharset = serverCharset;
        this.connectTimeout = connectTimeout;
        this.noopInterval = noopInterval;
    }

    @Override
    public boolean isStarted() {
        lock.lock();
        try {
            return startedFlag;
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void start() throws HandlerException {
        lock.lock();
        try {
            if (startedFlag) {
                return;
            }

            // 日志记录。
            LOGGER.info("FtpHandler 连接...");

            //检查参数是否合法。
            if (connectTimeout <= MIN_CONNECT_TIMEOUT) {
                throw new IllegalArgumentException("配置ftp.connect_timeout的值太小，应该大于1000");
            }
            if (noopInterval >= connectTimeout) {
                throw new IllegalArgumentException("配置ftp.noop_interval的值太大，应该小于ftp.connect_timeout");
            }

            // 初始化 FTP 客户端。
            ftpClient = new FTPClient();

            // 设置 FTP 服务器为本地被动模式。
            ftpClient.enterLocalPassiveMode();

            // 连接并登录。
            try {
                connectAndLogin();
            } catch (Exception e) {
                LOGGER.warn("FTP 连接失败，将会启用重连机制尝试重新连接", e);
            }

            // 添加noop周期发送计划。
            this.noopSendTaskFuture = scheduler.scheduleWithFixedDelay(
                    new NoopSendTask(), new Date(System.currentTimeMillis() + noopInterval), noopInterval
            );

            // 设置状态。
            startedFlag = true;
        } catch (Exception e) {
            throw new HandlerException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void stop() throws HandlerException {
        lock.lock();
        try {
            if (!startedFlag) {
                return;
            }

            // 日志记录。
            LOGGER.info("FtpHandler 断开连接...");

            // 断开连接 noop 发送计划。
            noopSendTaskFuture.cancel(true);

            // FTP服务器登出，如果遇到异常，则打印异常。
            try {
                ftpClient.logout();
            } catch (Exception e) {
                LOGGER.error("FTP 登出失败", e);
            }

            // 关闭连接。
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }

            // 释放 FTP 客户端。
            ftpClient = null;

            // 设置状态。
            startedFlag = false;
        } catch (Exception e) {
            throw new HandlerException(e);
        } finally {
            lock.unlock();
        }
    }

    @Deprecated
    @BehaviorAnalyse
    @Override
    public void connect() throws HandlerException {
        start();
    }

    @Deprecated
    @BehaviorAnalyse
    @Override
    public void disconnect() throws HandlerException {
        stop();
    }

    @BehaviorAnalyse
    @Override
    public boolean existsFile(String[] filePaths, String fileName) throws FtpException {
        lock.lock();
        try {
            ensureStatus();
            enterDirection(filePaths);
            FTPFile[] ftpFiles = ftpClient.listFiles(fileName);
            return Objects.nonNull(ftpFiles) && ftpFiles.length > 0;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void storeFile(String[] filePaths, String fileName, @SkipRecord byte[] content) throws FtpException {
        lock.lock();
        try (ByteArrayInputStream bin = new ByteArrayInputStream(content)) {
            ensureStatus();
            enterDirection(filePaths);
            if (!ftpClient.storeFile(serverFileNameEncoding(fileName), bin)) {
                throw new FtpFileStoreException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + fileName
                );
            }
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @SkipRecord
    @Override
    public byte[] getFileContent(String[] filePaths, String fileName) throws FtpException {
        lock.lock();
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            ensureStatus();
            enterDirection(filePaths);
            if (!ftpClient.retrieveFile(serverFileNameEncoding(fileName), bout)) {
                throw new FtpFileRetrieveException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + fileName
                );
            }
            bout.flush();
            return bout.toByteArray();
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void storeFileByStream(String[] filePaths, String fileName, @SkipRecord InputStream in)
            throws HandlerException {
        lock.lock();
        try {
            ensureStatus();
            enterDirection(filePaths);
            if (!ftpClient.storeFile(serverFileNameEncoding(fileName), in)) {
                throw new FtpFileStoreException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + fileName
                );
            }
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void getFileContentByStream(String[] filePaths, String fileName, @SkipRecord OutputStream out)
            throws HandlerException {
        lock.lock();
        try {
            ensureStatus();
            enterDirection(filePaths);
            if (!ftpClient.retrieveFile(serverFileNameEncoding(fileName), out)) {
                throw new FtpFileRetrieveException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + fileName
                );
            }
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void deleteFile(String[] filePaths, String fileName) throws FtpException {
        lock.lock();
        try {
            ensureStatus();
            enterDirection(filePaths);
            if (!ftpClient.deleteFile(serverFileNameEncoding(fileName))) {
                throw new FtpFileDeleteException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + fileName
                );
            }
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void removeDirectory(String[] filePaths, String directoryName) throws FtpException {
        lock.lock();
        try {
            enterDirection(filePaths);
            if (!ftpClient.removeDirectory(serverFileNameEncoding(directoryName))) {
                throw new FtpFileDeleteException(
                        humanReadableFileNameEncoding(ftpClient.printWorkingDirectory()) + '/' + directoryName
                );
            }
        } catch (HandlerException e) {
            throw e;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public FtpFile[] listFiles(String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认状态并列出文件。
            ensureStatus();
            enterDirection(filePaths);
            FTPFile[] ftpFiles = ftpClient.listFiles();

            // 映射文件并返回结果。
            FtpFile[] result = new FtpFile[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                // 定义变量。
                String name;
                int type;
                long size;
                // 映射变量。
                name = humanReadableFileNameEncoding(ftpFile.getName());
                switch (ftpFile.getType()) {
                    case FTPFile.FILE_TYPE:
                        type = Constants.FTP_FILE_TYPE_FILE;
                        break;
                    case FTPFile.DIRECTORY_TYPE:
                        type = Constants.FTP_FILE_TYPE_DIRECTORY;
                        break;
                    case FTPFile.SYMBOLIC_LINK_TYPE:
                        type = Constants.FTP_FILE_TYPE_SYMBOLIC_LINK;
                        break;
                    default:
                        type = Constants.FTP_FILE_TYPE_UNKNOWN;
                        break;
                }
                size = ftpFile.getSize();
                // 设置结果。
                result[i] = new FtpFile(name, type, size);
            }
            return result;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public String[] listFileNames(String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认状态并列出文件。
            ensureStatus();
            enterDirection(filePaths);
            FTPFile[] ftpFiles = ftpClient.listFiles();

            // 映射文件并返回结果。
            String[] result = new String[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                result[i] = humanReadableFileNameEncoding(ftpFile.getName());
            }
            return result;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 确保 FTP 的状态正常。
     * <p>
     * 如果未连接成功，则尝试立即连接。连接失败后抛出异常。
     */
    private void ensureStatus() {
        try {
            ftpClient.sendNoOp();
        } catch (IOException e) {
            LOGGER.warn("向 FTP 服务器发送 NoOp 指令失败，异常信息如下: ", e);
            LOGGER.warn("尝试重新连接...");
            try {
                connectAndLogin();
            } catch (Exception ex) {
                LOGGER.warn("重连失败，异常信息如下: ", ex);
            }
        }
    }

    /**
     * 按照 filePaths 依次打开指定的目录，如果目录不存在就创建。
     *
     * @param filePaths 指定的文件目录。
     * @throws IOException IO异常。
     */
    private void enterDirection(String[] filePaths) throws IOException {
        ftpClient.changeWorkingDirectory(serverFileNameEncoding("/"));
        for (String filePath : filePaths) {
            String adjustedFilePath = serverFileNameEncoding(filePath);
            boolean result = ftpClient.changeWorkingDirectory(adjustedFilePath);
            if (!result) {
                ftpClient.mkd(adjustedFilePath);
                ftpClient.changeWorkingDirectory(adjustedFilePath);
            }
        }
    }

    private String serverFileNameEncoding(String fileName) throws UnsupportedEncodingException {
        return new String(fileName.getBytes(serverCharset), STANDARD_FILE_NAME_CHARSET);
    }

    private String humanReadableFileNameEncoding(String fileName) throws UnsupportedEncodingException {
        return new String(fileName.getBytes(STANDARD_FILE_NAME_CHARSET), serverCharset);
    }

    private void connectAndLogin() throws Exception {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }

        // 设置连接超时时间。
        // 连接的超时时间一定要在调用connect方法之前设置。
        ftpClient.setConnectTimeout(connectTimeout);

        // 连接 FTP 服务器,设置IP及端口
        try {
            ftpClient.connect(ftpHost, ftpPort);
        } catch (Exception e) {
            throw new FtpConnectException(e);
        }

        // 设置用户名和密码
        ftpClient.login(ftpUserName, ftpPassword);

        // 设置文件传输为模式为binary。
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        // 检查连接结果，确认连接正常。
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            ftpClient.disconnect();
            throw new FtpLoginException();
        } else {
            LOGGER.info("FTP连接成功");
        }
    }

    private class NoopSendTask implements Runnable {

        @Override
        public void run() {
            lock.lock();
            try {
                LOGGER.debug("向 FTP 服务器 发送 NoOp 指令，以保持 FTP 服务器的正常连接...");
                ftpClient.sendNoOp();
            } catch (IOException e) {
                LOGGER.warn("向 FTP 服务器发送 NoOp 指令失败，异常信息如下: ", e);
                LOGGER.warn("尝试重新连接...");
                try {
                    connectAndLogin();
                } catch (Exception ex) {
                    LOGGER.warn("重连失败，异常信息如下: ", ex);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
