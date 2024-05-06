package com.dwarfeng.ftp.util;

import com.dwarfeng.ftp.exception.*;
import com.dwarfeng.subgrade.stack.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 异常的帮助工具类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class ServiceExceptionHelper {

    /**
     * 向指定的映射中添加 dwarfeng-ftp 默认的目标映射。
     * <p>该方法可以在配置类中快速的搭建目标映射。</p>
     *
     * @param map 指定的映射，允许为null。
     * @return 添加了默认目标的映射。
     */
    public static Map<Class<? extends Exception>, ServiceException.Code> putDefaultDestination(
            Map<Class<? extends Exception>, ServiceException.Code> map) {
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }

        map.put(FtpException.class, ServiceExceptionCodes.FTP_FAILED);
        map.put(FtpConnectException.class, ServiceExceptionCodes.FTP_CONNECT_FAILED);
        map.put(FtpLoginException.class, ServiceExceptionCodes.FTP_LOGIN_FAILED);
        map.put(FtpFileException.class, ServiceExceptionCodes.FTP_FILE_FAILED);
        map.put(FtpFileRetrieveException.class, ServiceExceptionCodes.FTP_FILE_RETRIEVE_FAILED);
        map.put(FtpFileStoreException.class, ServiceExceptionCodes.FTP_FILE_STORE_FAILED);
        map.put(FtpFileDeleteException.class, ServiceExceptionCodes.FTP_FILE_DELETE_FAILED);
        map.put(FtpFileNotExistsException.class, ServiceExceptionCodes.FTP_FILE_NOT_EXISTS);
        map.put(FtpStreamOpenException.class, ServiceExceptionCodes.FTP_STREAM_OPEN_FAILED);
        map.put(FtpHandlerStoppedException.class, ServiceExceptionCodes.FTP_HANDLER_STOPPED);

        return map;
    }

    private ServiceExceptionHelper() {
        throw new IllegalStateException("禁止外部实例化");
    }
}
