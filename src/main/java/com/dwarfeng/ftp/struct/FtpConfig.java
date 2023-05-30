package com.dwarfeng.ftp.struct;

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
}
