package com.dwarfeng.ftp.impl.handler;

import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.exception.AmbiguousFtpHandlerException;
import com.dwarfeng.ftp.stack.exception.FtpHandlerNotFoundException;
import com.dwarfeng.ftp.stack.exception.NoFtpHandlerPresentException;
import com.dwarfeng.ftp.stack.handler.FtpHandler;
import com.dwarfeng.ftp.stack.handler.FtpQosHandler;
import com.dwarfeng.ftp.stack.struct.FtpFileLocation;
import com.dwarfeng.subgrade.sdk.exception.HandlerExceptionHelper;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FTP QoS 处理器实现。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpQosHandlerImpl implements FtpQosHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpQosHandlerImpl.class);

    private final Map<String, FtpHandler> ftpHandlerMap;

    public FtpQosHandlerImpl(Map<String, FtpHandler> ftpHandlerMap) {
        this.ftpHandlerMap = Optional.ofNullable(ftpHandlerMap).orElse(Collections.emptyMap());
    }

    @Override
    public List<String> listHandlerNames() throws HandlerException {
        try {
            List<String> handlerNames = ftpHandlerMap.keySet().stream().sorted().collect(Collectors.toList());
            return Collections.unmodifiableList(handlerNames);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void stopAllManagedHandlers() throws HandlerException {
        try {
            ftpHandlerMap.keySet().stream().sorted().forEach(name -> {
                FtpHandler handler = ftpHandlerMap.get(name);
                try {
                    handler.stop();
                } catch (Exception e) {
                    LOGGER.warn("停止 FTP 处理器 {} 失败，将继续尝试停止其余处理器", name, e);
                }
            });
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public boolean isStarted(@Nullable String handlerName) throws HandlerException {
        try {
            return determineHandler(handlerName).isStarted();
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void start(@Nullable String handlerName) throws HandlerException {
        try {
            determineHandler(handlerName).start();
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void stop(@Nullable String handlerName) throws HandlerException {
        try {
            determineHandler(handlerName).stop();
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public boolean existsFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).existsFile(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public boolean existsFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).existsFile(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void storeFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull byte[] content
    ) throws HandlerException {
        try {
            determineHandler(handlerName).storeFile(filePaths, fileName, content);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void storeFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull byte[] content
    ) throws HandlerException {
        try {
            determineHandler(handlerName).storeFile(fileLocation, content);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).retrieveFile(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).retrieveFile(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull InputStream in
    ) throws HandlerException {
        try {
            determineHandler(handlerName).storeFileByStream(filePaths, fileName, in);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in
    ) throws HandlerException {
        try {
            determineHandler(handlerName).storeFileByStream(fileLocation, in);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws HandlerException {
        try {
            determineHandler(handlerName).retrieveFileByStream(filePaths, fileName, out);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out
    ) throws HandlerException {
        try {
            determineHandler(handlerName).retrieveFileByStream(fileLocation, out);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void deleteFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            determineHandler(handlerName).deleteFile(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void deleteFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            determineHandler(handlerName).deleteFile(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void removeDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException {
        try {
            determineHandler(handlerName).removeDirectory(filePaths);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void removeDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            determineHandler(handlerName).removeDirectory(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).listFiles(filePaths);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).listFiles(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).listFileNames(filePaths);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).listFileNames(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).openInputStream(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).openInputStream(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).openOutputStream(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).openOutputStream(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void renameFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException {
        try {
            determineHandler(handlerName).renameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void renameFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws HandlerException {
        try {
            determineHandler(handlerName).renameFile(oldFileLocation, neoFileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void clearDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException {
        try {
            determineHandler(handlerName).clearDirectory(filePaths);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void clearDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            determineHandler(handlerName).clearDirectory(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void copyFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException {
        try {
            determineHandler(handlerName).copyFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public void copyFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws HandlerException {
        try {
            determineHandler(handlerName).copyFile(oldFileLocation, neoFileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).descFile(filePaths, fileName);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    @Override
    public FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException {
        try {
            return determineHandler(handlerName).descFile(fileLocation);
        } catch (Exception e) {
            throw HandlerExceptionHelper.parse(e);
        }
    }

    private FtpHandler determineHandler(@Nullable String handlerName) throws Exception {
        if (ftpHandlerMap.isEmpty()) {
            throw new NoFtpHandlerPresentException();
        }
        if (handlerName == null) {
            if (ftpHandlerMap.size() == 1) {
                return ftpHandlerMap.values().iterator().next();
            } else {
                throw new AmbiguousFtpHandlerException();
            }
        } else {
            if (!ftpHandlerMap.containsKey(handlerName)) {
                throw new FtpHandlerNotFoundException(handlerName);
            }
            return ftpHandlerMap.get(handlerName);
        }
    }

    @Override
    public String toString() {
        return "FtpQosHandlerImpl{" +
                "ftpHandlerMap=" + ftpHandlerMap +
                '}';
    }
}
