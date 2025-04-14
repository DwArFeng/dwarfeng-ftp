package com.dwarfeng.ftp.struct;

import com.dwarfeng.dutil.basic.prog.Buildable;
import com.dwarfeng.ftp.util.FtpConfigUtil;

/**
 * FTP 配置。
 *
 * @author DwArFeng
 * @since 1.1.8
 */
public final class FtpConfig {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String serverCharset;
    private final int connectTimeout;
    private final long noopInterval;
    private final int bufferSize;

    /**
     * @since 1.2.0
     */
    private final String temporaryFileDirectoryPath;

    /**
     * @since 1.2.0
     */
    private final String temporaryFilePrefix;
    /**
     * @since 1.2.0
     */
    private final String temporaryFileSuffix;

    /**
     * @since 1.2.0
     */
    private final int fileCopyMemoryBufferSize;

    /**
     * 数据连接模式。
     *
     * <p>
     * int 枚举，可能的状态为：
     * <ul>
     *     <li>本地主动模式（传统主动模式 / PORT 模式）</li>
     *     <li>远程主动模式（反向主动模式）</li>
     *     <li>本地被动模式（传统被动模式 / PASV 模式）</li>
     *     <li>远程被动模式（反向被动模式）</li>
     * </ul>
     * 详细值参考 {@link Builder} 类的常量字段。
     *
     * @see Builder#DATA_CONNECTION_MODE_ACTIVE_LOCAL
     * @see Builder#DATA_CONNECTION_MODE_ACTIVE_REMOTE
     * @see Builder#DATA_CONNECTION_MODE_PASSIVE_LOCALE
     * @see Builder#DATA_CONNECTION_MODE_PASSIVE_REMOTE
     * @since 1.3.0
     */
    private final int dataConnectionMode;

    /**
     * 数据超时时间。
     *
     * <p>
     * 设置从数据连接读取时的超时时间。<br>
     * 在建立本地主动模式（传统主动模式 / PORT 模式）数据连接时调用 <code>ServerSocket.accept()</code> 时也会应用此超时设置。
     * <p>
     * 该值的单位是毫秒，小于等于 0 表示无限超时。
     * <p>
     * 当 <code>dataConnectionMode</code> 取值为 <code>0（本地主动模式）</code> 时，
     * 建议将此值设置为大于 0，以避免服务端故障或网络环境波动导致的程序阻塞。
     *
     * @see #dataConnectionMode
     * @since 1.3.0
     */
    private final int dataTimeout;

    /**
     * 远程主动数据连接模式下的服务主机地址。
     *
     * <p>
     * 当 <code>dataConnectionMode</code> 不为远程主动模式（反向主动模式）时，该值将被忽略。
     * <p>
     * 当 <code>dataConnectionMode</code> 取值为 <code>1（远程主动模式）</code> 时，
     * 该值将作为服务的主机地址。
     *
     * @see #dataConnectionMode
     * @since 1.3.0
     */
    private final String activeRemoteDataConnectionModeServerHost;

    /**
     * 远程主动数据连接模式下的服务端口。
     *
     * <p>
     * 当 <code>dataConnectionMode</code> 不为远程主动模式（反向主动模式）时，该值将被忽略。
     * <p>
     * 当 <code>dataConnectionMode</code> 取值为 <code>1（远程主动模式）</code> 时，
     * 该值将作为服务的端口。
     *
     * @see #dataConnectionMode
     * @since 1.3.0
     */
    private final int activeRemoteDataConnectionModeServerPort;

