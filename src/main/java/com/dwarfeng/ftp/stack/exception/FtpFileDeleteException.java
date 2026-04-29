package com.dwarfeng.ftp.stack.exception;

/**
 * FTP 文件删除异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileDeleteException extends FtpFileException {

    private static final long serialVersionUID = 3894648145722254359L;

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
