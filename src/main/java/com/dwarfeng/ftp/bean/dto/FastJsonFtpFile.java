package com.dwarfeng.ftp.bean.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.dwarfeng.subgrade.stack.bean.dto.Dto;

import java.util.Objects;

/**
 * FastJson FTP 文件。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
public class FastJsonFtpFile implements Dto {

    private static final long serialVersionUID = 8532589897948433983L;

    public static FastJsonFtpFile of(FtpFile ftpFile) {
        if (Objects.isNull(ftpFile)) {
            return null;
        } else {
            return new FastJsonFtpFile(
                    ftpFile.getName(), ftpFile.getType(), ftpFile.getSize()
            );
        }
    }

    @JSONField(name = "name", ordinal = 1)
    private String name;

    @JSONField(name = "type", ordinal = 2)
    private int type;

    @JSONField(name = "size", ordinal = 3)
    private long size;

    public FastJsonFtpFile() {
    }

    public FastJsonFtpFile(String name, int type, long size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FastJsonFtpFile{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
