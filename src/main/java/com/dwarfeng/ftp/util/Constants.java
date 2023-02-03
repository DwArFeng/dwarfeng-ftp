package com.dwarfeng.ftp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 常量类。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
public final class Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.class);

    @FtpFileType
    public static final int FTP_FILE_TYPE_FILE = 0;
    @FtpFileType
    public static final int FTP_FILE_TYPE_DIRECTORY = 10;
    @FtpFileType
    public static final int FTP_FILE_TYPE_SYMBOLIC_LINK = 20;
    @FtpFileType
    public static final int FTP_FILE_TYPE_UNKNOWN = 30;

    private static final Lock LOCK = new ReentrantLock();

    private static List<Integer> ftpFileTypeSpace = null;

    /**
     * 获取 FTP 文件类型的空间。
     *
     * @return 产线组件属性类型的空间。
     */
    public static List<Integer> ftpFileTypeSpace() {
        if (Objects.nonNull(ftpFileTypeSpace)) {
            return ftpFileTypeSpace;
        }
        // 基于线程安全的懒加载初始化结果列表。
        LOCK.lock();
        try {
            if (Objects.nonNull(ftpFileTypeSpace)) {
                return ftpFileTypeSpace;
            }
            initFtpFileTypeSpace();
            return ftpFileTypeSpace;
        } finally {
            LOCK.unlock();
        }
    }

    private static void initFtpFileTypeSpace() {
        List<Integer> result = new ArrayList<>();

        Field[] declaredFields = Constants.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!declaredField.isAnnotationPresent(FtpFileType.class)) {
                continue;
            }
            Integer value;
            try {
                value = (Integer) declaredField.get(null);
                result.add(value);
            } catch (Exception e) {
                LOGGER.error("初始化异常, 请检查代码, 信息如下: ", e);
            }
        }

        ftpFileTypeSpace = Collections.unmodifiableList(result);
    }

    private Constants() {
        throw new IllegalStateException("禁止实例化");
    }
}
