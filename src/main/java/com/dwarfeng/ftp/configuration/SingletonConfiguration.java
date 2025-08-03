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

    /**
     * SPEL: FTP 的临时文件目录。
     *
     * @since 1.3.4
     */
    public static final String SPEL_TEMPORARY_FILE_DIRECTORY_PATH = "${ftp.temporary_file_directory_path:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH}}";

    /**
     * SPEL: FTP 临时文件的前缀。
     *
     * @since 1.3.4
     */
    public static final String SPEL_TEMPORARY_FILE_PREFIX = "${ftp.temporary_file_prefix:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_PREFIX}}";

    /**
     * SPEL: FTP 临时文件的后缀。
     *
     * @since 1.3.4
     */
    public static final String SPEL_TEMPORARY_FILE_SUFFIX = "${ftp.temporary_file_suffix:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_TEMPORARY_FILE_SUFFIX}}";

    /**
     * SPEL: FTP 文件拷贝功能的内存缓冲区大小。
     *
     * @since 1.3.4
     */
    public static final String SPEL_FILE_COPY_MEMORY_BUFFER_SIZE = "${ftp.file_copy_memory_buffer_size:" +
            "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE}}";

    /**
     * SPEL: FTP 远程主动数据连接模式下的服务主机地址。
     *
     * @since 1.3.4
     */
    public static final String SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST =
            "${ftp.active_remote_data_connection_mode_server_host:" +
                    "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder)" +
                    ".DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST}}";

    /**
     * SPEL: FTP 远程主动数据连接模式下的服务端口。
     *
     * @since 1.3.4
     */
    public static final String SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT =
            "${ftp.active_remote_data_connection_mode_server_port:" +
                    "#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder)" +
                    ".DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT}}";

    /**
     * SPEL: FTP 的临时文件目录。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_TEMPORARY_FILE_DIRECTORY_PATH} 替代。
     *
     * @see #SPEL_TEMPORARY_FILE_DIRECTORY_PATH
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH = SPEL_TEMPORARY_FILE_DIRECTORY_PATH;

    /**
     * SPEL: FTP 临时文件的前缀。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_TEMPORARY_FILE_PREFIX} 替代。
     *
     * @see #SPEL_TEMPORARY_FILE_PREFIX
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_TEMPORARY_FILE_PREFIX = SPEL_TEMPORARY_FILE_PREFIX;

    /**
     * SPEL: FTP 临时文件的后缀。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_TEMPORARY_FILE_SUFFIX} 替代。
     *
     * @see #SPEL_TEMPORARY_FILE_SUFFIX
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_TEMPORARY_FILE_SUFFIX = SPEL_TEMPORARY_FILE_SUFFIX;

    /**
     * SPEL: FTP 文件拷贝功能的内存缓冲区大小。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_FILE_COPY_MEMORY_BUFFER_SIZE} 替代。
     *
     * @see #SPEL_FILE_COPY_MEMORY_BUFFER_SIZE
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE = SPEL_FILE_COPY_MEMORY_BUFFER_SIZE;

    /**
     * SPEL: FTP 远程主动数据连接模式下的服务主机地址。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST} 替代。
     *
     * @see #SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST =
            SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST;

    /**
     * SPEL: FTP 远程主动数据连接模式下的服务端口。
     *
     * <p>
     * 该常量由于命名不规范，已被弃用。<br>
     * 请使用 {@link #SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT} 替代。
     *
     * @see #SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT
     * @deprecated 该常量由于命名不规范，已被弃用。
     */
    @Deprecated
    public static final String SPEL_DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT =
            SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT;

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
    @Value(SPEL_TEMPORARY_FILE_DIRECTORY_PATH)
    private String temporaryFileDirectoryPath;

    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_TEMPORARY_FILE_PREFIX)
    private String temporaryFilePrefix;
    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_TEMPORARY_FILE_SUFFIX)
    private String temporaryFileSuffix;

    /**
     * @since 1.2.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_FILE_COPY_MEMORY_BUFFER_SIZE)
    private int fileCopyMemoryBufferSize;

    /**
     * @since 1.3.0
     */
    @Value("${ftp.data_connection_mode:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_DATA_CONNECTION_MODE}}")
    private int dataConnectionMode;

    /**
     * @since 1.3.0
     */
    @Value("${ftp.data_timeout:#{T(com.dwarfeng.ftp.struct.FtpConfig$Builder).DEFAULT_DATA_TIMEOUT}}")
    private int dataTimeout;

    /**
     * @since 1.3.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST)
    private String activeRemoteDataConnectionModeServerHost;

    /**
     * @since 1.3.0
     */
    // SPEL 太长，故使用常量缩短长度。
    @Value(SPEL_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT)
    private int activeRemoteDataConnectionModeServerPort;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SingletonConfiguration(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FtpHandler ftpHandler() {
        FtpConfig ftpConfig = new FtpConfig(
                host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize,
                dataConnectionMode, dataTimeout, activeRemoteDataConnectionModeServerHost,
                activeRemoteDataConnectionModeServerPort
        );

        return new FtpHandlerImpl(scheduler, ftpConfig);
    }
}
