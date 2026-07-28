package com.dwarfeng.ftp.stack.exception;

import java.io.Serial;

/**
 * FTP 处理器停止异常。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class FtpHandlerStoppedException extends FtpException {

    @Serial
    private static final long serialVersionUID = 6750840280601354256L;

    public FtpHandlerStoppedException() {
    }

    public FtpHandlerStoppedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "FTP handler is stopped";
    }
}
