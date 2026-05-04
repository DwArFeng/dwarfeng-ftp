package com.dwarfeng.ftp.node.configuration;

import com.dwarfeng.ftp.sdk.util.BeanDefinitionParserUtil;
import com.dwarfeng.ftp.stack.struct.FtpConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;

/**
 * FTP Config 元素的 BeanDefinitionParser。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public class FtpConfigDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, @Nonnull ParserContext parserContext) {
        String configName = (String) BeanDefinitionParserUtil.mayResolveSpel(
                parserContext, element.getAttribute("config-name")
        );

        BeanDefinitionParserUtil.makeSureBeanNameNotDuplicated(parserContext, configName);

        RootBeanDefinition ftpConfigBuilderBeanDefinition = new RootBeanDefinition(FtpConfig.Builder.class);
        String host = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("host")
        );
        String username = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("username")
        );
        String password = BeanDefinitionParserUtil.mayResolvePlaceholder(
                parserContext, element.getAttribute("password")
        );
        ftpConfigBuilderBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, host);
        ftpConfigBuilderBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, username);
        ftpConfigBuilderBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(2, password);
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "port",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("port")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "serverCharset",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("server-charset")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "connectTimeout",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("connect-timeout")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "noopInterval",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("noop-interval")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "bufferSize",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("buffer-size")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "temporaryFileDirectoryPath",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("temporary-file-directory-path")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "temporaryFilePrefix",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("temporary-file-prefix")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "temporaryFileSuffix",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("temporary-file-suffix")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "fileCopyMemoryBufferSize",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("file-copy-memory-buffer-size")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "dataConnectionMode",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("data-connection-mode")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "dataTimeout",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("data-timeout")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "activeRemoteDataConnectionModeServerHost",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("active-remote-data-connection-mode-server-host")
                )
        );
        ftpConfigBuilderBeanDefinition.getPropertyValues().add(
                "activeRemoteDataConnectionModeServerPort",
                BeanDefinitionParserUtil.mayResolvePlaceholder(
                        parserContext, element.getAttribute("active-remote-data-connection-mode-server-port")
                )
        );
        ftpConfigBuilderBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        ftpConfigBuilderBeanDefinition.setLazyInit(false);
        String ftpConfigBuilderBeanName = BeanDefinitionParserUtil.parseAvailableBeanName(
                parserContext, configName + "Builder"
        );
        parserContext.getRegistry().registerBeanDefinition(ftpConfigBuilderBeanName, ftpConfigBuilderBeanDefinition);

        RootBeanDefinition ftpConfigBeanDefinition = new RootBeanDefinition(FtpConfig.class);
        ftpConfigBeanDefinition.setFactoryBeanName(ftpConfigBuilderBeanName);
        ftpConfigBeanDefinition.setFactoryMethodName("build");
        ftpConfigBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        ftpConfigBeanDefinition.setLazyInit(false);
        parserContext.getRegistry().registerBeanDefinition(configName, ftpConfigBeanDefinition);

        return null;
    }
}
