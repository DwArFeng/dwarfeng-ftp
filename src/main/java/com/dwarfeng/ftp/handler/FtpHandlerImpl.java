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
     * 最小超时时间。
     */
    private static final int MIN_CONNECT_TIMEOUT = 1000;

    /**
     * 默认缓冲区大小。
     */
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private final ThreadPoolTaskScheduler scheduler;

    private final String ftpHost;
    private final int ftpPort;
    private final String ftpUserName;
    private final String ftpPassword;
    private final String serverCharset;
    private final int connectTimeout;
    private final long noopInterval;
    private final int bufferSize;

    private final Lock lock = new ReentrantLock();

    private FTPClient ftpClient = null;
    private ScheduledFuture<?> noopSendTaskFuture;
    private boolean startedFlag = false;

    public FtpHandlerImpl(
            ThreadPoolTaskScheduler scheduler, String ftpHost, int ftpPort, String ftpUserName, String ftpPassword,
            String serverCharset, int connectTimeout, long noopInterval
    ) {
        this(
                scheduler, ftpHost, ftpPort, ftpUserName, ftpPassword, serverCharset, connectTimeout, noopInterval,
                DEFAULT_BUFFER_SIZE
        );
    }

    public FtpHandlerImpl(
            ThreadPoolTaskScheduler scheduler, String ftpHost, int ftpPort, String ftpUserName, String ftpPassword,
            String serverCharset, int connectTimeout, long noopInterval, int bufferSize
    ) {
        //检查参数是否合法。
        if (connectTimeout <= MIN_CONNECT_TIMEOUT) {
            throw new IllegalArgumentException("配置ftp.connect_timeout的值太小，应该大于1000");
        }
        if (noopInterval >= connectTimeout) {
            throw new IllegalArgumentException("配置ftp.noop_interval的值太大，应该小于ftp.connect_timeout");
        }

        this.scheduler = scheduler;
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.serverCharset = serverCharset;
        this.connectTimeout = connectTimeout;
        this.noopInterval = noopInterval;
        this.bufferSize = bufferSize;
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

            // 初始化 FTP 客户端。
            ftpClient = new FTPClient();

            // 设置 FTP 客户端的控制编码。
            ftpClient.setControlEncoding(serverCharset);

            // 设置 FTP 客户端的缓冲区大小。
            ftpClient.setBufferSize(bufferSize);

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
            checkPositiveCompletion();
            FTPFile[] ftpFiles = ftpClient.listFiles(fileName);
            checkPositiveCompletion();
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
            checkPositiveCompletion();
            if (!ftpClient.storeFile(fileName, bin)) {
                throw new FtpFileStoreException(ftpClient.printWorkingDirectory() + '/' + fileName);
            }
            checkPositiveCompletion();
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @SkipRecord
    @Override
    public byte[] retrieveFile(String[] filePaths, String fileName) throws FtpException {
        lock.lock();
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            ensureStatus();
            enterDirection(filePaths);
            checkPositiveCompletion();
            if (!ftpClient.retrieveFile(fileName, bout)) {
                throw new FtpFileRetrieveException(ftpClient.printWorkingDirectory() + '/' + fileName);
            }
            checkPositiveCompletion();
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
            checkPositiveCompletion();
            if (!ftpClient.storeFile(fileName, in)) {
                throw new FtpFileStoreException(ftpClient.printWorkingDirectory() + '/' + fileName);
            }
            checkPositiveCompletion();
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void retrieveFileByStream(String[] filePaths, String fileName, @SkipRecord OutputStream out)
            throws HandlerException {
        lock.lock();
        try {
            ensureStatus();
            enterDirection(filePaths);
            checkPositiveCompletion();
            if (!ftpClient.retrieveFile(fileName, out)) {
                throw new FtpFileRetrieveException(ftpClient.printWorkingDirectory() + '/' + fileName);
            }
            checkPositiveCompletion();
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
            checkPositiveCompletion();
            if (!ftpClient.deleteFile(fileName)) {
                throw new FtpFileDeleteException(ftpClient.printWorkingDirectory() + '/' + fileName);
            }
            checkPositiveCompletion();
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
            checkPositiveCompletion();
            if (!ftpClient.removeDirectory(directoryName)) {
                throw new FtpFileDeleteException(ftpClient.printWorkingDirectory() + '/' + directoryName);
            }
            checkPositiveCompletion();
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
            checkPositiveCompletion();
            FTPFile[] ftpFiles = ftpClient.listFiles();
            checkPositiveCompletion();

            // 映射文件并返回结果。
            FtpFile[] result = new FtpFile[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                // 定义变量。
                String name;
                int type;
                long size;
                // 映射变量。
                name = ftpFile.getName();
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
            checkPositiveCompletion();
            FTPFile[] ftpFiles = ftpClient.listFiles();
            checkPositiveCompletion();

            // 映射文件并返回结果。
            String[] result = new String[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                result[i] = ftpFile.getName();
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
    public InputStream openInputStream(String[] filePaths, String fileName) throws HandlerException {
        lock.lock();
        try {
            // 确认状态并打开文件目录。
            ensureStatus();
            enterDirection(filePaths);
            checkPositiveCompletion();

            // 打开文件的输入流。
            InputStream in = ftpClient.retrieveFileStream(fileName);
            checkPositivePreliminary();

            // 包装输入流并返回。
            return new CompletePendingInputStream(in);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public OutputStream openOutputStream(String[] filePaths, String fileName) throws HandlerException {
        lock.lock();
        try {
            // 确认状态并打开文件目录。
            ensureStatus();
            enterDirection(filePaths);
            checkPositiveCompletion();

            // 打开文件的输出流。
            OutputStream out = ftpClient.storeFileStream(fileName);
            checkPositivePreliminary();

            // 包装输出流并返回。
            return new CompletePendingOutputStream(out);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行 FtpClient 具体操作之前确保 FTP 的状态正常。
     *
     * <p>
     * 如果未连接成功，则尝试立即连接。连接失败后抛出异常。
     */
    private void ensureStatus() throws Exception {
        try {
            ftpClient.sendNoOp();
        } catch (IOException e) {
            LOGGER.warn("向 FTP 服务器发送 NoOp 指令失败，异常信息如下: ", e);
            LOGGER.warn("尝试重新连接...");
            try {
                connectAndLogin();
            } catch (Exception ex) {
                LOGGER.warn("重连失败，异常信息如下: ", ex);
                throw ex;
            }
        }
    }

    /**
     * 执行 FtpClient 具体操作之后检查 FTP 的状态。
     *
     * @throws IOException 如果 FTP 服务器返回错误的状态码，则抛出此异常。
     */
    private void checkPositiveCompletion() throws IOException {
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            return;
        }

        throw new IOException("FTP 服务器返回错误的状态码: " + ftpClient.getReplyCode());
    }

    /**
     * 执行 FtpClient 具体操作之后检查 FTP 的状态。
     *
     * @throws IOException 如果 FTP 服务器返回错误的状态码，则抛出此异常。
     */
    private void checkPositivePreliminary() throws IOException {
        if (FTPReply.isPositivePreliminary(ftpClient.getReplyCode())) {
            return;
        }

        throw new IOException("FTP 服务器返回错误的状态码: " + ftpClient.getReplyCode());
    }

    /**
     * 按照 filePaths 依次打开指定的目录，如果目录不存在就创建。
     *
     * @param filePaths 指定的文件目录。
     * @throws IOException IO异常。
     */
    private void enterDirection(String[] filePaths) throws IOException {
        ftpClient.changeWorkingDirectory("/");
        for (String filePath : filePaths) {
            boolean result = ftpClient.changeWorkingDirectory(filePath);
            if (!result) {
                ftpClient.mkd(filePath);
                ftpClient.changeWorkingDirectory(filePath);
            }
        }
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

    private class CompletePendingInputStream extends FilterInputStream {

        public CompletePendingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            // 调用父类的 close 方法，关闭输入流。
            super.close();

            // 根据 FtpClient 的文档，必须调用 completePendingCommand 方法，以完成文件传输。
            if (ftpClient.completePendingCommand()) {
                return;
            }

            // 如果文件传输失败，则主动断开连接。
            // 主动断开连接后，调用其它方法，会自动触发重连机制，所以这里不需要再次重连。
            ftpClient.logout();
            ftpClient.disconnect();
            // 抛出 IOException，以通知上层调用者。
            throw new IOException("completePendingCommand 失败，主动断开连接");
        }
    }

    private class CompletePendingOutputStream extends FilterOutputStream {

        public CompletePendingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() throws IOException {
            // 调用父类的 close 方法，关闭输出流。
            super.close();

            // 根据 FtpClient 的文档，必须调用 completePendingCommand 方法，以完成文件传输。
            if (ftpClient.completePendingCommand()) {
                return;
            }

            // 如果文件传输失败，则主动断开连接。
            // 主动断开连接后，调用其它方法，会自动触发重连机制，所以这里不需要再次重连。
            ftpClient.logout();
            ftpClient.disconnect();
            // 抛出 IOException，以通知上层调用者。
            throw new IOException("completePendingCommand 失败，主动断开连接");
        }
    }
}
