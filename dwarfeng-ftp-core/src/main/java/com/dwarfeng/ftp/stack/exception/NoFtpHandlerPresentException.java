package com.dwarfeng.ftp.stack.exception;

/**
 * 没有 FTP 处理器异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class NoFtpHandlerPresentException extends FtpQosException {

    private static final long serialVersionUID = -8127002829915487319L;

    public NoFtpHandlerPresentException() {
    }

    public NoFtpHandlerPresentException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "应用上下文中没有 FTP 处理器";
    }
}
