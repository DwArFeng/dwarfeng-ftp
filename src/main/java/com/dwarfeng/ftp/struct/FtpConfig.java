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

    public FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize
    ) {
        this(
                host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize, false
        );
    }

    private FtpConfig(
            String host, int port, String username, String password, String serverCharset, int connectTimeout,
            long noopInterval, int bufferSize, boolean paramReliable
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

        private final String host;
        private final String username;
        private final String password;

        private int port = DEFAULT_PORT;
        private String serverCharset = DEFAULT_SERVER_CHARSET;
        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private long noopInterval = DEFAULT_NOOP_INTERVAL;
        private int bufferSize = DEFAULT_BUFFER_SIZE;

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
            // 构造并返回配置。
            return new FtpConfig(
                    host, port, username, password, serverCharset, connectTimeout, noopInterval, bufferSize, true
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
                    '}';
        }
    }
}
