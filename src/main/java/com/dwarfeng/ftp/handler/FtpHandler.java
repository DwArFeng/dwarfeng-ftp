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
     * @deprecated 该处理器实现 {@link StartableHandler} 接口，请使用 {@link #start()} 方法执行连接动作。
     */
    @Deprecated
    void connect() throws HandlerException;

    /**
     * 执行断开连接动作。
     *
     * @throws HandlerException 处理器异常。
     * @deprecated 该处理器实现 {@link StartableHandler} 接口，请使用 {@link #stop()} 方法执行断开连接动作。
     */
    @Deprecated
    void disconnect() throws HandlerException;

    /**
     * 检查文件是否存在。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @param in        文件的输入流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.3
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
     * @since 1.1.10
     */
    void storeFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称
     * @param out       待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
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
     * @since 1.1.10
     */
    void retrieveFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称
     * @param out       待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.3
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
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
     * <p>
     * 在 FTP 协议中，删除一个目录，需要保证该目录下没有文件，否则会抛出异常。
     *
     * <p>
     * 如果希望删除一个包含文件的目录，需要先递归地删除目录下的所有文件和子目录。
     * 此时，可以使用 {@link #clearDirectory(String[])} 方法，该方法会清空目录下的所有文件，包括子目录。<br>
     * {@link #clearDirectory(String[])} 调用完成后，再调用本方法删除目录本身。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。<br>
     *                  如果需要删除的目录是根目录，那么该数组长度为 0。
     * @throws HandlerException 处理器异常。
     * @see #clearDirectory(String[])
     * @since 1.1.8
     */
    void removeDirectory(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 删除目录。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * <p>
     * 在 FTP 协议中，删除一个目录，需要保证该目录下没有文件，否则会抛出异常。
     *
     * <p>
     * 如果希望删除一个包含文件的目录，需要先递归地删除目录下的所有文件和子目录。
     * 此时，可以使用 {@link #clearDirectory(String[])} 方法，该方法会清空目录下的所有文件，包括子目录。<br>
     * {@link #clearDirectory(String[])} 调用完成后，再调用本方法删除目录本身。
     *
     * @see #removeDirectory(String[])
     * @see #clearDirectory(FtpFileLocation)
     * @since 1.1.10
     */
    void removeDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @return 指定目录下所有文件组成的数组。
     * @throws HandlerException 处理器异常。
     * @since 1.1.4
     */
    FtpFile[] listFiles(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * @param fileLocation 文件位置。
     * @return 指定目录下所有文件组成的数组。
     * @see #listFiles(String[])
     * @since 1.1.10
     */
    FtpFile[] listFiles(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 如果有专用的目录（这种目录通常只存放一种类型的文件），那么直接获取名称会更方便一些。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @return 指定目录下所有文件的名称（不带路径前缀）组成的数组。
     * @throws HandlerException 处理器异常。
     * @since 1.1.4
     */
    String[] listFileNames(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * @param fileLocation 文件位置。
     * @return 指定目录下所有文件的名称（不带路径前缀）组成的数组。
     * @see #listFileNames(String[])
     * @since 1.1.10
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
     */
    InputStream openInputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输入流。
     *
     * @param fileLocation 文件位置
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     * @see #openInputStream(String[], String)
     * @since 1.1.10
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
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
     */
    OutputStream openOutputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输出流。
     *
     * @param fileLocation 文件位置。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     * @see #openOutputStream(String[], String)
     * @since 1.1.10
     */
    OutputStream openOutputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 重命名文件。
     *
     * @param oldFilePaths 旧的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @since 1.1.8
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
     * @since 1.1.10
     */
    void renameFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException;

    /**
     * 清空目录。
     *
     * <p>
     * 递归地清空目录下的所有文件以及子目录。清空目录不会删除目录本身。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要按照递归逻辑多次列出目录下的文件并分别多次调用删除文件以及删除目录的方法，
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    void clearDirectory(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 清空目录。
     *
     * <p>
     * 递归地清空目录下的所有文件以及子目录。清空目录不会删除目录本身。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要按照递归逻辑多次列出目录下的文件并分别多次调用删除文件以及删除目录的方法，
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param fileLocation 文件位置。
     * @throws HandlerException 处理器异常。
     * @see #clearDirectory(String[])
     * @since 1.2.0
     */
    void clearDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 复制文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先获取源文件的内容，再存储到目标文件中。
     * 对于大文件，可能还需要将文件存储到文件系统中。
     * 该方法的执行时间与文件的大小呈正相关。尽量不要在大文件上调用该方法。
     *
     * @param oldFilePaths 旧的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    void copyFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 复制文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先获取源文件的内容，再存储到目标文件中。
     * 对于大文件，可能还需要将文件存储到文件系统中。
     * 该方法的执行时间与文件的大小呈正相关。尽量不要在大文件上调用该方法。
     *
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see #copyFile(String[], String, String[], String)
     * @since 1.2.0
     */
    void copyFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException;

    /**
     * 描述文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先列出目标文件所在目录下的所有文件，再查找目标文件。
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 指定文件对应的文件对象。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    FtpFile descFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 描述文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先列出目标文件所在目录下的所有文件，再查找目标文件。
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param fileLocation 文件位置。
     * @return 指定文件对应的文件对象。
     * @throws HandlerException 处理器异常。
     * @see #descFile(String[], String)
     * @since 1.2.0
     */
    FtpFile descFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 移动文件。
     *
     * <p>
     * 移动文件与重命名文件的作用以及实现是完全一样的。增加该方法是为了满足语义上的需求，以及部分开发人员的使用习惯。
     *
     * @param oldFilePaths 旧的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    default void moveFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException {
        renameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
    }

    /**
     * 移动文件。
     *
     * <p>
     * 移动文件与重命名文件的作用以及实现是完全一样的。增加该方法是为了满足语义上的需求，以及部分开发人员的使用习惯。
     *
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see #moveFile(String[], String, String[], String)
     * @since 1.2.0
     */
    default void moveFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException {
        renameFile(oldFileLocation, neoFileLocation);
    }
}
