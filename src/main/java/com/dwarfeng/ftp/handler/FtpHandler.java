package com.dwarfeng.ftp.handler;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FTP 处理器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface FtpHandler extends StartableHandler {

    /**
     * 执行连接动作。
     *
     * @throws HandlerException 处理器异常。
     */
    void connect() throws HandlerException;

    /**
     * 执行断开连接动作。
     *
     * @throws HandlerException 处理器异常。
     */
    void disconnect() throws HandlerException;

    /**
     * 检查文件是否存在。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件是否存在。
     * @throws HandlerException 处理器异常。
     */
    boolean existsFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 检查文件是否存在。
     *
     * @param fileLocation 文件位置。
     * @return 文件是否存在。
     * @throws HandlerException 处理器异常。
     * @see #existsFile(String[], String)
     */
    boolean existsFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 存储文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @param content   文件的内容。
     * @throws HandlerException 处理器异常。
     */
    void storeFile(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull byte[] content
    ) throws HandlerException;

    /**
     * 存储文件。
     *
     * @param fileLocation 文件位置。
     * @param content      文件的内容。
     * @throws HandlerException 处理器异常。
     * @see #storeFile(String[], String, byte[])
     */
    void storeFile(@Nonnull FtpFileLocation fileLocation, @Nonnull byte[] content) throws HandlerException;

    /**
     * 获取文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的内容。
     * @throws HandlerException 处理器异常。
     */
    byte[] retrieveFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 获取文件。
     *
     * @param fileLocation 文件位置。
     * @return 文件的内容。
     * @throws HandlerException 处理器异常。
     * @see #retrieveFile(String[], String)
     */
    byte[] retrieveFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 获取文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的内容。
     * @throws HandlerException 处理器异常。
     * @deprecated 该方法命名不规范，get 一般用于直接获取对象，不应该用于需要消耗时间的过程方法。<br>
     * 请使用 {@link #retrieveFile(String[], String)}。
     */
    @Deprecated
    default byte[] getFileContent(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException {
        return retrieveFile(filePaths, fileName);
    }

    /**
     * 通过流的形式存储文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @param in        文件的输入流。
     * @throws HandlerException 处理器异常。
     */
    void storeFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull InputStream in
    ) throws HandlerException;

    /**
     * 通过流的形式存储文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param fileLocation 文件位置。
     * @param in           文件的输入流。
     * @throws HandlerException 处理器异常。
     * @see #storeFileByStream(String[], String, InputStream)
     */
    void storeFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称
     * @param out       待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     */
    void retrieveFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param fileLocation 文件位置。
     * @param out          待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @see #retrieveFileByStream(String[], String, OutputStream)
     */
    void retrieveFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称
     * @param out       待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @deprecated 该方法命名不规范，get 一般用于直接获取对象，不应该用于需要消耗时间的过程方法。<br>
     * 请使用 {@link #retrieveFileByStream(String[], String, OutputStream)}。
     */
    @Deprecated
    default void getFileContentByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws HandlerException {
        retrieveFileByStream(filePaths, fileName, out);
    }

    /**
     * 删除文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @throws HandlerException 处理器异常。
     */
    void deleteFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 删除文件。
     *
     * @param fileLocation 文件位置。
     * @throws HandlerException 处理器异常。
     * @see #deleteFile(String[], String)
     */
    void deleteFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 删除目录。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @throws HandlerException 处理器异常。
     * @deprecated 该方法命名不规范，请使用 {@link #removeDirectory(String[])}。
     */
    @Deprecated
    default void removeDirectory(@Nonnull String[] filePaths, @Nonnull String directoryName) throws HandlerException {
        String[] neoFilePaths = new String[filePaths.length + 1];
        System.arraycopy(filePaths, 0, neoFilePaths, 0, filePaths.length);
        neoFilePaths[filePaths.length] = directoryName;
        removeDirectory(neoFilePaths);
    }

    /**
     * 删除目录。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。<br>
     *                  如果需要删除的文件夹是根文件夹，那么该数组长度为 0。
     * @throws HandlerException 处理器异常。
     */
    void removeDirectory(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 删除目录。
     *
     * <p>
     * 执行该方法时，文件路径取文件夹，如路径包含文件名，则忽略。
     *
     * @see #removeDirectory(String[])
     */
    void removeDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @return 指定文件夹下所有文件的名称（不带路径前缀）组成的数组。
     * @throws HandlerException 处理器异常。
     */
    FtpFile[] listFiles(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，文件路径取文件夹，如路径包含文件名，则忽略。
     *
     * @see #listFiles(String[])
     */
    FtpFile[] listFiles(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 如果有专用的文件夹（这种文件夹通常只存放一种类型的文件），那么直接获取名称会更方便一些。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @return 指定文件夹下所有文件的名称（不带路径前缀）组成的数组。
     * @throws HandlerException 处理器异常。
     */
    String[] listFileNames(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，文件路径取文件夹，如路径包含文件名，则忽略。
     *
     * @see #listFileNames(String[])
     */
    String[] listFileNames(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 打开指定文件的输入流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法会在调用前检查 FTP 服务器的状态，如果 FTP 服务器不可用，会抛出异常。
     *
     * <p>
     * 文件在读取过程中，如果 FTP 服务器不可用，会抛出 {@link java.io.IOException} 异常。
     *
     * <p>
     * 该方法不会关闭流，需要调用者自行关闭，请 <b>务必</b> 在调用该方法结束后关闭流，否则会造成 FTP 服务器行为异常。
     *
     * <p>
     * 对于线程安全的实现，从调用开始直到用户关闭流的这段时间内，其它线程调用处理器的任何方法都应该被阻塞。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     */
    InputStream openInputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输入流。
     *
     * @param fileLocation 文件位置
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     */
    InputStream openInputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 打开指定文件的输出流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法会在调用前检查 FTP 服务器的状态，如果 FTP 服务器不可用，会抛出异常。
     *
     * <p>
     * 文件在写入过程中，如果 FTP 服务器不可用，会抛出 {@link java.io.IOException} 异常。
     *
     * <p>
     * 该方法不会关闭流，需要调用者自行关闭，请 <b>务必</b> 在调用该方法结束后关闭流，否则会造成 FTP 服务器行为异常。
     *
     * <p>
     * 对于线程安全的实现，从调用开始直到用户关闭流的这段时间内，其它线程调用处理器的任何方法都应该被阻塞。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     */
    OutputStream openOutputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输出流。
     *
     * @param fileLocation 文件位置。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     * @see #openOutputStream(String[], String)
     */
    OutputStream openOutputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 重命名文件。
     *
     * @param oldFilePaths 旧的文件夹的路径。<br>
     *                     路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的文件夹的路径。<br>
     *                     路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     */
    void renameFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 重命名文件。
     *
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see #renameFile(String[], String, String[], String)
     */
    void renameFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException;
}
