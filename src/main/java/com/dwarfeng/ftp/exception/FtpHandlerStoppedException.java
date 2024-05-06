package com.dwarfeng.ftp.exception;

/**
 * FTP 处理器停止异常。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class FtpHandlerStoppedException extends FtpException {

    private static final long serialVersionUID = -3858639937543949737L;

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
