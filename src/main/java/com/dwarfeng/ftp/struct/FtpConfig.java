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

    public FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize, String temporaryFileDirectoryPath, String temporaryFilePrefix,
            String temporaryFileSuffix, int fileCopyMemoryBufferSize
    ) {
        this(
                host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize, false
        );
    }

    private FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize, String temporaryFileDirectoryPath, String temporaryFilePrefix,
            String temporaryFileSuffix, int fileCopyMemoryBufferSize, boolean paramReliable
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
                '}';
    }

    /**
     * FTP 配置构造器。
     *
     * @author DwArFeng
     * @since 1.1.9
     */
    public static final class Builder implements Buildable<FtpConfig> {

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

            // 构造并返回配置。
            return new FtpConfig(
                    host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize,
                    temporaryFileDirectoryPath, temporaryFilePrefix, temporaryFileSuffix, fileCopyMemoryBufferSize, true
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
                    '}';
        }
    }
}
