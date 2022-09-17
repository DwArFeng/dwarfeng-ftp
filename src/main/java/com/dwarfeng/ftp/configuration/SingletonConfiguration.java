package com.dwarfeng.ftp.configuration;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.handler.FtpHandlerImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 单例模式配置。
 *
 * @author DwArFeng
 * @since 1.1.0
 */
@Configuration
public class SingletonConfiguration {

    private final ThreadPoolTaskScheduler scheduler;

    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.username}")
    private String ftpUserName;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.server_charset}")
    private String serverCharset;
    @Value("${ftp.connect_timeout}")
    private int connectTimeout;
    @Value("${ftp.noop_interval}")
    private long noopInterval;

    public SingletonConfiguration(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Bean
    public FtpHandler ftpHandler() {
        return new FtpHandlerImpl(
                scheduler, ftpHost, ftpPort, ftpUserName, ftpPassword, serverCharset, connectTimeout, noopInterval
        );
    }
}
