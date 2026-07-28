package com.dwarfeng.ftp.stack.exception;

import java.io.Serial;

/**
 * FTP 文件存储异常。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class FtpFileStoreException extends FtpFileException {

    @Serial
    private static final long serialVersionUID = -6726995144822216403L;

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
