package com.dwarfeng.ftp.stack.exception;

import com.dwarfeng.subgrade.basic.stack.exception.HandlerException;

import java.io.Serial;

/**
 * FTP 异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpException extends HandlerException {

    @Serial
    private static final long serialVersionUID = -9056522962880850814L;

    public FtpException() {
    }

    public FtpException(String message) {
        super(message);
    }

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }

    public FtpException(Throwable cause) {
        super(cause);
    }
}