    public FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize, String temporaryFileDirectoryPath, String temporaryFilePrefix,
            String temporaryFileSuffix, int fileCopyMemoryBufferSize, int dataConnectionMode, int dataTimeout,
            String activeRemoteDataConnectionModeServerHost, int activeRemoteDataConnectionModeServerPort
    ) {
        this(
                host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize,
                dataConnectionMode, dataTimeout, activeRemoteDataConnectionModeServerHost,
                activeRemoteDataConnectionModeServerPort, false
        );
    }

    private FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize, String temporaryFileDirectoryPath, String temporaryFilePrefix,
            String temporaryFileSuffix, int fileCopyMemoryBufferSize, int dataConnectionMode, int dataTimeout,
            String activeRemoteDataConnectionModeServerHost, int activeRemoteDataConnectionModeServerPort,
            boolean paramReliable
    ) {
        // 如果参数不可靠，则检查参数。
        if (!paramReliable) {
            FtpConfigUtil.checkHost(host);
            FtpConfigUtil.checkPort(port);
            FtpConfigUtil.checkUsername(username);
            FtpConfigUtil.checkPassword(password);
            FtpConfigUtil.checkServerCharset(serverCharset);
            FtpConfigUtil.checkConnectTimeout(connectTimeout);
            FtpConfigUtil.checkNoopInterval(noopInterval, connectTimeout);
            FtpConfigUtil.checkBufferSize(bufferSize);
            FtpConfigUtil.checkTemporaryFileDirectoryPath(temporaryFileDirectoryPath);
            FtpConfigUtil.checkTemporaryFilePrefix(temporaryFilePrefix);
            FtpConfigUtil.checkTemporaryFileSuffix(temporaryFileSuffix);
            FtpConfigUtil.checkFileCopyMemoryBufferSize(fileCopyMemoryBufferSize);
            FtpConfigUtil.checkDataConnectionMode(dataConnectionMode);
            FtpConfigUtil.checkDataTimeout(dataTimeout);
            FtpConfigUtil.checkActiveRemoteDataConnectionModeServerHost(
                    activeRemoteDataConnectionModeServerHost, dataConnectionMode
            );
            FtpConfigUtil.checkActiveRemoteDataConnectionModeServerPort(
                    activeRemoteDataConnectionModeServerPort, dataConnectionMode
            );
        }
        // 设置值。
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.serverCharset = serverCharset;
        this.connectTimeout = connectTimeout;
        this.noopInterval = noopInterval;
        this.bufferSize = bufferSize;
        this.temporaryFileDirectoryPath = temporaryFileDirectoryPath;
        this.temporaryFilePrefix = temporaryFilePrefix;
        this.temporaryFileSuffix = temporaryFileSuffix;
        this.fileCopyMemoryBufferSize = fileCopyMemoryBufferSize;
        this.dataConnectionMode = dataConnectionMode;
        this.dataTimeout = dataTimeout;
        this.activeRemoteDataConnectionModeServerHost = activeRemoteDataConnectionModeServerHost;
        this.activeRemoteDataConnectionModeServerPort = activeRemoteDataConnectionModeServerPort;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerCharset() {
        return serverCharset;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public long getNoopInterval() {
        return noopInterval;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getTemporaryFileDirectoryPath() {
        return temporaryFileDirectoryPath;
    }

    public String getTemporaryFilePrefix() {
        return temporaryFilePrefix;
    }

    public String getTemporaryFileSuffix() {
        return temporaryFileSuffix;
    }

    public int getFileCopyMemoryBufferSize() {
        return fileCopyMemoryBufferSize;
    }

    public int getDataConnectionMode() {
        return dataConnectionMode;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public String getActiveRemoteDataConnectionModeServerHost() {
        return activeRemoteDataConnectionModeServerHost;
    }

    public int getActiveRemoteDataConnectionModeServerPort() {
        return activeRemoteDataConnectionModeServerPort;
    }

    @Override
    public String toString() {
        return "FtpConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", serverCharset='" + serverCharset + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", noopInterval=" + noopInterval +
                ", bufferSize=" + bufferSize +
                ", temporaryFileDirectoryPath='" + temporaryFileDirectoryPath + '\'' +
                ", temporaryFilePrefix='" + temporaryFilePrefix + '\'' +
                ", temporaryFileSuffix='" + temporaryFileSuffix + '\'' +
                ", fileCopyMemoryBufferSize=" + fileCopyMemoryBufferSize +
                ", dataConnectionMode=" + dataConnectionMode +
                ", dataTimeout=" + dataTimeout +
                ", activeRemoteDataConnectionModeServerHost='" + activeRemoteDataConnectionModeServerHost + '\'' +
                ", activeRemoteDataConnectionModeServerPort=" + activeRemoteDataConnectionModeServerPort +
                '}';
    }

    /**
     * FTP 配置构造器。
     *
     * @author DwArFeng
     * @since 1.1.9
     */
    public static final class Builder implements Buildable<FtpConfig> {

        /**
         * 数据连接模式：本地主动模式（传统主动模式 / PORT 模式）。
         *
         * @since 1.3.0
         */
        public static final int DATA_CONNECTION_MODE_ACTIVE_LOCAL = 0;

        /**
         * 数据连接模式：远程主动模式（反向主动模式）。
         *
         * @since 1.3.0
         */
        public static final int DATA_CONNECTION_MODE_ACTIVE_REMOTE = 1;

        /**
         * 数据连接模式：本地被动模式（传统被动模式 / PASV 模式）。
         */
        public static final int DATA_CONNECTION_MODE_PASSIVE_LOCALE = 2;

        /**
         * 数据连接模式：远程被动模式（反向被动模式）。
         */
        public static final int DATA_CONNECTION_MODE_PASSIVE_REMOTE = 3;

        public static final int DEFAULT_PORT = 21;
        public static final String DEFAULT_SERVER_CHARSET = "UTF-8";
        public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
        public static final long DEFAULT_NOOP_INTERVAL = 4000;
        public static final int DEFAULT_BUFFER_SIZE = 4096;

        /**
         * @since 1.2.0
         */
        public static final String DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");

        /**
         * @since 1.2.0
         */
        public static final String DEFAULT_TEMPORARY_FILE_PREFIX = "ftp-";
        /**
         * @since 1.2.0
         */
        public static final String DEFAULT_TEMPORARY_FILE_SUFFIX = ".tmp";

        /**
         * 默认的文件复制内存缓冲区大小。
         *
         * <p>
         * 1048576 bytes = 1 MiB。
         *
         * @since 1.2.0
         */
        public static final int DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE = 1048576;

        /**
         * 默认的数据连接模式。
         *
         * <p>
         * 默认值：本地主动模式（传统主动模式 / PORT 模式）。
         *
         * @since 1.3.0
         */
        public static final int DEFAULT_DATA_CONNECTION_MODE = DATA_CONNECTION_MODE_ACTIVE_LOCAL;

        /**
         * 默认的数据超时时间。
         *
         * <p>
         * 默认值：-1（永不超时）。
         *
         * @since 1.3.0
         */
        public static final int DEFAULT_DATA_TIMEOUT = -1;

        /**
         * 默认的远程主动数据连接模式下的服务主机地址。
         *
         * <p>
         * 默认值：null。
         * <p>
         * 当 <code>dataConnectionMode</code> 取值为 <code>1（远程主动模式）</code> 时，默认值将无法通过检查，
         * 在此条件下，请勿使用默认值。
         */
        public static final String DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST = null;

        /**
         * 默认的远程主动数据连接模式下的服务主机端口。
         *
         * <p>
         * 默认值：-1。
         * <p>
         * 当 <code>dataConnectionMode</code> 取值为 <code>1（远程主动模式）</code> 时，默认值将无法通过检查，
         * 在此条件下，请勿使用默认值。
         */
        public static final int DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT = -1;

        private final String host;
        private final String username;
        private final String password;

        private int port = DEFAULT_PORT;
        private String serverCharset = DEFAULT_SERVER_CHARSET;
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private long noopInterval = DEFAULT_NOOP_INTERVAL;
        private int bufferSize = DEFAULT_BUFFER_SIZE;
        private String temporaryFileDirectoryPath = DEFAULT_TEMPORARY_FILE_DIRECTORY_PATH;
        private String temporaryFilePrefix = DEFAULT_TEMPORARY_FILE_PREFIX;
        private String temporaryFileSuffix = DEFAULT_TEMPORARY_FILE_SUFFIX;
        private int fileCopyMemoryBufferSize = DEFAULT_FILE_COPY_MEMORY_BUFFER_SIZE;
        private int dataConnectionMode = DEFAULT_DATA_CONNECTION_MODE;
        private int dataTimeout = DEFAULT_DATA_TIMEOUT;
        private String activeRemoteDataConnectionModeServerHost
                = DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_HOST;
        private int activeRemoteDataConnectionModeServerPort = DEFAULT_ACTIVE_REMOTE_DATA_CONNECTION_MODE_SERVER_PORT;

        public Builder(String host, String username, String password) {
            // 检查参数。
            FtpConfigUtil.checkHost(host);
            FtpConfigUtil.checkUsername(username);
            FtpConfigUtil.checkPassword(password);

            // 设置值。
            this.host = host;
            this.username = username;
            this.password = password;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setServerCharset(String serverCharset) {
            this.serverCharset = serverCharset;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setNoopInterval(long noopInterval) {
            this.noopInterval = noopInterval;
            return this;
        }

        public Builder setBufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder setTemporaryFileDirectoryPath(String temporaryFileDirectoryPath) {
            this.temporaryFileDirectoryPath = temporaryFileDirectoryPath;
            return this;
        }

        public Builder setTemporaryFilePrefix(String temporaryFilePrefix) {
            this.temporaryFilePrefix = temporaryFilePrefix;
            return this;
        }

        public Builder setTemporaryFileSuffix(String temporaryFileSuffix) {
            this.temporaryFileSuffix = temporaryFileSuffix;
            return this;
        }

        public Builder setFileCopyMemoryBufferSize(int fileCopyMemoryBufferSize) {
            this.fileCopyMemoryBufferSize = fileCopyMemoryBufferSize;
            return this;
        }

        public Builder setDataConnectionMode(int dataConnectionMode) {
            this.dataConnectionMode = dataConnectionMode;
            return this;
        }

        public Builder setDataTimeout(int dataTimeout) {
            this.dataTimeout = dataTimeout;
            return this;
        }

        public Builder setActiveRemoteDataConnectionModeServerHost(String activeRemoteDataConnectionModeServerHost) {
            this.activeRemoteDataConnectionModeServerHost = activeRemoteDataConnectionModeServerHost;
            return this;
        }

        public Builder setActiveRemoteDataConnectionModeServerPort(int activeRemoteDataConnectionModeServerPort) {
            this.activeRemoteDataConnectionModeServerPort = activeRemoteDataConnectionModeServerPort;
            return this;
        }

        @Override
        public FtpConfig build() {
            // 检查参数。
            FtpConfigUtil.checkHost(host);
            FtpConfigUtil.checkPort(port);
            FtpConfigUtil.checkUsername(username);
            FtpConfigUtil.checkPassword(password);
            FtpConfigUtil.checkServerCharset(serverCharset);
            FtpConfigUtil.checkConnectTimeout(connectTimeout);
            FtpConfigUtil.checkNoopInterval(noopInterval, connectTimeout);
            FtpConfigUtil.checkBufferSize(bufferSize);
            FtpConfigUtil.checkTemporaryFileDirectoryPath(temporaryFileDirectoryPath);
            FtpConfigUtil.checkTemporaryFilePrefix(temporaryFilePrefix);
            FtpConfigUtil.checkTemporaryFileSuffix(temporaryFileSuffix);
            FtpConfigUtil.checkFileCopyMemoryBufferSize(fileCopyMemoryBufferSize);
            FtpConfigUtil.checkDataConnectionMode(dataConnectionMode);
            FtpConfigUtil.checkDataTimeout(dataTimeout);
            FtpConfigUtil.checkActiveRemoteDataConnectionModeServerHost(
                    activeRemoteDataConnectionModeServerHost, dataConnectionMode
            );
            FtpConfigUtil.checkActiveRemoteDataConnectionModeServerPort(
                    activeRemoteDataConnectionModeServerPort, dataConnectionMode
            );

            // 构造并返回配置。
            return new FtpConfig(
                    host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                    temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize,
                    dataConnectionMode, dataTimeout, activeRemoteDataConnectionModeServerHost,
                    activeRemoteDataConnectionModeServerPort, true
            );
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "host='" + host + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", port=" + port +
                    ", serverCharset='" + serverCharset + '\'' +
                    ", connectTimeout=" + connectTimeout +
                    ", noopInterval=" + noopInterval +
                    ", bufferSize=" + bufferSize +
                    ", temporaryFileDirectoryPath='" + temporaryFileDirectoryPath + '\'' +
                    ", temporaryFilePrefix='" + temporaryFilePrefix + '\'' +
                    ", temporaryFileSuffix='" + temporaryFileSuffix + '\'' +
                    ", fileCopyMemoryBufferSize=" + fileCopyMemoryBufferSize +
                    ", dataConnectionMode=" + dataConnectionMode +
                    ", dataTimeout=" + dataTimeout +
                    ", activeRemoteDataConnectionModeServerHost='" + activeRemoteDataConnectionModeServerHost + '\'' +
                    ", activeRemoteDataConnectionModeServerPort=" + activeRemoteDataConnectionModeServerPort +
                    '}';
        }
    }
}
