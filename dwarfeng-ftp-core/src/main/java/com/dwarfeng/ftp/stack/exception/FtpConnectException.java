package com.dwarfeng.ftp.stack.exception;

import java.io.Serial;

/**
 * FTP 连接异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpConnectException extends FtpException {

    @Serial
    private static final long serialVersionUID = -4399560620149281321L;

    public FtpConnectException() {
    }

    public FtpConnectException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "FTP connection failed";
    }
}
