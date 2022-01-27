package com.dwarfeng.ftp.exception;

import com.dwarfeng.subgrade.stack.exception.HandlerException;

/**
 * FTP 异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpException extends HandlerException {

    private static final long serialVersionUID = 395354844582856044L;

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
