package com.dwarfeng.ftp.exception;

/**
 * FTP 文件存储异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileStoreException extends FtpFileException {

    private static final long serialVersionUID = -5867125334615040355L;

    public FtpFileStoreException(String filePath) {
        super(filePath);
    }

    public FtpFileStoreException(Throwable cause, String filePath) {
        super(cause, filePath);
    }

    @Override
    public String getMessage() {
        return "FTP file store failed: " + filePath;
    }
}
