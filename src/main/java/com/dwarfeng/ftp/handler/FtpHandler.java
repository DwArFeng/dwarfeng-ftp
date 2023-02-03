package com.dwarfeng.ftp.handler;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import com.dwarfeng.subgrade.stack.handler.Handler;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * FTP 处理器。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public interface FtpHandler extends Handler {

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
    boolean existsFile(String[] filePaths, String fileName) throws HandlerException;

    /**
     * 存储文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @param content   文件的内容。
     * @throws HandlerException 处理器异常。
     */
    void storeFile(String[] filePaths, String fileName, byte[] content) throws HandlerException;

    /**
     * 获取文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的内容。
     * @throws HandlerException 处理器异常。
     */
    byte[] getFileContent(String[] filePaths, String fileName) throws HandlerException;

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
    void storeFileByStream(String[] filePaths, String fileName, InputStream in) throws HandlerException;

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
    void getFileContentByStream(String[] filePaths, String fileName, OutputStream out)
            throws HandlerException;

    /**
     * 删除文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @param fileName  文件的名称。
     * @throws HandlerException 处理器异常。
     */
    void deleteFile(String[] filePaths, String fileName) throws HandlerException;

    /**
     * 删除目录。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @throws HandlerException 处理器异常。
     */
    void removeDirectory(String[] filePaths, String directoryName) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * @param filePaths 文件夹路径。<br>
     *                  路径从根文件出发，一直到达最后一个文件夹，所有文件夹按照顺序组成数组。
     * @return 指定文件夹下所有文件的名称（不带路径前缀）组成的数组。
     * @throws HandlerException 处理器异常。
     */
    FtpFile[] listFiles(String[] filePaths) throws HandlerException;

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
    String[] listFileNames(String[] filePaths) throws HandlerException;
}
