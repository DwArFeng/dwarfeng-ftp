package com.dwarfeng.ftp.sdk.util;

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

    // region FTP 文件类型

    @FtpFileType
    public static final int FTP_FILE_TYPE_FILE = 0;
    @FtpFileType
    public static final int FTP_FILE_TYPE_DIRECTORY = 10;
    @FtpFileType
    public static final int FTP_FILE_TYPE_SYMBOLIC_LINK = 20;
    @FtpFileType
    public static final int FTP_FILE_TYPE_UNKNOWN = 30;

    // endregion

    // region XSD 默认值

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_FTP_CONFIG_NAME = "ftpConfig";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_FTP_HANDLER_NAME = "ftpHandler";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_THREAD_POOL_TASK_SCHEDULER_NAME = "scheduler";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_FTP_QOS_HANDLER_NAME = "ftpQosHandler";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_FTP_QOS_SERVICE_NAME = "ftpQosService";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_SERVICE_EXCEPTION_MAPPER_NAME = "mapServiceExceptionMapper";

    /**
     * @since 2.0.0
     */
    public static final String XSD_DEFAULT_FTP_HANDLER_AUTO_START_VALUE = "true";

    // endregion

    private static final Lock LOCK = new ReentrantLock();

    // region FTP 文件类型空间

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

    // endregion

    private Constants() {
        throw new IllegalStateException("禁止实例化");
    }
}
