package com.dwarfeng.ftp.exception;

/**
 * FTP 流打开异常。
 *
 * @author DwArFeng
 * @since 1.1.6
 */
public class FtpStreamOpenException extends FtpException {

    private static final long serialVersionUID = -4524389201046188065L;

    public FtpStreamOpenException() {
    }

    public FtpStreamOpenException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "无法打开流。";
    }
}
