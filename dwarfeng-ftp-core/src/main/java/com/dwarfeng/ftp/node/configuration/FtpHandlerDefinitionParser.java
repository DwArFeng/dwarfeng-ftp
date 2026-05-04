package com.dwarfeng.ftp.node.configuration;

import com.dwarfeng.ftp.impl.handler.FtpHandlerImpl;
import com.dwarfeng.ftp.sdk.util.BeanDefinitionParserUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;

/**
 * FTP Handler 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpHandlerDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String handlerName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("handler-name")
        );
        String schedulerRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("scheduler-ref")
        );
        String configRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("config-ref")
        );
        String autoStart = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("auto-start")
        );

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, handlerName);

        BeanDefinitionBuilder ftpHandlerBuilder = BeanDefinitionBuilder.rootBeanDefinition(FtpHandlerImpl.class);
        ftpHandlerBuilder.getRawBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        ConstructorArgumentValues ftpHandlerConstructorArgumentValues = new ConstructorArgumentValues();
        ftpHandlerConstructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(schedulerRef));
        ftpHandlerConstructorArgumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(configRef));
        ftpHandlerBuilder.getRawBeanDefinition().setConstructorArgumentValues(ftpHandlerConstructorArgumentValues);
        if (Boolean.parseBoolean(autoStart)) {
            ftpHandlerBuilder.setInitMethodName("start");
        }
        ftpHandlerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        ftpHandlerBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(handlerName, ftpHandlerBuilder.getBeanDefinition());

        return null;
    }
}
