package com.dwarfeng.ftp.exception;

/**
 * FTP 文件删除异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileDeleteException extends FtpFileException {

    private static final long serialVersionUID = -1196700150874586320L;

    public FtpFileDeleteException(String filePath) {
        super(filePath);
    }

    public FtpFileDeleteException(Throwable cause, String filePath) {
        super(cause, filePath);
    }

    @Override
    public String getMessage() {
        return "FTP file delete failed: " + filePath;
    }
}
