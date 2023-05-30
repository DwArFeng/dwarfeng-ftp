package com.dwarfeng.ftp.exception;

/**
 * FTP 文件不存在异常。
 *
 * @author DwArFeng
 * @since 1.1.8
 */
public class FtpFileNotExistsException extends FtpFileException {

    private static final long serialVersionUID = -6922447600652897994L;

    public FtpFileNotExistsException(String filePath) {
        super(filePath);
    }

    public FtpFileNotExistsException(Throwable cause, String filePath) {
        super(cause, filePath);
    }

    @Override
    public String getMessage() {
        return "FTP file not exists: " + filePath;
    }
}
