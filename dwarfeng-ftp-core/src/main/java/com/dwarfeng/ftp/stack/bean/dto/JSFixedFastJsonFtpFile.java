package com.dwarfeng.ftp.stack.bean.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.alibaba.fastjson2.writer.ObjectWriterImplToString;
import com.dwarfeng.subgrade.basic.stack.bean.dto.Dto;

import java.io.Serial;
import java.util.Objects;

/**
 * JSFixed FastJson FTP 文件。
 *
 * @author DwArFeng
 * @since 1.1.4
 */
public class JSFixedFastJsonFtpFile implements Dto {

    @Serial
    private static final long serialVersionUID = 2714730118422740077L;

    public static JSFixedFastJsonFtpFile of(FtpFile ftpFile) {
        if (Objects.isNull(ftpFile)) {
            return null;
        } else {
            return new JSFixedFastJsonFtpFile(
                    ftpFile.getName(), ftpFile.getType(), ftpFile.getSize()
            );
        }
    }

    @JSONField(name = "name", ordinal = 1)
    private String name;

    @JSONField(name = "type", ordinal = 2)
    private int type;

    @JSONField(name = "size", ordinal = 3, serializeUsing = ObjectWriterImplToString.class)
    private long size;

    public JSFixedFastJsonFtpFile() {
    }

    public JSFixedFastJsonFtpFile(String name, int type, long size) {
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
        return "JSFixedFastJsonFtpFile{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
