package com.dwarfeng.ftp.stack.exception;

/**
 * FTP 处理器未找到异常。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpHandlerNotFoundException extends FtpQosException {

    private static final long serialVersionUID = 8819328689031545721L;

    private final String handlerName;

    public FtpHandlerNotFoundException(String handlerName) {
        this.handlerName = handlerName;
    }

    public FtpHandlerNotFoundException(Throwable cause, String handlerName) {
        super(cause);
        this.handlerName = handlerName;
    }

    @Override
    public String getMessage() {
        return "应用上下文中没有找到名称为 " + handlerName + " 的 FTP 处理器";
    }
}
