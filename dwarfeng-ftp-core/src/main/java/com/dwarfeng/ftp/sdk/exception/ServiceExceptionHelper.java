package com.dwarfeng.ftp.sdk.exception;

import com.dwarfeng.ftp.stack.exception.*;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * FTP 模块服务异常帮助类。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class ServiceExceptionHelper {

    private ServiceExceptionHelper() {
        throw new IllegalStateException("禁止外部实例化");
    }

    /**
     * 向指定的映射中添加 FTP 模块默认的目标映射。
     *
     * <p>
     * 该方法可以在配置类中快速搭建异常目标映射。映射保存异常代码供应器，调用方在映射异常时解析当前代码快照。
     *
     * @param map 指定的映射，允许为 null。
     * @return 添加了默认目标的映射。
     */
    public static Map<Class<? extends Exception>, Supplier<ServiceException.Code>> putDefaultDestination(
            Map<Class<? extends Exception>, Supplier<ServiceException.Code>> map
    ) {
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }

        map.put(FtpException.class, ServiceExceptionCodeSuppliers.FTP_FAILED);
        map.put(FtpConnectException.class, ServiceExceptionCodeSuppliers.FTP_CONNECT_FAILED);
        map.put(FtpLoginException.class, ServiceExceptionCodeSuppliers.FTP_LOGIN_FAILED);
        map.put(FtpFileException.class, ServiceExceptionCodeSuppliers.FTP_FILE_FAILED);
        map.put(FtpFileRetrieveException.class, ServiceExceptionCodeSuppliers.FTP_FILE_RETRIEVE_FAILED);
        map.put(FtpFileStoreException.class, ServiceExceptionCodeSuppliers.FTP_FILE_STORE_FAILED);
        map.put(FtpFileDeleteException.class, ServiceExceptionCodeSuppliers.FTP_FILE_DELETE_FAILED);
        map.put(FtpFileNotExistsException.class, ServiceExceptionCodeSuppliers.FTP_FILE_NOT_EXISTS);
        map.put(FtpStreamOpenException.class, ServiceExceptionCodeSuppliers.FTP_STREAM_OPEN_FAILED);
        map.put(FtpHandlerStoppedException.class, ServiceExceptionCodeSuppliers.FTP_HANDLER_STOPPED);
        map.put(FtpQosException.class, ServiceExceptionCodeSuppliers.FTP_QOS_FAILED);
        map.put(AmbiguousFtpHandlerException.class, ServiceExceptionCodeSuppliers.AMBIGUOUS_FTP_HANDLER);
        map.put(NoFtpHandlerPresentException.class, ServiceExceptionCodeSuppliers.NO_FTP_HANDLER_PRESENT);
        map.put(FtpHandlerNotFoundException.class, ServiceExceptionCodeSuppliers.FTP_QOS_HANDLER_NOT_FOUND);

        return map;
    }
}
