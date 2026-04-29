package com.dwarfeng.ftp.stack.exception;

/**
 * FTP 流打开异常。
 *
 * @author DwArFeng
 * @since 1.1.6
 */
public class FtpStreamOpenException extends FtpException {

    private static final long serialVersionUID = -3485561733768809626L;

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
