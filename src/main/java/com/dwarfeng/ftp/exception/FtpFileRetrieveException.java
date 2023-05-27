package com.dwarfeng.ftp.exception;

/**
 * FTP 文件获取异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileRetrieveException extends FtpFileException {

    private static final long serialVersionUID = 926849182885006952L;

    public FtpFileRetrieveException(String filePath) {
        super(filePath);
    }

    public FtpFileRetrieveException(Throwable cause, String filePath) {
        super(cause, filePath);
    }

    @Override
    public String getMessage() {
        return "FTP file retrieve failed: " + filePath;
    }
}
