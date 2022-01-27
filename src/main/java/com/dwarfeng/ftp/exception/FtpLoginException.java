package com.dwarfeng.ftp.exception;

/**
 * FTP 登录异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpLoginException extends FtpException {

    private static final long serialVersionUID = -6172569055969118180L;

    public FtpLoginException() {
    }

    public FtpLoginException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Wrong username or password";
    }
}
