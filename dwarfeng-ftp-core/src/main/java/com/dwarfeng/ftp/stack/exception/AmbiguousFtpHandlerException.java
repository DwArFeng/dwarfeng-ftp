package com.dwarfeng.ftp.stack.exception;

/**
 * FTP 处理器歧义异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class AmbiguousFtpHandlerException extends FtpQosException {

    private static final long serialVersionUID = -4074759026227939357L;

    public AmbiguousFtpHandlerException() {
    }

    public AmbiguousFtpHandlerException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "应用上下文中有多个 FTP 处理器, 但是没有指定 handlerName";
    }
}
