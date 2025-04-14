package com.dwarfeng.ftp.util;

import com.dwarfeng.ftp.struct.FtpConfig;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
     * 数据连接模式空间。
     *
     * @since 1.3.0
     */
    private static final Set<Integer> DATA_CONNECTION_MODE_SPACE;

    static {
        Set<Integer> DATA_CONNECTION_MODE_SPACE_DEJA_VU = new HashSet<>();
        DATA_CONNECTION_MODE_SPACE_DEJA_VU.add(FtpConfig.Builder.DATA_CONNECTION_MODE_ACTIVE_LOCAL);
        DATA_CONNECTION_MODE_SPACE_DEJA_VU.add(FtpConfig.Builder.DATA_CONNECTION_MODE_ACTIVE_REMOTE);
        DATA_CONNECTION_MODE_SPACE_DEJA_VU.add(FtpConfig.Builder.DATA_CONNECTION_MODE_PASSIVE_LOCALE);
        DATA_CONNECTION_MODE_SPACE_DEJA_VU.add(FtpConfig.Builder.DATA_CONNECTION_MODE_PASSIVE_REMOTE);
        DATA_CONNECTION_MODE_SPACE = Collections.unmodifiableSet(DATA_CONNECTION_MODE_SPACE_DEJA_VU);
    }

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
    @SuppressWarnings({"unused", "EmptyMethod"})
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
    @SuppressWarnings({"unused", "EmptyMethod"})
    public static void checkBufferSize(int bufferSize) {
        // bufferSize 允许为负数，因为负数表示使用系统默认值。
        // 因此无论如何都不会抛出异常。
    }

    /**
     * 检查指定的临时文件目录路径是否合法。
     *
     * @param temporaryFileDirectoryPath 指定的临时文件目录路径。
     * @since 1.2.0
     */
    public static void checkTemporaryFileDirectoryPath(String temporaryFileDirectoryPath) {
        if (Objects.isNull(temporaryFileDirectoryPath)) {
            throw new NullPointerException("临时文件目录路径不能为 null");
        }
        File temporaryFileDirectory = new File(temporaryFileDirectoryPath);
        // 确认目录是否存在，不存在则尝试创建，创建失败则抛出异常。
        if (!temporaryFileDirectory.exists()) {
            if (!temporaryFileDirectory.mkdirs()) {
                throw new IllegalArgumentException("无法创建临时文件目录");
            }
        }
        // 目录不能是文件。
        if (temporaryFileDirectory.isFile()) {
            throw new IllegalArgumentException("临时文件目录不能是文件");
        }
        // 需要有读写权限。
        if (!temporaryFileDirectory.canRead() || !temporaryFileDirectory.canWrite()) {
            throw new IllegalArgumentException("临时文件目录需要有读写权限");
        }
    }

    /**
     * 检查指定的临时文件前缀是否合法。
     *
     * @param temporaryFilePrefix 指定的临时文件前缀。
     * @since 1.2.0
     */
    public static void checkTemporaryFilePrefix(String temporaryFilePrefix) {
        if (Objects.isNull(temporaryFilePrefix)) {
            throw new NullPointerException("临时文件前缀不能为 null");
        }
    }

    /**
     * 检查指定的临时文件后缀是否合法。
     *
     * @param temporaryFileSuffix 指定的临时文件后缀。
     * @since 1.2.0
     */
    public static void checkTemporaryFileSuffix(String temporaryFileSuffix) {
        if (Objects.isNull(temporaryFileSuffix)) {
            throw new NullPointerException("临时文件后缀不能为 null");
        }
    }

    /**
     * 检查指定的文件复制内存缓冲区大小是否合法。
     *
     * @param fileCopyMemoryBufferSize 指定的文件复制内存缓冲区大小。
     * @since 1.2.0
     */
    public static void checkFileCopyMemoryBufferSize(int fileCopyMemoryBufferSize) {
        if (fileCopyMemoryBufferSize <= 0) {
            throw new IllegalArgumentException("文件复制内存缓冲区大小必须大于 0");
        }
    }

    /**
     * 检查指定的数据连接模式是否合法。
     *
     * @param dataConnectionMode 指定的数据连接模式。
     * @since 1.3.0
     */
    public static void checkDataConnectionMode(int dataConnectionMode) {
        if (!DATA_CONNECTION_MODE_SPACE.contains(dataConnectionMode)) {
            throw new IllegalArgumentException("数据连接模式 " + dataConnectionMode + " 非法");
        }
    }

    /**
     * 检查指定的数据超时时间是否合法。
     *
     * @param dataTimeout 指定的数据超时时间。
     * @since 1.3.0
     */
    @SuppressWarnings({"unused", "EmptyMethod"})
    public static void checkDataTimeout(int dataTimeout) {
        // dataTimeout 允许为 0 或负数，因为 0 或负数表示用不超时。
        // 因此无论如何都不会抛出异常。
    }

    /**
     * 检查远程主动数据连接模式的服务主机地址是否合法。
     *
     * @param activeRemoteDataConnectionModeServerHost 指定的远程主动数据连接模式的服务主机地址。
     * @param refDataConnectionMode                    参考的指定的数据连接模式。
     * @since 1.3.0
     */
    public static void checkActiveRemoteDataConnectionModeServerHost(
            String activeRemoteDataConnectionModeServerHost, int refDataConnectionMode
    ) {
        // 如果开发的数据连接模式不是主动远程数据连接模式，则直接返回。
        if (!Objects.equals(refDataConnectionMode, FtpConfig.Builder.DATA_CONNECTION_MODE_ACTIVE_REMOTE)) {
            return;
        }
        if (Objects.isNull(activeRemoteDataConnectionModeServerHost)) {
            throw new NullPointerException("远程主动数据连接模式下的服务主机地址不能为 null");
        }
    }

    /**
     * 检查远程主动数据连接模式的服务端口是否合法。
     *
     * @param activeRemoteDataConnectionModeServerPort 指定的远程主动数据连接模式的服务端口。
     * @param refDataConnectionMode                    参考的指定的数据连接模式。
     */
    public static void checkActiveRemoteDataConnectionModeServerPort(
            int activeRemoteDataConnectionModeServerPort, int refDataConnectionMode
    ) {
        // 如果开发的数据连接模式不是主动远程数据连接模式，则直接返回。
        if (!Objects.equals(refDataConnectionMode, FtpConfig.Builder.DATA_CONNECTION_MODE_ACTIVE_REMOTE)) {
            return;
        }
        if (activeRemoteDataConnectionModeServerPort < 0 || activeRemoteDataConnectionModeServerPort > 65535) {
            throw new IllegalArgumentException("远程主动数据连接模式下的服务端口必须在 0 - 65535 之间");
        }
    }

    private FtpConfigUtil() {
        throw new IllegalStateException("禁止外部实例化");
    }
}
