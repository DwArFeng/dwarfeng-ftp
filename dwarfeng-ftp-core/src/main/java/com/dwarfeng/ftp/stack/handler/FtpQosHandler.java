package com.dwarfeng.ftp.stack.handler;

import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.exception.HandlerException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * FTP QoS 处理器。
 *
 * <p>
 * 参数 <code>handlerName</code> 为对应 {@link com.dwarfeng.ftp.stack.handler.FtpHandler} 实例的 <code>bean name</code>。<br>
 * 当应用上下文中只有一个 {@link FtpHandler} 时，参数 <code>handlerName</code> 可以为 <code>null</code>。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
public interface FtpQosHandler {

    /**
     * 列出所有 FTP 处理器名称。
     *
     * <p>
     * 返回结果按字典序排序且不可变。
     *
     * @return 所有处理器的名称组成的列表（按字典序排序，不可变）。
     * @throws HandlerException 处理器异常。
     */
    List<String> listHandlerNames() throws HandlerException;

    /**
     * 停止全部 FTP 托管处理器。
     *
     * <p>
     * 按处理器名称的字典序依次调用各 {@link FtpHandler} 的 {@link FtpHandler#stop()}，适用于容器销毁等多实例停机场景。<br>
     * 若某个处理器停止失败，将记录告警并继续尝试停止其余处理器，不因单次失败中断整体流程；
     * 遍历映射本身的非预期错误仍会以 {@link HandlerException} 形式抛出。
     *
     * @throws HandlerException 处理器异常。
     */
    void stopAllManagedHandlers() throws HandlerException;

    /**
     * 查询 FTP 处理器是否已启动。
     *
     * @param handlerName 处理器名称。
     * @return 目标处理器是否已启动。
     * @throws HandlerException 处理器异常。
     */
    boolean isStarted(@Nullable String handlerName) throws HandlerException;

    /**
     * 启动 FTP 处理器。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void start(@Nullable String handlerName) throws HandlerException;

    /**
     * 停止 FTP 处理器。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void stop(@Nullable String handlerName) throws HandlerException;

    /**
     * 检查 FTP 文件是否存在。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    boolean existsFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 检查 FTP 文件是否存在。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    boolean existsFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 存储 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void storeFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull byte[] content
    ) throws HandlerException;

    /**
     * 存储 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void storeFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull byte[] content
    ) throws HandlerException;

    /**
     * 获取 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 获取 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    byte[] retrieveFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 通过流存储 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull InputStream in
    ) throws HandlerException;

    /**
     * 通过流存储 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void storeFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in
    ) throws HandlerException;

    /**
     * 通过流获取 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws HandlerException;

    /**
     * 通过流获取 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void retrieveFileByStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out
    ) throws HandlerException;

    /**
     * 删除 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void deleteFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 删除 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void deleteFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 删除 FTP 目录。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void removeDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException;

    /**
     * 删除 FTP 目录。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void removeDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 列出 FTP 目录文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException;

    /**
     * 列出 FTP 目录文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    FtpFile[] listFiles(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 列出 FTP 目录文件名。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException;

    /**
     * 列出 FTP 目录文件名。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    String[] listFileNames(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 打开 FTP 文件输入流。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 打开 FTP 文件输入流。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    InputStream openInputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 打开 FTP 文件输出流。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 打开 FTP 文件输出流。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    OutputStream openOutputStream(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 重命名 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void renameFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 重命名 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void renameFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws HandlerException;

    /**
     * 清空 FTP 目录。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void clearDirectory(
            @Nullable String handlerName,
            @Nonnull String[] filePaths
    ) throws HandlerException;

    /**
     * 清空 FTP 目录。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void clearDirectory(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 复制 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void copyFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 复制 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    void copyFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws HandlerException;

    /**
     * 描述 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull String[] filePaths, @Nonnull String fileName
    ) throws HandlerException;

    /**
     * 描述 FTP 文件。
     *
     * @param handlerName 处理器名称。
     * @throws HandlerException 处理器异常。
     */
    FtpFile descFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation fileLocation
    ) throws HandlerException;

    /**
     * 移动 FTP 文件。
     *
     * @param handlerName  处理器名称。
     * @param oldFilePaths 旧的目录的路径。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @see FtpQosHandler#renameFile(String, String[], String, String[], String)
     */
    default void moveFile(
            @Nullable String handlerName,
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException {
        renameFile(handlerName, oldFilePaths, oldFileName, neoFilePaths, neoFileName);
    }

    /**
     * 移动 FTP 文件。
     *
     * @param handlerName     处理器名称。
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see FtpQosHandler#moveFile(String, String[], String, String[], String)
     */
    default void moveFile(
            @Nullable String handlerName,
            @Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation
    ) throws HandlerException {
        renameFile(handlerName, oldFileLocation, neoFileLocation);
    }
}
