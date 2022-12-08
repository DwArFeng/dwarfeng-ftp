package com.dwarfeng.ftp.handler;

import com.dwarfeng.subgrade.stack.exception.HandlerException;
import com.dwarfeng.subgrade.stack.handler.Handler;

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
}
