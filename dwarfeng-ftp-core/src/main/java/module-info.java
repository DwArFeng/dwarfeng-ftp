module com.dwarfeng.ftp.core {

    requires com.dwarfeng.ftp.base;
    requires transitive com.dwarfeng.dutil.basic;
    requires com.dwarfeng.subgrade.aop;
    requires transitive com.dwarfeng.subgrade.basic;
    requires transitive com.dwarfeng.subgrade.lifecycle;
    requires com.alibaba.fastjson2;
    requires jakarta.annotation;
    requires org.apache.commons.lang3;
    requires org.apache.commons.net;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.expression;
    requires static org.jetbrains.annotations;
    requires transitive java.xml;

    exports com.dwarfeng.ftp.impl.handler;
    exports com.dwarfeng.ftp.impl.service;
    exports com.dwarfeng.ftp.node.configuration;
    exports com.dwarfeng.ftp.sdk.exception;
    exports com.dwarfeng.ftp.sdk.util;
    exports com.dwarfeng.ftp.stack.bean.dto;
    exports com.dwarfeng.ftp.stack.exception;
    exports com.dwarfeng.ftp.stack.handler;
    exports com.dwarfeng.ftp.stack.service;
    exports com.dwarfeng.ftp.stack.struct;
    exports com.dwarfeng.ftp.stack.util;

    opens com.dwarfeng.ftp.sdk.i18n to com.dwarfeng.ftp.base;
}
