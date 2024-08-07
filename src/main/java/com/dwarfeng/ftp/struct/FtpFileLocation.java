package com.dwarfeng.ftp.struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * FTP 文件位置。
 *
 * <p>
 * 文件位置是一个便捷类，用于封装 FTP 服务器上的文件的位置信息，它包括文件的路径和文件的名称。
 *
 * <p>
 * 该类是一个不可变类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class FtpFileLocation {

    private final String[] filePaths;
    private final String fileName;

    public FtpFileLocation(@Nonnull String[] filePaths, @Nullable String fileName) {
        this.filePaths = filePaths;
        this.fileName = fileName;
    }

    @Nonnull
    public String[] getFilePaths() {
        return filePaths;
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FtpFileLocation that = (FtpFileLocation) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(filePaths, that.filePaths)) return false;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(filePaths);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FtpFileLocation{" +
                "filePaths=" + Arrays.toString(filePaths) +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
