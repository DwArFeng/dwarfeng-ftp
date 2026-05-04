package com.dwarfeng.ftp.node.configuration;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * FTP 命名空间处理器。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpNamespaceHandlerSupport extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("config", new FtpConfigDefinitionParser());
        registerBeanDefinitionParser("handler", new FtpHandlerDefinitionParser());
        registerBeanDefinitionParser("qos", new FtpQosDefinitionParser());
    }
}
