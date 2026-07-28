package com.dwarfeng.ftp.sdk.exception;

import com.dwarfeng.ftp.core.internal.i18n.CoreMessageKey;
import com.dwarfeng.ftp.core.internal.i18n.CoreMessages;
import com.dwarfeng.subgrade.basic.stack.exception.ServiceException;

import java.util.function.Supplier;

/**
 * FTP 模块服务异常代码供应器。
 *
 * @author DwArFeng
 * @since 3.0.0
 */
public final class ServiceExceptionCodeSuppliers {

    /**
     * FTP 操作失败。
     */
    public static final Supplier<ServiceException.Code> FTP_FAILED =
            () -> new ServiceException.Code(
                    offset(0), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FAILED)
            );

    /**
     * FTP 连接失败。
     */
    public static final Supplier<ServiceException.Code> FTP_CONNECT_FAILED =
            () -> new ServiceException.Code(
                    offset(1), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_CONNECT_FAILED)
            );

    /**
     * FTP 登录失败。
     */
    public static final Supplier<ServiceException.Code> FTP_LOGIN_FAILED =
            () -> new ServiceException.Code(
                    offset(2), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_LOGIN_FAILED)
            );

    /**
     * FTP 文件操作失败。
     */
    public static final Supplier<ServiceException.Code> FTP_FILE_FAILED =
            () -> new ServiceException.Code(
                    offset(3), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_FAILED)
            );

    /**
     * FTP 文件下载失败。
     */
    public static final Supplier<ServiceException.Code> FTP_FILE_RETRIEVE_FAILED =
            () -> new ServiceException.Code(
                    offset(4), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_RETRIEVE_FAILED)
            );

    /**
     * FTP 文件上传失败。
     */
    public static final Supplier<ServiceException.Code> FTP_FILE_STORE_FAILED =
            () -> new ServiceException.Code(
                    offset(5), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_STORE_FAILED)
            );

    /**
     * FTP 文件删除失败。
     */
    public static final Supplier<ServiceException.Code> FTP_FILE_DELETE_FAILED =
            () -> new ServiceException.Code(
                    offset(6), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_DELETE_FAILED)
            );

    /**
     * FTP 文件不存在。
     */
    public static final Supplier<ServiceException.Code> FTP_FILE_NOT_EXISTS =
            () -> new ServiceException.Code(
                    offset(8), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_FILE_NOT_EXISTS)
            );

    /**
     * FTP 流打开失败。
     */
    public static final Supplier<ServiceException.Code> FTP_STREAM_OPEN_FAILED =
            () -> new ServiceException.Code(
                    offset(7), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_STREAM_OPEN_FAILED)
            );

    /**
     * FTP 处理器已停止。
     */
    public static final Supplier<ServiceException.Code> FTP_HANDLER_STOPPED =
            () -> new ServiceException.Code(
                    offset(9), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_HANDLER_STOPPED)
            );

    /**
     * FTP QoS 操作失败。
     */
    public static final Supplier<ServiceException.Code> FTP_QOS_FAILED =
            () -> new ServiceException.Code(
                    offset(10), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_QOS_FAILED)
            );

    /**
     * FTP 处理器选择不明确。
     */
    public static final Supplier<ServiceException.Code> AMBIGUOUS_FTP_HANDLER =
            () -> new ServiceException.Code(
                    offset(11), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_AMBIGUOUS_FTP_HANDLER)
            );
    /**
     *
     * 没有可用的 FTP 处理器。
     */
    public static final Supplier<ServiceException.Code> NO_FTP_HANDLER_PRESENT =
            () -> new ServiceException.Code(
                    offset(12), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_NO_FTP_HANDLER_PRESENT)
            );

    /**
     * 指定的 FTP 处理器不存在。
     */
    public static final Supplier<ServiceException.Code> FTP_QOS_HANDLER_NOT_FOUND =
            () -> new ServiceException.Code(
                    offset(13), CoreMessages.message(CoreMessageKey.SERVICE_EXCEPTION_FTP_QOS_HANDLER_NOT_FOUND)
            );

    private static volatile int EXCEPTION_CODE_OFFSET = 20000;

    private ServiceExceptionCodeSuppliers() {
        throw new IllegalStateException("禁止实例化");
    }

    private static int offset(int i) {
        return EXCEPTION_CODE_OFFSET + i;
    }

    /**
     * 获取异常代码的偏移量。
     *
     * @return 异常代码的偏移量。
     */
    public static int getExceptionCodeOffset() {
        return EXCEPTION_CODE_OFFSET;
    }

    /**
     * 设置异常代码的偏移量。
     *
     * <p>
     * 该方法只更新后续生成异常代码所使用的偏移量，已经创建的异常代码保持不变。
     *
     * @param exceptionCodeOffset 指定的异常代码偏移量。
     */
    public static void setExceptionCodeOffset(int exceptionCodeOffset) {
        EXCEPTION_CODE_OFFSET = exceptionCodeOffset;
    }
}
