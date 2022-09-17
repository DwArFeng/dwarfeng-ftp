package com.dwarfeng.ftp.handler;

import com.dwarfeng.ftp.configuration.SingletonConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 遗留配置。
 *
 * <p>
 * 在旧版本中，FtpHandlerImpl 被设计为单例模式，本身配有扫描注解，
 * 使用者只需扫描 handler 包，就能加载 FtpHandler 的单例。
 *
 * <p>
 * 新版本中，FtpHandlerImpl 不再是单例模式了，因此扫描注解也被去除。
 * 单例模式通过扫描 {@link SingletonConfiguration} 注解实现。
 *
 * <p>
 * 为了兼容使用旧版本的代码，增加此配置文件，该配置文件直接导入 {@link SingletonConfiguration}，并且可被扫描。
 * 这样，旧代码扫描 handler 包时，等同于加载 FtpHandler 的单例。
 *
 * <p>
 * 该类的设计是为了兼容旧代码，因此，新的项目不应主动扫描 handler 包，或者以其它形式加载此配置。
 *
 * @author DwArFeng
 * @since 1.1.0
 */
@Configuration
@Import(SingletonConfiguration.class)
@Deprecated
class LegacyConfiguration {
}
