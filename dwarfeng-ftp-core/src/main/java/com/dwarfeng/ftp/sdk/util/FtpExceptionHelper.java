package com.dwarfeng.ftp.sdk.util;

import com.dwarfeng.ftp.stack.exception.FtpException;

import jakarta.annotation.Nonnull;

/**
 * FTP 处理器异常帮助类。
 *
 * @author DwArFeng
 * @since 2.1.0
 */
public final class FtpExceptionHelper {

    /**
     * 将指定的异常转化为 FTP 处理器异常。
     *
     * @param e 指定的异常。
     * @return 解析后得到的 FTP 处理器异常。
     */
    public static FtpException parse(@Nonnull Exception e) {
        if (e instanceof FtpException) {
            return (FtpException) e;
        }
        return new FtpException(e);
    }

    private FtpExceptionHelper() {
        throw new IllegalStateException("禁止实例化");
    }
}
