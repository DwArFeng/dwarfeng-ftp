package com.dwarfeng.ftp.node.configuration;

import com.dwarfeng.ftp.impl.handler.FtpQosHandlerImpl;
import com.dwarfeng.ftp.impl.service.FtpQosServiceImpl;
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
 * FTP Qos 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpQosDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String qosHandlerName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("qos-handler-name")
        );
        String qosServiceName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("qos-service-name")
        );
        String semRef = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("sem-ref")
        );

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, qosHandlerName);
        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, qosServiceName);

        BeanDefinitionBuilder ftpQosHandlerBuilder = BeanDefinitionBuilder.rootBeanDefinition(FtpQosHandlerImpl.class);
        ftpQosHandlerBuilder.getRawBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        ftpQosHandlerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        ftpQosHandlerBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(qosHandlerName, ftpQosHandlerBuilder.getBeanDefinition());

        BeanDefinitionBuilder ftpQosServiceBuilder = BeanDefinitionBuilder.rootBeanDefinition(FtpQosServiceImpl.class);
        ftpQosServiceBuilder.getRawBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        ConstructorArgumentValues ftpQosServiceConstructorArgumentValues = new ConstructorArgumentValues();
        ftpQosServiceConstructorArgumentValues.addIndexedArgumentValue(0, new RuntimeBeanReference(qosHandlerName));
        ftpQosServiceConstructorArgumentValues.addIndexedArgumentValue(1, new RuntimeBeanReference(semRef));
        ftpQosServiceBuilder.getRawBeanDefinition().setConstructorArgumentValues(
                ftpQosServiceConstructorArgumentValues
        );
        ftpQosServiceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        ftpQosServiceBuilder.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(qosServiceName, ftpQosServiceBuilder.getBeanDefinition());

        return null;
    }
}
