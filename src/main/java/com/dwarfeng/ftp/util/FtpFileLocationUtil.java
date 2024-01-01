package com.dwarfeng.ftp.util;

import com.dwarfeng.ftp.struct.FtpFileLocation;

import java.util.Objects;

/**
 * FTP 文件位置工具类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class FtpFileLocationUtil {

    /**
     * 检查指定的文件位置是否是一个目录。
     *
     * @param fileLocation 指定的文件位置。
     * @return 指定的文件位置是否是一个目录。
     */
    public static boolean isDirectory(FtpFileLocation fileLocation) {
        return Objects.isNull(fileLocation.getFileName());
    }

    /**
     * 检查指定的文件位置作为文件是否合法。
     *
     * @param fileLocation 指定的文件位置。
     */
    public static void checkAsFile(FtpFileLocation fileLocation) {
        if (Objects.isNull(fileLocation)) {
            throw new NullPointerException("文件位置不能为 null");
        }
        if (Objects.isNull(fileLocation.getFileName())) {
            throw new NullPointerException("文件名不能为 null");
        }
    }

    private FtpFileLocationUtil() {
        throw new IllegalStateException("禁止外部实例化");
    }
}
