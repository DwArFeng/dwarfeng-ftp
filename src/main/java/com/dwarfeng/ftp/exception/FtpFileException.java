package com.dwarfeng.ftp.exception;

/**
 * FTP 文件异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileException extends FtpException {

    private static final long serialVersionUID = 5688270595564649694L;

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
