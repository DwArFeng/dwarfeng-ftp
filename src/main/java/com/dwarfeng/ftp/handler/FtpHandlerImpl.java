package com.dwarfeng.ftp.handler;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.exception.*;
import com.dwarfeng.ftp.struct.FtpConfig;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.ftp.util.Constants;
import com.dwarfeng.ftp.util.FtpFileLocationUtil;
import com.dwarfeng.subgrade.sdk.interceptor.analyse.BehaviorAnalyse;
import com.dwarfeng.subgrade.sdk.interceptor.analyse.SkipRecord;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FTP 处理器实现。
 *
 * <p>
 * 该处理器实现基于 Apache Commons Net 的 FTPClient 实现。
 *
 * <p>
 * 该实现是线程安全的，包括 {@link #openInputStream(String[], String)} 和 {@link #openOutputStream(String[], String)}
 * 方法。<br>
 * 除了 {@link #openInputStream(String[], String)} 和 {@link #openOutputStream(String[], String)} 方法以外，
 * 其它方法在调用时会自动加锁，其它线程调用处理器的任何方法都会被阻塞，直到该方法执行完毕。<br>
 * {@link #openInputStream(String[], String)} 和 {@link #openOutputStream(String[], String)} 在调用时会加锁，
 * 但返回结果后不会解锁，直到调用者关闭流或者流被关闭时才会解锁，在这段时间内，其它线程调用处理器的任何方法都会被阻塞。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpHandlerImpl implements FtpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpHandlerImpl.class);

    private static final String ROOT_PATH = "/";
    private static final char PATH_SEPARATOR = '/';

    private final ThreadPoolTaskScheduler scheduler;

    private final FtpConfig config;

    private final Lock lock = new ReentrantLock();

    private FTPClient ftpClient = null;
    private ScheduledFuture<?> noopSendTaskFuture;
    private boolean startedFlag = false;

    /**
     * @deprecated 该构造器已经过时，请使用 {@link #FtpHandlerImpl(ThreadPoolTaskScheduler, FtpConfig)}。
     */
    @Deprecated
    public FtpHandlerImpl(
            ThreadPoolTaskScheduler scheduler, String ftpHost, int ftpPort, String ftpUserName, String ftpPassword,
            String serverCharset, int connectTimeout, long noopInterval
    ) {
        this(
                scheduler,
                new FtpConfig(
                        ftpHost, ftpPort, ftpUserName, ftpPassword, serverCharset, connectTimeout, noopInterval,
                        FtpConfig.Builder.DEFAULT_BUFFER_SIZE, FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH,
                        FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_PREFIX,
                        FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_SUFFIX,
                        FtpConfig.Builder.DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE
                )
        );
    }

    /**
     * @deprecated 该构造器已经过时，请使用 {@link #FtpHandlerImpl(ThreadPoolTaskScheduler, FtpConfig)}。
     */
    @Deprecated
    public FtpHandlerImpl(
            ThreadPoolTaskScheduler scheduler, String ftpHost, int ftpPort, String ftpUserName, String ftpPassword,
            String serverCharset, int connectTimeout, long noopInterval, int bufferSize
    ) {
        this(
                scheduler,
                new FtpConfig(
                        ftpHost, ftpPort, ftpUserName, ftpPassword, serverCharset, connectTimeout, noopInterval,
                        bufferSize, FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH,
                        FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_PREFIX,
                        FtpConfig.Builder.DEFAULT_TEMPORARY_FILE_SUFFIX,
                        FtpConfig.Builder.DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE
                )
        );
    }

    public FtpHandlerImpl(@Nonnull ThreadPoolTaskScheduler scheduler, @Nonnull FtpConfig config) {
        this.scheduler = scheduler;
        this.config = config;
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
            ftpClient.setControlEncoding(config.getServerCharset());

            // 设置 FTP 客户端的缓冲区大小。
            ftpClient.setBufferSize(config.getBufferSize());

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
                    new NoopSendTask(), new Date(System.currentTimeMillis() + config.getNoopInterval()),
                    config.getNoopInterval()
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
    public boolean existsFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws FtpException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalExistsFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public boolean existsFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作，并返回结果。
            return internalExistsFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private boolean internalExistsFile(String[] filePaths, String fileName) throws Exception {
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();
        FTPFile[] ftpFiles = ftpClient.listFiles(fileName);
        boolean existsFlag = Objects.nonNull(ftpFiles) && ftpFiles.length > 0;
        /*
         * 如果文件存在，检查 FTP 的状态；文件不存在时，不检查 FTP 的状态。
         * 这是因为部分 FTP 服务器找不到文件时，会返回非 positiveCompletion 的代码。
         * 已知会返回非 positiveCompletion 的 FTP 服务器清单：
         *   1. Windows 内置服务器，找不到文件时，返回代码 550。
         */
        if (existsFlag) {
            checkPositiveCompletion();
        }
        return existsFlag;
    }

    @BehaviorAnalyse
    @Override
    public void storeFile(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull @SkipRecord byte[] content
    ) throws FtpException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalStoreFile(filePaths, fileName, content);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void storeFile(@Nonnull FtpFileLocation fileLocation, @Nonnull @SkipRecord byte[] content)
            throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作。
            internalStoreFile(filePaths, fileName, content);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalStoreFile(String[] filePaths, String fileName, byte[] content) throws Exception {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(content)) {
            internalStoreFileByStream(filePaths, fileName, bin);
        }
    }

    @BehaviorAnalyse
    @SkipRecord
    @Override
    public byte[] retrieveFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws FtpException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalRetrieveFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @SkipRecord
    @Override
    public byte[] retrieveFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作，并返回结果。
            return internalRetrieveFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private byte[] internalRetrieveFile(String[] filePaths, String fileName) throws Exception {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
            internalRetrieveFileByStream(filePaths, fileName, bout);
            bout.flush();
            return bout.toByteArray();
        }
    }

    @BehaviorAnalyse
    @Override
    public void storeFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull @SkipRecord InputStream in
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalStoreFileByStream(filePaths, fileName, in);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void storeFileByStream(
            @Nonnull FtpFileLocation fileLocation, @Nonnull @SkipRecord InputStream in
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作。
            internalStoreFileByStream(filePaths, fileName, in);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalStoreFileByStream(String[] filePaths, String fileName, InputStream in) throws Exception {
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();
        if (!ftpClient.storeFile(fileName, in)) {
            throw new FtpFileStoreException(resolveAbsolutePath(filePaths, fileName));
        }
        checkPositiveCompletion();
    }

    @BehaviorAnalyse
    @Override
    public void retrieveFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull @SkipRecord OutputStream out
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalRetrieveFileByStream(filePaths, fileName, out);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void retrieveFileByStream(
            @Nonnull FtpFileLocation fileLocation, @Nonnull @SkipRecord OutputStream out
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作。
            internalRetrieveFileByStream(filePaths, fileName, out);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalRetrieveFileByStream(String[] filePaths, String fileName, OutputStream out) throws Exception {
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();
        if (!ftpClient.retrieveFile(fileName, out)) {
            throw new FtpFileRetrieveException(resolveAbsolutePath(filePaths, fileName));
        }
        checkPositiveCompletion();
    }

    @BehaviorAnalyse
    @Override
    public void deleteFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws FtpException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalDeleteFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void deleteFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作。
            internalDeleteFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalDeleteFile(String[] filePaths, String fileName) throws Exception {
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();
        if (!ftpClient.deleteFile(fileName)) {
            throw new FtpFileDeleteException(resolveAbsolutePath(filePaths, fileName));
        }
        checkPositiveCompletion();
    }

    @BehaviorAnalyse
    @Override
    public void removeDirectory(@Nonnull String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalRemoveDirectory(filePaths);
        } catch (HandlerException e) {
            throw e;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @BehaviorAnalyse
    @Override
    public void removeDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            // 执行操作。
            internalRemoveDirectory(filePaths);
        } catch (HandlerException e) {
            throw e;
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalRemoveDirectory(String[] filePaths) throws Exception {
        // 如果目录为空，则直接抛出异常（不能删除根目录）。
        if (filePaths.length == 0) {
            throw new FtpFileDeleteException(resolveAbsolutePath(filePaths, null));
        }

        // 如果目录不为空，则获取父目录路径。
        String[] parentFilePaths = new String[filePaths.length - 1];
        System.arraycopy(filePaths, 0, parentFilePaths, 0, parentFilePaths.length);

        // 确认状态并打开文件目录。
        ensureStatus();
        enterDirection(parentFilePaths);
        checkPositiveCompletion();

        // 删除文件目录。
        if (!ftpClient.removeDirectory(filePaths[filePaths.length - 1])) {
            throw new FtpFileDeleteException(resolveAbsolutePath(filePaths, null));
        }
        checkPositiveCompletion();
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public FtpFile[] listFiles(@Nonnull String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalListFile(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public FtpFile[] listFiles(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            // 执行操作，并返回结果。
            return internalListFile(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private FtpFile[] internalListFile(String[] filePaths) throws Exception {
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
            result[i] = apacheFtpFileToDwarfengFtpFile(ftpFile);
        }
        return result;
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public String[] listFileNames(@Nonnull String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalListFileNames(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public String[] listFileNames(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            // 执行操作，并返回结果。
            return internalListFileNames(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private String[] internalListFileNames(String[] filePaths) throws Exception {
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
    }

    /**
     * 打开指定文件的输入流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法在调用时会加锁，但返回结果后不会解锁，直到调用者关闭流或者流被关闭时才会解锁，在这段时间内，
     * 其它线程调用处理器的任何方法都会被阻塞。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     */
    @Override
    @BehaviorAnalyse
    @SkipRecord
    public InputStream openInputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalOpenInputStream(filePaths, fileName);
        } catch (Exception e) {
            lock.unlock();
            throw new FtpStreamOpenException(e);
        }
    }

    /**
     * @see #openInputStream(String[], String)
     */
    @Override
    @BehaviorAnalyse
    @SkipRecord
    public InputStream openInputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作，并返回结果。
            return internalOpenInputStream(filePaths, fileName);
        } catch (Exception e) {
            lock.unlock();
            throw new FtpStreamOpenException(e);
        }
    }

    private CompletePendingInputStream internalOpenInputStream(String[] filePaths, String fileName) throws Exception {
        // 确认状态并打开文件目录。
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();

        // 打开文件的输入流。
        InputStream in = ftpClient.retrieveFileStream(fileName);
        checkPositivePreliminary();

        // 包装输入流并返回。
        return new CompletePendingInputStream(in);
    }

    /**
     * 打开指定文件的输出流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法在调用时会加锁，但返回结果后不会解锁，直到调用者关闭流或者流被关闭时才会解锁，在这段时间内，
     * 其它线程调用处理器的任何方法都会被阻塞。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     */
    @Override
    @BehaviorAnalyse
    @SkipRecord
    public OutputStream openOutputStream(@Nonnull String[] filePaths, @Nonnull String fileName)
            throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalOpenOutputStream(filePaths, fileName);
        } catch (Exception e) {
            lock.unlock();
            throw new FtpStreamOpenException(e);
        }
    }

    /**
     * @see #openOutputStream(String[], String)
     */
    @Override
    public OutputStream openOutputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作，并返回结果。
            return internalOpenOutputStream(filePaths, fileName);
        } catch (Exception e) {
            lock.unlock();
            throw new FtpStreamOpenException(e);
        }
    }

    private CompletePendingOutputStream internalOpenOutputStream(String[] filePaths, String fileName)
            throws Exception {
        // 确认状态并打开文件目录。
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();

        // 打开文件的输出流。
        OutputStream out = ftpClient.storeFileStream(fileName);
        checkPositivePreliminary();

        // 包装输出流并返回。
        return new CompletePendingOutputStream(out);
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public void renameFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName, @Nonnull String[] neoFilePaths,
            @Nonnull String neoFileName
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalRenameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    @SkipRecord
    public void renameFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(oldFileLocation);
            FtpFileLocationUtil.checkAsFile(neoFileLocation);
            // 展开参数。
            String[] oldFilePaths = oldFileLocation.getFilePaths();
            String oldFileName = oldFileLocation.getFileName();
            String[] neoFilePaths = neoFileLocation.getFilePaths();
            String neoFileName = neoFileLocation.getFileName();
            // 执行操作。
            internalRenameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalRenameFile(
            String[] oldFilePaths, String oldFileName, String[] neoFilePaths, String neoFileName
    ) throws Exception {
        // 确认状态。
        ensureStatus();

        // 确保旧文件存在。
        if (!internalExistsFile(oldFilePaths, oldFileName)) {
            throw new FtpFileNotExistsException(resolveAbsolutePath(oldFilePaths, oldFileName));
        }

        // 如果新文件存在，则删除新文件。
        if (internalExistsFile(neoFilePaths, neoFileName)) {
            internalDeleteFile(neoFilePaths, neoFileName);
        }

        // 执行重命名操作。
        ftpClient.rename(
                resolveAbsolutePath(oldFilePaths, oldFileName),
                resolveAbsolutePath(neoFilePaths, neoFileName)
        );
        checkPositiveCompletion();
    }

    @Override
    @BehaviorAnalyse
    public void clearDirectory(@Nonnull String[] filePaths) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalClearDirectory(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    public void clearDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            // 执行操作。
            internalClearDirectory(filePaths);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalClearDirectory(String[] filePaths) throws Exception {
        // 确认状态并列出文件。
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();

        FTPFile[] ftpFiles = ftpClient.listFiles();
        checkPositiveCompletion();

        // 特殊情形：如果目录为空，则直接返回。
        if (Objects.isNull(ftpFiles) || ftpFiles.length == 0) {
            return;
        }

        // 定义帧栈，并初始化。
        Stack<DirectoryClearFrame> frameStack = new Stack<>();
        DirectoryClearFrame initialFrame = new DirectoryClearFrame(filePaths, new ArrayDeque<>());
        initialFrame.getRemainingFiles().addAll(Arrays.asList(ftpFiles));
        frameStack.push(initialFrame);

        // 只要帧栈不为空，就一直循环。
        while (!frameStack.isEmpty()) {
            // 弹出栈顶帧。
            DirectoryClearFrame frame = frameStack.pop();
            // 清理单帧。
            clearSingleFrame(filePaths, frame, frameStack);
        }
    }

    @Override
    @BehaviorAnalyse
    public void copyFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            internalCopyFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    public void copyFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(oldFileLocation);
            FtpFileLocationUtil.checkAsFile(neoFileLocation);
            // 展开参数。
            String[] oldFilePaths = oldFileLocation.getFilePaths();
            String oldFileName = oldFileLocation.getFileName();
            String[] neoFilePaths = neoFileLocation.getFilePaths();
            String neoFileName = neoFileLocation.getFileName();
            // 执行操作。
            internalCopyFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private void internalCopyFile(
            String[] oldFilePaths, String oldFileName, String[] neoFilePaths, String neoFileName
    ) throws Exception {
        // 新建文件复制临时存储。
        FileCopyTemporaryStorage temporaryStorage = new FileCopyTemporaryStorage(
                config.getFileCopyMemoryBufferSize(), config.getTemporaryFileDirectoryPath(),
                config.getTemporaryFilePrefix(), config.getTemporaryFileSuffix()
        );
        // 读旧文件。
        try (OutputStream out = temporaryStorage.openOutputStream()) {
            ensureStatus();
            enterDirection(oldFilePaths);
            checkPositiveCompletion();
            if (!ftpClient.retrieveFile(oldFileName, out)) {
                throw new FtpFileRetrieveException(resolveAbsolutePath(oldFilePaths, oldFileName));
            }
            checkPositiveCompletion();
        }
        // 写新文件。
        try (InputStream in = temporaryStorage.openInputStream()) {
            ensureStatus();
            enterDirection(neoFilePaths);
            checkPositiveCompletion();
            if (!ftpClient.storeFile(neoFileName, in)) {
                throw new FtpFileStoreException(resolveAbsolutePath(neoFilePaths, neoFileName));
            }
            checkPositiveCompletion();
        }
        // 释放资源。
        temporaryStorage.dispose();
    }

    @Override
    @BehaviorAnalyse
    public FtpFile descFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            return internalDescFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @BehaviorAnalyse
    public FtpFile descFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException {
        lock.lock();
        try {
            // 确认处理器已经启动。
            makeSureHandlerStart();
            // 校验参数。
            FtpFileLocationUtil.checkAsFile(fileLocation);
            // 展开参数。
            String[] filePaths = fileLocation.getFilePaths();
            String fileName = fileLocation.getFileName();
            // 执行操作，并返回结果。
            return internalDescFile(filePaths, fileName);
        } catch (Exception e) {
            throw new FtpException(e);
        } finally {
            lock.unlock();
        }
    }

    private FtpFile internalDescFile(String[] filePaths, String fileName) throws Exception {
        ensureStatus();
        enterDirection(filePaths);
        checkPositiveCompletion();
        FTPFile ftpFile = Arrays.stream(
                ftpClient.listFiles(null, f -> f.getName().equals(fileName))
        ).findFirst().orElse(null);
        if (Objects.isNull(ftpFile)) {
            throw new FtpFileNotExistsException(resolveAbsolutePath(filePaths, fileName));
        }
        checkPositiveCompletion();
        return apacheFtpFileToDwarfengFtpFile(ftpFile);
    }

    private void clearSingleFrame(String[] filePaths, DirectoryClearFrame frame, Stack<DirectoryClearFrame> frameStack)
            throws Exception {
        // 展开参数。
        String[] frameFilePaths = frame.getFilePaths();
        Queue<FTPFile> frameRemainingFiles = frame.getRemainingFiles();
        // 进入文件夹。
        enterDirection(frameFilePaths);
        checkPositiveCompletion();
        // 只要剩余文件队列不为空，就一直循环。
        while (!frameRemainingFiles.isEmpty()) {
            // 弹出队首文件。
            FTPFile ftpFile = frameRemainingFiles.poll();
            // 如果是文件夹，则进入文件夹。
            if (ftpFile.isDirectory()) {
                // 列出文件。
                FTPFile[] neoFtpFiles = ftpClient.listFiles(ftpFile.getName());
                checkPositiveCompletion();
                // 如果文件夹为空，则直接删除目录并返回。
                if (Objects.isNull(neoFtpFiles) || neoFtpFiles.length == 0) {
                    if (!ftpClient.removeDirectory(ftpFile.getName())) {
                        throw new FtpFileDeleteException(resolveAbsolutePath(frameFilePaths, ftpFile.getName()));
                    }
                    continue;
                }
                // 创建新帧。
                String[] newFilePaths = Arrays.copyOf(frameFilePaths, frameFilePaths.length + 1);
                newFilePaths[frameFilePaths.length] = ftpFile.getName();
                DirectoryClearFrame neoFrame = new DirectoryClearFrame(newFilePaths, new ArrayDeque<>());
                neoFrame.getRemainingFiles().addAll(Arrays.asList(neoFtpFiles));
                frameStack.push(frame);
                frameStack.push(neoFrame);
                return;
            }
            // 如果是文件，则删除文件。
            if (!ftpClient.deleteFile(ftpFile.getName())) {
                throw new FtpFileDeleteException(resolveAbsolutePath(frameFilePaths, ftpFile.getName()));
            }
            checkPositiveCompletion();
        }
        // 剩余文件删除完毕后，删除帧对应的目录（如果不是 filePaths）。
        // 如果 frameFilePaths 与 filePaths 相等，则不删除
        if (Arrays.equals(frameFilePaths, filePaths)) {
            return;
        }
        // 如果目录为空，则直接抛出异常（不能删除根目录）。
        if (frameFilePaths.length == 0) {
            throw new FtpFileDeleteException(resolveAbsolutePath(frameFilePaths, null));
        }
        // 如果目录不为空，则获取父目录路径。
        String[] frameParentFilePaths = new String[frameFilePaths.length - 1];
        System.arraycopy(frameFilePaths, 0, frameParentFilePaths, 0, frameParentFilePaths.length);
        // 打开文件目录。
        enterDirection(frameParentFilePaths);
        checkPositiveCompletion();
        // 删除文件目录。
        if (!ftpClient.removeDirectory(frameFilePaths[frameFilePaths.length - 1])) {
            throw new FtpFileDeleteException(resolveAbsolutePath(frameFilePaths, null));
        }
        checkPositiveCompletion();
    }

    private String resolveAbsolutePath(@Nonnull String[] filePaths, @Nullable String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_PATH);
        for (String filePath : filePaths) {
            builder.append(filePath).append(PATH_SEPARATOR);
        }
        if (Objects.nonNull(fileName)) {
            builder.append(fileName);
        }
        return builder.toString();
    }

    private FtpFile apacheFtpFileToDwarfengFtpFile(FTPFile ftpFile) {
        // 特殊值判断。
        if (Objects.isNull(ftpFile)) {
            return null;
        }
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
        return new FtpFile(name, type, size);
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
        ftpClient.changeWorkingDirectory(ROOT_PATH);
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
        ftpClient.setConnectTimeout(config.getConnectTimeout());

        // 连接 FTP 服务器,设置IP及端口
        try {
            ftpClient.connect(config.getHost(), config.getPort());
        } catch (Exception e) {
            throw new FtpConnectException(e);
        }

        // 设置用户名和密码
        ftpClient.login(config.getUsername(), config.getPassword());

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

    private void makeSureHandlerStart() throws HandlerException {
        if (!startedFlag) {
            throw new FtpHandlerStoppedException();
        }
    }

    private static class DirectoryClearFrame {

        private final String[] filePaths;
        private final Queue<FTPFile> remainingFiles;

        public DirectoryClearFrame(String[] filePaths, Queue<FTPFile> remainingFiles) {
            this.filePaths = filePaths;
            this.remainingFiles = remainingFiles;
        }

        public String[] getFilePaths() {
            return filePaths;
        }

        public Queue<FTPFile> getRemainingFiles() {
            return remainingFiles;
        }

        @Override
        public String toString() {
            return "DirectoryClearFrame{" +
                    "filePaths=" + Arrays.toString(filePaths) +
                    ", remainingFiles=" + remainingFiles +
                    '}';
        }
    }

    private static class FileCopyTemporaryStorage {

        public byte[] memoryBuffer;
        public File fileBuffer;

        public int memoryBufferActualLength = 0;
        public boolean fileBufferUsed = false;

        private FileCopyTemporaryStorage(
                int memoryBufferMaxSize, String temporaryFileDirectoryPath, String temporaryFilePrefix,
                String temporaryFileSuffix
        ) {
            this.memoryBuffer = new byte[memoryBufferMaxSize];
            this.fileBuffer = new File(
                    temporaryFileDirectoryPath, temporaryFilePrefix + UUID.randomUUID() + temporaryFileSuffix
            );
            fileBuffer.deleteOnExit();
        }

        public InputStream openInputStream() {
            return new FileCopyTemporaryStorageInputStream(this);
        }

        public OutputStream openOutputStream() {
            return new FileCopyTemporaryStorageOutputStream(this);
        }

        public void dispose() {
            // 释放内存缓冲区。
            memoryBufferActualLength = 0;
            memoryBuffer = null;
            // 删除文件缓冲区。
            fileBufferUsed = false;
            if (Objects.nonNull(fileBuffer) && fileBuffer.exists()) {
                if (!fileBuffer.delete()) {
                    LOGGER.warn("删除文件缓冲区失败: {}", fileBuffer.getAbsolutePath());
                }
                fileBuffer = null;
            }
        }
    }

    private static class FileCopyTemporaryStorageInputStream extends InputStream {

        private final FileCopyTemporaryStorage temporaryStorage;

        private int memoryBufferAnchorIndex = 0;
        private InputStream fileBufferInputStream;

        public FileCopyTemporaryStorageInputStream(FileCopyTemporaryStorage temporaryStorage) {
            this.temporaryStorage = temporaryStorage;
        }

        @Override
        public int available() throws IOException {
            // 如果 temporaryStorage.memoryBuffer 中没有数据了：
            if (memoryBufferAnchorIndex >= temporaryStorage.memoryBufferActualLength) {
                // 如果 fileBuffer 已经被使用，则直接返回 fileBufferInputStream 的 available 方法的返回值。
                if (temporaryStorage.fileBufferUsed) {
                    mayOpenFileBufferInputStream();
                    return fileBufferInputStream.available();
                }
                // 否则，返回 0。
                return 0;
            }
            // 如果 temporaryStorage.memoryBuffer 中还有数据：
            // 计算剩余数据长度。
            int memoryBufferRemainingLength = temporaryStorage.memoryBufferActualLength - memoryBufferAnchorIndex;
            // 如果 fileBuffer 已经被使用，则返回 fileBuffer 的可用长度 + temporaryStorage.memoryBuffer 的剩余长度。
            if (temporaryStorage.fileBufferUsed) {
                mayOpenFileBufferInputStream();
                return fileBufferInputStream.available() + memoryBufferRemainingLength;
            }
            // 如果 fileBuffer 没有被使用，则返回 temporaryStorage.memoryBuffer 的剩余长度。
            return memoryBufferRemainingLength;
        }

        @Override
        public int read(@Nonnull byte[] b, int off, int len) throws IOException {
            return internalRead(b, off, len);
        }

        @Override
        public int read(@Nonnull byte[] b) throws IOException {
            return internalRead(b, 0, b.length);
        }

        private int internalRead(byte[] b, int i, int len) throws IOException {
            // 如果 temporaryStorage.memoryBuffer 中没有数据了：
            if (memoryBufferAnchorIndex >= temporaryStorage.memoryBufferActualLength) {
                // 如果 fileBuffer 已经被使用，则直接从 fileBuffer 读取。
                if (temporaryStorage.fileBufferUsed) {
                    mayOpenFileBufferInputStream();
                    return fileBufferInputStream.read(b, i, len);
                }
                // 否则，读取结束。
                return -1;
            }
            // 如果 temporaryStorage.memoryBuffer 中还有数据：
            // 计算剩余数据长度。
            int memoryBufferRemainingLength = temporaryStorage.memoryBufferActualLength - memoryBufferAnchorIndex;
            // 如果剩余数据长度大于等于 len，则直接从 temporaryStorage.memoryBuffer 读取。
            if (memoryBufferRemainingLength >= len) {
                System.arraycopy(temporaryStorage.memoryBuffer, memoryBufferAnchorIndex, b, i, len);
                memoryBufferAnchorIndex += len;
                return len;
            }
            // 如果剩余长度小于 len：
            // 如果 fileBuffer 已经被使用：
            if (temporaryStorage.fileBufferUsed) {
                // 将 temporaryStorage.memoryBuffer 中的数据读取完毕，之后再从 fileBuffer 读取剩余部分。
                System.arraycopy(
                        temporaryStorage.memoryBuffer, memoryBufferAnchorIndex, b, i, memoryBufferRemainingLength
                );
                memoryBufferAnchorIndex = temporaryStorage.memoryBufferActualLength;
                mayOpenFileBufferInputStream();
                return memoryBufferRemainingLength + fileBufferInputStream.read(
                        b, i + memoryBufferRemainingLength, len - memoryBufferRemainingLength
                );
            }
            // 如果 fileBuffer 没有被使用，则直接从 temporaryStorage.memoryBuffer 读取剩余部分，返回真实的读取长度。
            System.arraycopy(
                    temporaryStorage.memoryBuffer, memoryBufferAnchorIndex, b, i, memoryBufferRemainingLength
            );
            memoryBufferAnchorIndex = temporaryStorage.memoryBufferActualLength;
            return memoryBufferRemainingLength;
        }

        @Override
        public int read() throws IOException {
            // 如果 temporaryStorage.memoryBuffer 中没有数据了：
            if (memoryBufferAnchorIndex >= temporaryStorage.memoryBufferActualLength) {
                // 如果 fileBuffer 已经被使用，则直接从 fileBuffer 读取。
                if (temporaryStorage.fileBufferUsed) {
                    mayOpenFileBufferInputStream();
                    return fileBufferInputStream.read();
                }
                // 否则，读取结束。
                return -1;
            }
            // 如果 temporaryStorage.memoryBuffer 中还有数据：
            return temporaryStorage.memoryBuffer[memoryBufferAnchorIndex++] & 0xFF;
        }

        @Override
        public long skip(long n) throws IOException {
            // 如果 temporaryStorage.memoryBuffer 中没有数据了：
            if (memoryBufferAnchorIndex >= temporaryStorage.memoryBufferActualLength) {
                // 如果 fileBuffer 已经被使用，则直接从 fileBuffer 跳过。
                if (temporaryStorage.fileBufferUsed) {
                    mayOpenFileBufferInputStream();
                    return fileBufferInputStream.skip(n);
                }
                // 否则，跳过结束。
                return 0;
            }
            // 如果 temporaryStorage.memoryBuffer 中还有数据：
            // 计算剩余数据长度。
            int memoryBufferRemainingLength = temporaryStorage.memoryBufferActualLength - memoryBufferAnchorIndex;
            // 如果剩余数据长度大于等于 n，则直接从 temporaryStorage.memoryBuffer 跳过。
            if (memoryBufferRemainingLength >= n) {
                memoryBufferAnchorIndex += (int) n;
                return n;
            }
            // 如果剩余长度小于 n：
            // 如果 fileBuffer 已经被使用：
            if (temporaryStorage.fileBufferUsed) {
                // 将 temporaryStorage.memoryBuffer 中的数据跳过完毕，之后再从 fileBuffer 跳过剩余部分。
                memoryBufferAnchorIndex = temporaryStorage.memoryBufferActualLength;
                mayOpenFileBufferInputStream();
                return memoryBufferRemainingLength + fileBufferInputStream.skip(n - memoryBufferRemainingLength);
            }
            // 如果 fileBuffer 没有被使用，则直接从 temporaryStorage.memoryBuffer 跳过剩余部分，返回真实的跳过长度。
            memoryBufferAnchorIndex = temporaryStorage.memoryBufferActualLength;
            return memoryBufferRemainingLength;
        }

        @Override
        public void close() throws IOException {
            // 根据情况关闭文件缓冲区输入流。
            try {
                mayCloseFileBufferInputStream();
            } catch (Exception e) {
                LOGGER.debug("关闭文件缓冲区输入流时发生异常, 将抛出异常...");
                throw new IOException("关闭文件缓冲区输入流时发生异常", e);
            }
        }

        private void mayOpenFileBufferInputStream() throws IOException {
            if (Objects.nonNull(fileBufferInputStream)) {
                return;
            }
            fileBufferInputStream = Files.newInputStream(
                    temporaryStorage.fileBuffer.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ
            );
        }

        private void mayCloseFileBufferInputStream() throws Exception {
            if (Objects.isNull(fileBufferInputStream)) {
                return;
            }
            fileBufferInputStream.close();
            fileBufferInputStream = null;
        }
    }

    private static class FileCopyTemporaryStorageOutputStream extends OutputStream {

        private final FileCopyTemporaryStorage temporaryStorage;

        private OutputStream fileBufferOutputStream;

        public FileCopyTemporaryStorageOutputStream(FileCopyTemporaryStorage temporaryStorage) {
            this.temporaryStorage = temporaryStorage;
        }

        @Override
        public void write(@Nonnull byte[] b, int off, int len) throws IOException {
            internalWrite(b, off, len);
        }

        @Override
        public void write(@Nonnull byte[] b) throws IOException {
            internalWrite(b, 0, b.length);
        }

        private void internalWrite(byte[] b, int off, int len) throws IOException {
            // 特殊值判断：如果 len 为 0，则直接返回。
            if (len == 0) {
                return;
            }
            // 如果 temporaryStorage.memoryBuffer 已经被写满了：
            if (temporaryStorage.memoryBufferActualLength >= temporaryStorage.memoryBuffer.length) {
                // 置位 temporaryStorage.fileBufferUsed 标志。
                temporaryStorage.fileBufferUsed = true;
                // 将数据写入 fileBuffer。
                mayOpenFileBufferOutputStream();
                fileBufferOutputStream.write(b, off, len);
                return;
            }
            // 如果 temporaryStorage.memoryBuffer 还有剩余空间：
            // 计算 temporaryStorage.memoryBuffer 剩余空间。
            int memoryBufferRemainingLength =
                    temporaryStorage.memoryBuffer.length - temporaryStorage.memoryBufferActualLength;
            // 如果剩余空间大于等于 len，则直接写入 temporaryStorage.memoryBuffer。
            if (memoryBufferRemainingLength >= len) {
                System.arraycopy(
                        b, off, temporaryStorage.memoryBuffer, temporaryStorage.memoryBufferActualLength, len
                );
                temporaryStorage.memoryBufferActualLength += len;
                return;
            }
            // 如果剩余空间小于 len：
            // 先将 temporaryStorage.memoryBuffer 写满。
            System.arraycopy(
                    b, off, temporaryStorage.memoryBuffer, temporaryStorage.memoryBufferActualLength,
                    memoryBufferRemainingLength
            );
            temporaryStorage.memoryBufferActualLength = temporaryStorage.memoryBuffer.length;
            // 置位 temporaryStorage.fileBufferUsed 标志。
            temporaryStorage.fileBufferUsed = true;
            // 将剩余数据写入 fileBuffer。
            mayOpenFileBufferOutputStream();
            fileBufferOutputStream.write(b, off + memoryBufferRemainingLength, len - memoryBufferRemainingLength);
        }

        @Override
        public void write(int b) throws IOException {
            // 如果 temporaryStorage.memoryBuffer 已经被写满了：
            if (temporaryStorage.memoryBufferActualLength >= temporaryStorage.memoryBuffer.length) {
                // 置位 temporaryStorage.fileBufferUsed 标志。
                temporaryStorage.fileBufferUsed = true;
                // 将数据写入 fileBuffer。
                mayOpenFileBufferOutputStream();
                fileBufferOutputStream.write(b);
                return;
            }
            // 如果 temporaryStorage.memoryBuffer 还有剩余空间：
            temporaryStorage.memoryBuffer[temporaryStorage.memoryBufferActualLength] = (byte) b;
        }

        @Override
        public void flush() throws IOException {
            // 如果没有使用 fileBuffer，则直接返回。
            if (!temporaryStorage.fileBufferUsed) {
                return;
            }
            // 如果使用了 fileBuffer，则 flush fileBuffer。
            mayOpenFileBufferOutputStream();
            fileBufferOutputStream.flush();
        }

        @Override
        public void close() throws IOException {
            // 根据情况关闭文件缓冲区输出流。
            try {
                mayCloseFileBufferOutputStream();
            } catch (Exception e) {
                LOGGER.debug("关闭文件缓冲区输出流时发生异常, 将抛出异常...");
                throw new IOException("关闭文件缓冲区输出流时发生异常", e);
            }
        }

        private void mayOpenFileBufferOutputStream() throws IOException {
            if (Objects.nonNull(fileBufferOutputStream)) {
                return;
            }
            fileBufferOutputStream = Files.newOutputStream(
                    temporaryStorage.fileBuffer.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        private void mayCloseFileBufferOutputStream() throws IOException {
            if (Objects.isNull(fileBufferOutputStream)) {
                return;
            }
            fileBufferOutputStream.close();
            fileBufferOutputStream = null;
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

    private class CompletePendingInputStream extends InputStream {

        private final InputStream in;

        private boolean closed = false;

        public CompletePendingInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            makeSureOpen("流已经关闭");
            return in.read();
        }

        @Override
        public int read(@Nonnull byte[] b) throws IOException {
            makeSureOpen("流已经关闭");
            return in.read(b);
        }

        @Override
        public int read(@Nonnull byte[] b, int off, int len) throws IOException {
            makeSureOpen("流已经关闭");
            return in.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            makeSureOpen("流已经关闭");
            return in.skip(n);
        }

        @Override
        public int available() throws IOException {
            makeSureOpen("流已经关闭");
            return in.available();
        }

        @Override
        public void mark(int readlimit) {
            makeSureOpen("流已经关闭");
            in.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            makeSureOpen("流已经关闭");
            in.reset();
        }

        @Override
        public boolean markSupported() {
            makeSureOpen("流已经关闭");
            return in.markSupported();
        }

        @SuppressWarnings("DuplicatedCode")
        @Override
        public void close() throws IOException {
            makeSureOpen("不能多次关闭流");

            // 关闭 FtpClient 原生输入流。
            try {
                in.close();
            } catch (Exception e) {
                LOGGER.debug("关闭 FtpClient 原生输入流时发生异常, 将主动断开连接, 并抛出异常...");
                noThrowingDisconnectFtp();
                closed = true;
                lock.unlock();
                throw new IOException("关闭 FtpClient 原生输入流时发生异常", e);
            }

            // 根据 FtpClient 的文档，必须调用 completePendingCommand 方法，以完成文件传输。
            try {
                if (ftpClient.completePendingCommand()) {
                    closed = true;
                    lock.unlock();
                    return;
                }
            } catch (Exception e) {
                LOGGER.debug("调用 FtpClient 的 completePendingCommand 方法时发生异常, 将主动断开连接, 并抛出异常...");
                noThrowingDisconnectFtp();
                closed = true;
                lock.unlock();
                throw new IOException("调用 FtpClient 的 completePendingCommand 方法时发生异常", e);
            }

            // ftpClient.completePendingCommand 返回 false，说明文件传输失败，则主动断开连接。
            LOGGER.debug("ftpClient.completePendingCommand 返回 false, 文件传输失败, 将主动断开连接, 并抛出异常...");
            // 主动断开连接后，调用其它方法，会自动触发重连机制，所以不需要再次重连。
            noThrowingDisconnectFtp();
            closed = true;
            lock.unlock();
            // 抛出 IOException，以通知上层调用者。
            throw new IOException("ftpClient.completePendingCommand 返回 false, 文件传输失败");
        }

        private void noThrowingDisconnectFtp() {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (Exception e) {
                LOGGER.warn("FtpClient 登出或断开连接时发生异常, 异常信息如下: ", e);
            }
        }

        private void makeSureOpen(String exceptionMessage) throws IllegalStateException {
            if (closed) {
                throw new IllegalStateException(exceptionMessage);
            }
        }
    }

    private class CompletePendingOutputStream extends OutputStream {

        private final OutputStream out;

        private boolean closed = false;

        public CompletePendingOutputStream(OutputStream out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            makeSureOpen("流已经关闭");
            out.write(b);
        }

        @Override
        public void write(@Nonnull byte[] b) throws IOException {
            makeSureOpen("流已经关闭");
            out.write(b);
        }

        @Override
        public void write(@Nonnull byte[] b, int off, int len) throws IOException {
            makeSureOpen("流已经关闭");
            out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            makeSureOpen("流已经关闭");
            out.flush();
        }

        @SuppressWarnings("DuplicatedCode")
        @Override
        public void close() throws IOException {
            makeSureOpen("不能多次关闭流");

            // 关闭 FtpClient 原生输出流。
            try {
                out.close();
            } catch (Exception e) {
                LOGGER.debug("关闭 FtpClient 原生输出流时发生异常, 将主动断开连接, 并抛出异常...");
                noThrowingDisconnectFtp();
                closed = true;
                lock.unlock();
                throw new IOException("关闭 FtpClient 原生输出流时发生异常", e);
            }

            // 根据 FtpClient 的文档，必须调用 completePendingCommand 方法，以完成文件传输。
            try {
                if (ftpClient.completePendingCommand()) {
                    closed = true;
                    lock.unlock();
                    return;
                }
            } catch (Exception e) {
                LOGGER.debug("调用 FtpClient 的 completePendingCommand 方法时发生异常, 将主动断开连接, 并抛出异常...");
                noThrowingDisconnectFtp();
                closed = true;
                lock.unlock();
                throw new IOException("调用 FtpClient 的 completePendingCommand 方法时发生异常", e);
            }

            // ftpClient.completePendingCommand 返回 false，说明文件传输失败，则主动断开连接。
            LOGGER.debug("ftpClient.completePendingCommand 返回 false, 文件传输失败, 将主动断开连接, 并抛出异常...");
            // 主动断开连接后，调用其它方法，会自动触发重连机制，所以不需要再次重连。
            noThrowingDisconnectFtp();
            closed = true;
            lock.unlock();
            // 抛出 IOException，以通知上层调用者。
            throw new IOException("ftpClient.completePendingCommand 返回 false, 文件传输失败");
        }

        private void noThrowingDisconnectFtp() {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (Exception e) {
                LOGGER.warn("FtpClient 登出或断开连接时发生异常, 异常信息如下: ", e);
            }
        }

        private void makeSureOpen(String exceptionMessage) throws IllegalStateException {
            if (closed) {
                throw new IllegalStateException(exceptionMessage);
            }
        }
    }
}
