package com.dwarfeng.ftp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 资源工具类。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public final class ResourceUtil {

    private static byte[] content;

    public static byte[] getContent() throws IOException {
        if (Objects.nonNull(content)) {
            return content;
        }
        try (InputStream in = ResourceUtil.class.getResourceAsStream("/file/menherachan.jpg");
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 输入流对应的资源存放在固定的位置，输入流必不会为 null。
            assert in != null;
            content = stream2Bytes(in, out);
        }
        return content;
    }

    private static byte[] alterContent;

    public static byte[] getAlterContent() throws IOException {
        if (Objects.nonNull(alterContent)) {
            return alterContent;
        }
        try (InputStream in = ResourceUtil.class.getResourceAsStream("/file/nagisa.jpg");
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 输入流对应的资源存放在固定的位置，输入流必不会为 null。
            assert in != null;
            alterContent = stream2Bytes(in, out);
        }
        return alterContent;
    }

    private static byte[] stream2Bytes(InputStream in, ByteArrayOutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int i;
        while ((i = in.read(buffer, 0, buffer.length)) >= 0) {
            out.write(buffer, 0, i);
            out.flush();
        }
        out.flush();
        return out.toByteArray();
    }

    private ResourceUtil() {
        throw new IllegalStateException("禁止实例化");
    }
}
