package com.dwarfeng.ftp.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FTP 文件类型。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@interface FtpFileType {
}
