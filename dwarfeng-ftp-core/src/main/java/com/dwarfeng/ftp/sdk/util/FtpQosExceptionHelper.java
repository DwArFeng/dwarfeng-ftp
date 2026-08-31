package com.dwarfeng.ftp.sdk.util;

import com.dwarfeng.ftp.stack.exception.FtpQosException;

import org.jetbrains.annotations.NotNull;

/**
 * FTP QoS 处理器异常帮助类。
 *
 * @author DwArFeng
 * @since 2.1.0
 */
public final class FtpQosExceptionHelper {

    /**
     * 将指定的异常转化为 FTP QoS 处理器异常。
     *
     * @param e 指定的异常。
     * @return 解析后得到的 FTP QoS 处理器异常。
     */
    public static FtpQosException parse(@NotNull Exception e) {
        if (e instanceof FtpQosException) {
            return (FtpQosException) e;
        }
        return new FtpQosException(e);
    }

    private FtpQosExceptionHelper() {
        throw new IllegalStateException("禁止实例化");
    }
}
