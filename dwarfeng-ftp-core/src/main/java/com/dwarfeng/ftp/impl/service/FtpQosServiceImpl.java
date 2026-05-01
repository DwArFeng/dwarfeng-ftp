package com.dwarfeng.ftp.impl.service;

import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.handler.FtpQosHandler;
import com.dwarfeng.ftp.stack.service.FtpQosService;
import com.dwarfeng.ftp.stack.struct.FtpFileLocation;
import com.dwarfeng.subgrade.sdk.exception.ServiceExceptionHelper;
import com.dwarfeng.subgrade.stack.exception.ServiceException;
import com.dwarfeng.subgrade.stack.exception.ServiceExceptionMapper;
import com.dwarfeng.subgrade.stack.log.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * FTP QoS 服务实现。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpQosServiceImpl implements FtpQosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpQosServiceImpl.class);

    private final FtpQosHandler ftpQosHandler;
    private final ServiceExceptionMapper sem;

    public FtpQosServiceImpl(FtpQosHandler ftpQosHandler, ServiceExceptionMapper sem) {
        this.ftpQosHandler = ftpQosHandler;
        this.sem = sem;
    }

    @PreDestroy
    public void preDestroy() {
        try {
            ftpQosHandler.stopAllManagedHandlers();
        } catch (Exception e) {
            LOGGER.warn("容器销毁时停止 FTP 托管处理器失败，将忽略该异常", e);
        }
    }

    @Override
    public List<String> listHandlerNames() throws ServiceException {
        try {
            return ftpQosHandler.listHandlerNames();
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("列出所有 FTP 处理器名称时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public boolean isStarted(@Nullable String handlerName) throws ServiceException {
        try {
            return ftpQosHandler.isStarted(handlerName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("查询 FTP 处理器是否已启动时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void start(@Nullable String handlerName) throws ServiceException {
        try {
            ftpQosHandler.start(handlerName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("启动 FTP 处理器时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void stop(@Nullable String handlerName) throws ServiceException {
        try {
            ftpQosHandler.stop(handlerName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("停止 FTP 处理器时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public boolean existsFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            return ftpQosHandler.existsFile(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("检查 FTP 文件是否存在时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public boolean existsFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.existsFile(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("检查 FTP 文件是否存在时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void storeFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull byte[] content
    ) throws ServiceException {
        try {
            ftpQosHandler.storeFile(handlerName, filePaths, fileName, content);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("存储 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void storeFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull byte[] content
    ) throws ServiceException {
        try {
            ftpQosHandler.storeFile(handlerName, fileLocation, content);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("存储 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            return ftpQosHandler.retrieveFile(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("获取 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.retrieveFile(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("获取 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull InputStream in
    ) throws ServiceException {
        try {
            ftpQosHandler.storeFileByStream(handlerName, filePaths, fileName, in);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("通过流存储 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in
    ) throws ServiceException {
        try {
            ftpQosHandler.storeFileByStream(handlerName, fileLocation, in);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("通过流存储 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws ServiceException {
        try {
            ftpQosHandler.retrieveFileByStream(handlerName, filePaths, fileName, out);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("通过流获取 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out
    ) throws ServiceException {
        try {
            ftpQosHandler.retrieveFileByStream(handlerName, fileLocation, out);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("通过流获取 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void deleteFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            ftpQosHandler.deleteFile(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("删除 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void deleteFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            ftpQosHandler.deleteFile(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("删除 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void removeDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws ServiceException {
        try {
            ftpQosHandler.removeDirectory(handlerName, filePaths);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("删除 FTP 目录时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void removeDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            ftpQosHandler.removeDirectory(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("删除 FTP 目录时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws ServiceException {
        try {
            return ftpQosHandler.listFiles(handlerName, filePaths);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("列出 FTP 目录文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.listFiles(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("列出 FTP 目录文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws ServiceException {
        try {
            return ftpQosHandler.listFileNames(handlerName, filePaths);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("列出 FTP 目录文件名时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.listFileNames(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("列出 FTP 目录文件名时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            return ftpQosHandler.openInputStream(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("打开 FTP 文件输入流时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.openInputStream(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("打开 FTP 文件输入流时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            return ftpQosHandler.openOutputStream(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("打开 FTP 文件输出流时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.openOutputStream(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("打开 FTP 文件输出流时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void renameFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws ServiceException {
        try {
            ftpQosHandler.renameFile(handlerName, oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("重命名 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void renameFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws ServiceException {
        try {
            ftpQosHandler.renameFile(handlerName, oldFileLocation, neoFileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("重命名 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void clearDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws ServiceException {
        try {
            ftpQosHandler.clearDirectory(handlerName, filePaths);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("清空 FTP 目录时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void clearDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            ftpQosHandler.clearDirectory(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("清空 FTP 目录时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void copyFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws ServiceException {
        try {
            ftpQosHandler.copyFile(handlerName, oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("复制 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public void copyFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws ServiceException {
        try {
            ftpQosHandler.copyFile(handlerName, oldFileLocation, neoFileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("复制 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws ServiceException {
        try {
            return ftpQosHandler.descFile(handlerName, filePaths, fileName);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("描述 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

    @Override
    public FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws ServiceException {
        try {
            return ftpQosHandler.descFile(handlerName, fileLocation);
        } catch (Exception e) {
            throw ServiceExceptionHelper.logParse("描述 FTP 文件时发生异常", LogLevel.WARN, e, sem);
        }
    }

}
