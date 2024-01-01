package com.dwarfeng.ftp.util;

import java.util.Objects;

/**
 * FTP 配置工具类。
 *
 * @author DwArFeng
 * @since 1.1.9
 */
public final class FtpConfigUtil {

    /**
     * 最小超时时间。
     */
    private static final int MIN_CONNECT_TIMEOUT = 1000;

    /**
     * 检查指定的主机是否合法。
     *
     * @param host 指定的主机。
     */
    public static void checkHost(String host) {
        if (Objects.isNull(host)) {
            throw new NullPointerException("主机不能为 null");
        }
    }

    /**
     * 检查指定的用户名是否合法。
     *
     * @param username 指定的用户名。
     */
    public static void checkUsername(String username) {
        if (Objects.isNull(username)) {
            throw new NullPointerException("用户名不能为 null");
        }
    }

    /**
     * 检查指定的密码是否合法。
     *
     * @param password 指定的密码。
     */
    @SuppressWarnings("unused")
    public static void checkPassword(String password) {
        // 无论密码是否为 null，都不会抛出异常。
    }

    /**
     * 检查指定的端口是否合法。
     *
     * @param port 指定的端口。
     */
    public static void checkPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("端口号必须在 0 - 65535 之间");
        }
    }

    /**
     * 检查指定的服务器字符集是否合法。
     *
     * @param serverCharset 指定的服务器字符集。
     */
    public static void checkServerCharset(String serverCharset) {
        if (Objects.isNull(serverCharset)) {
            throw new NullPointerException("服务器字符集不能为 null");
        }
    }

    /**
     * 检查指定的连接超时时间是否合法。
     *
     * @param connectTimeout 指定的连接超时时间。
     */
    public static void checkConnectTimeout(int connectTimeout) {
        if (connectTimeout <= MIN_CONNECT_TIMEOUT) {
            throw new IllegalArgumentException("配置 connectTimeout 的值太小，应该大于 " + MIN_CONNECT_TIMEOUT);
        }
    }

    /**
     * 检查指定的 NOOP 间隔时间是否合法。
     *
     * @param noopInterval      指定的 NOOP 间隔时间。
     * @param refConnectTimeout 参考的连接超时时间。
     */
    public static void checkNoopInterval(long noopInterval, long refConnectTimeout) {
        if (noopInterval >= refConnectTimeout) {
            throw new IllegalArgumentException(
                    "配置 noopInterval 的值太大，应该小于 connectTimeout（" + refConnectTimeout + "）"
            );
        }
    }

    /**
     * 检查指定的缓冲区大小是否合法。
     *
     * @param bufferSize 指定的缓冲区大小。
     */
    @SuppressWarnings("unused")
    public static void checkBufferSize(int bufferSize) {
        // bufferSize 允许为负数，因为负数表示使用系统默认值。
        // 因此无论如何都不会抛出异常。
    }

    private FtpConfigUtil() {
        throw new IllegalStateException("禁止外部实例化");
    }
}
