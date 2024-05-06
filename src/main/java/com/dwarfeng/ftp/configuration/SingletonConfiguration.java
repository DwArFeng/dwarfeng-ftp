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

    public static final String SPEL_DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH = "${ftp.temporary_file_directory_path:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH}}";
    public static final String SPEL_DEFAULT_TEMPORARY_FILE_PREFIX = "${ftp.temporary_file_prefix:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_PREFIX}}";
    public static final String SPEL_DEFAULT_TEMPORARY_FILE_SUFFIX = "${ftp.temporary_file_suffix:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_SUFFIX}}";
    public static final String SPEL_DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE = "${ftp.file_copy_memory_buffer_size:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE}}";

    private final ThreadPoolTaskScheduler scheduler;

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.port:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_PORT}}")
    private int port;

    @Value("${ftp.server_charset:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_SERVER_CHARSET}}")
    private String serverCharset;

    @Value("${ftp.connect_timeout:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_CONNECT_TIMEOUT}}")
    private int connectTimeout;

    @Value("${ftp.noop_interval:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_NOOP_INTERVAL}}")
    private long noopInterval;

    @Value("${ftp.buffer_size:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_BUFFER_SIZE}}")
    private int bufferSize;

    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH)
    private String temporaryFileDirectoryPath;

    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_DEFAULT_TEMPORARY_FILE_PREFIX)
    private String temporaryFilePrefix;
    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_DEFAULT_TEMPORARY_FILE_SUFFIX)
    private String temporaryFileSuffix;

    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE)
    private int fileCopyMemoryBufferSize;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SingletonConfiguration(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FtpHandler ftpHandler() {
        FtpConfig ftpConfig = new FtpConfig(
                host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize
        );

        return new FtpHandlerImpl(scheduler, ftpConfig);
    }
}
