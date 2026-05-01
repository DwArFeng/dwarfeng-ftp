package com.dwarfeng.ftp.stack.exception;

import com.dwarfeng.subgrade.stack.exception.HandlerException;

/**
 * FTP QoS 异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpQosException extends HandlerException {

    private static final long serialVersionUID = 1209204088884592170L;

    public FtpQosException() {
    }

    public FtpQosException(String message) {
        super(message);
    }

    public FtpQosException(String message, Throwable cause) {
        super(message, cause);
    }

    public FtpQosException(Throwable cause) {
        super(cause);
    }
}
