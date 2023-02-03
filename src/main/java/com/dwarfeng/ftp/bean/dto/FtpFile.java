package com.dwarfeng.ftp.bean.dto;

import com.dwarfeng.subgrade.stack.bean.dto.Dto;

/**
 * FTP 文件。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
public class FtpFile implements Dto {

    private static final long serialVersionUID = -1439245084347215148L;

    private String name;
    private int type;
    private long size;

    public FtpFile() {
    }

    public FtpFile(String name, int type, long size) {
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
        return "FtpFile{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
