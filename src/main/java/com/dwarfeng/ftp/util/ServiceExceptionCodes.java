package com.dwarfeng.ftp.util;

import com.dwarfeng.subgrade.stack.exception.ServiceException;

/**
 * 服务异常代码。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class ServiceExceptionCodes {

    private static int EXCEPTION_CODE_OFFSET = 20000;

    // 根异常。
    public static final ServiceException.Code FTP_FAILED =
            new ServiceException.Code(offset(0), "ftp failed");
    // 连接与登录异常。
    public static final ServiceException.Code FTP_CONNECT_FAILED =
            new ServiceException.Code(offset(1), "ftp connect failed");
    public static final ServiceException.Code FTP_LOGIN_FAILED =
            new ServiceException.Code(offset(2), "ftp login failed");
    // 文件异常。
    public static final ServiceException.Code FTP_FILE_FAILED =
            new ServiceException.Code(offset(3), "ftp file failed");
    // 文件子异常。
    public static final ServiceException.Code FTP_FILE_RETRIEVE_FAILED =
            new ServiceException.Code(offset(4), "ftp file retrieve failed");
    public static final ServiceException.Code FTP_FILE_STORE_FAILED =
            new ServiceException.Code(offset(5), "ftp file store failed");
    public static final ServiceException.Code FTP_FILE_DELETE_FAILED =
            new ServiceException.Code(offset(6), "ftp file delete failed");
    public static final ServiceException.Code FTP_FILE_NOT_EXISTS =
            new ServiceException.Code(offset(8), "ftp file not exists");
    // 流异常。
    public static final ServiceException.Code FTP_STREAM_OPEN_FAILED =
            new ServiceException.Code(offset(7), "ftp stream open failed");
    // 处理器异常。
    /**
     * @since 1.2.0
     */
    public static final ServiceException.Code FTP_HANDLER_STOPPED =
            new ServiceException.Code(offset(9), "ftp handler stopped");

    private static int offset(int i) {
        return EXCEPTION_CODE_OFFSET + i;
    }

    /**
     * 获取异常代号的偏移量。
     *
     * @return 异常代号的偏移量。
     */
    public static int getExceptionCodeOffset() {
        return EXCEPTION_CODE_OFFSET;
    }

    /**
     * 设置异常代号的偏移量。
     *
     * @param exceptionCodeOffset 指定的异常代号的偏移量。
     */
    public static void setExceptionCodeOffset(int exceptionCodeOffset) {
        // 设置 EXCEPTION_CODE_OFFSET 的值。
        EXCEPTION_CODE_OFFSET = exceptionCodeOffset;

        // 以新的 EXCEPTION_CODE_OFFSET 为基准，更新异常代码的值。
        FTP_FAILED.setCode(offset(0));
        FTP_CONNECT_FAILED.setCode(offset(1));
        FTP_LOGIN_FAILED.setCode(offset(2));
        FTP_FILE_FAILED.setCode(offset(3));
        FTP_FILE_RETRIEVE_FAILED.setCode(offset(4));
        FTP_FILE_STORE_FAILED.setCode(offset(5));
        FTP_FILE_DELETE_FAILED.setCode(offset(6));
        FTP_FILE_NOT_EXISTS.setCode(offset(8));
        FTP_STREAM_OPEN_FAILED.setCode(offset(7));
        FTP_HANDLER_STOPPED.setCode(offset(9));
    }

    private ServiceExceptionCodes() {
        throw new IllegalStateException("禁止实例化");
    }
}
