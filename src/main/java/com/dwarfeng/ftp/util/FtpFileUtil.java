package com.dwarfeng.ftp.util;

import com.dwarfeng.ftp.bean.dto.FtpFile;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * FTP 文件工具类。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
public final class FtpFileUtil {

    /**
     * 测试 FTP 文件是否为文件。
     *
     * @param ftpFile 指定的 FTP 文件。
     * @return FTP 文件是否为文件。
     */
    public static boolean isFile(@Nonnull FtpFile ftpFile) {
        return ftpFileTypeEquals(ftpFile, Constants.FTP_FILE_TYPE_FILE);
    }

    /**
     * 测试 FTP 文件是否为文件夹。
     *
     * @param ftpFile 指定的 FTP 文件。
     * @return FTP 文件是否为文件夹。
     */
    public static boolean isDirectory(@Nonnull FtpFile ftpFile) {
        return ftpFileTypeEquals(ftpFile, Constants.FTP_FILE_TYPE_DIRECTORY);
    }

    /**
     * 测试 FTP 文件是否为符号链接。
     *
     * @param ftpFile 指定的 FTP 文件。
     * @return FTP 文件是否为符号链接。
     */
    public static boolean isSymbolicLink(@Nonnull FtpFile ftpFile) {
        return ftpFileTypeEquals(ftpFile, Constants.FTP_FILE_TYPE_SYMBOLIC_LINK);
    }

    /**
     * 测试 FTP 文件是否为未知类型。
     *
     * @param ftpFile 指定的 FTP 文件。
     * @return FTP 文件是否为未知类型。
     */
    public static boolean isUnknown(@Nonnull FtpFile ftpFile) {
        return ftpFileTypeEquals(ftpFile, Constants.FTP_FILE_TYPE_UNKNOWN);
    }

    /**
     * 测试 FTP 文件是否为文件。
     *
     * @param ftpFile 指定的 FTP 文件。
     * @return FTP 文件是否为文件。
     */
    private static boolean ftpFileTypeEquals(FtpFile ftpFile, int type) {
        return Objects.equals(ftpFile.getType(), type);
    }

    private FtpFileUtil() {
        throw new IllegalStateException("禁止实例化");
    }
}
