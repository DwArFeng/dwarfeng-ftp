package com.dwarfeng.ftp.stack.exception;

/**
 * FTP 文件异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileException extends FtpException {

    private static final long serialVersionUID = -2559333601753500513L;

    protected final String filePath;

    public FtpFileException(String filePath) {
        this.filePath = filePath;
    }

    public FtpFileException(Throwable cause, String filePath) {
        super(cause);
        this.filePath = filePath;
    }

    @Override
    public String getMessage() {
        return "FTP file transfer failed: " + filePath;
    }
}
