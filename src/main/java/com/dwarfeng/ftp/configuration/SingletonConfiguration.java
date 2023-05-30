package com.dwarfeng.ftp.configuration;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.handler.FtpHandlerImpl;
import com.dwarfeng.ftp.struct.FtpConfig;
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
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.server_charset}")
    private String serverCharset;

    @Value("${ftp.connect_timeout}")
    private int connectTimeout;

    @Value("${ftp.noop_interval}")
    private long noopInterval;

    // 设置默认值，以兼容旧版本。
    @Value("${ftp.buffer_size:4096}")
    private int bufferSize;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SingletonConfiguration(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FtpHandler ftpHandler() {
        return new FtpHandlerImpl(
                scheduler,
                new FtpConfig(host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize)
        );
    }
}
