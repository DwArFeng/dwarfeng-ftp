package com.dwarfeng.ftp.node.example;

import com.dwarfeng.dutil.basic.sdk.io.CT;
import com.dwarfeng.dutil.basic.sdk.io.IOUtil;
import com.dwarfeng.ftp.stack.handler.FtpHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 流的使用示例。
 *
 * @author DwArFeng
 * @since 1.1.6
 */
public class StreamExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamExample.class);

    static void main() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:com/dwarfeng/ftp/node/spring/application-context*.xml"
        );
        ctx.registerShutdownHook();
        ctx.start();

        FtpHandler ftpHandler = ctx.getBean(FtpHandler.class);

        Scanner scanner = new Scanner(System.in);

        // 显示欢迎信息并获取展示功能用的根文件夹。
        CT.trace("开发者您好!");
        CT.trace("这是一个示例, 用于演示 dwarfeng-ftp 的流的使用");
        CT.trace("该示例将会在你配置的 ftp 目录下新建名为 foobar 的文件夹, 如果您的 ftp 已经有这个文件夹了, " +
                "请指定一个不存在的文件夹");
        System.out.print("请指定一个文件夹用于演示功能, 不填默认为 foobar...");
        String rootPath = scanner.nextLine();
        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "foobar";
        }

        // 1. 演示文件存储。
        CT.trace("");
        CT.trace("1. 文件存储...");
        byte[] content = ResourceUtil.getContent();
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(content);
                OutputStream out = ftpHandler.openOutputStream(new String[]{rootPath}, "comic-girl.jpg")
        ) {
            IOUtil.trans(in, out, 4096);
        }
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(content);
                OutputStream out = ftpHandler.openOutputStream(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg")
        ) {
            IOUtil.trans(in, out, 4096);
        }
        CT.trace("文件新建完毕, 您将会看到文件被创建");
        CT.trace("如果路径中的文件夹不存在, 则文件夹将会被创建, 中文名称被支持。");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 2. 演示文件读取。
        CT.trace("");
        CT.trace("2. 文件读取...");
        byte[] ftpContent;
        try (
                InputStream in = ftpHandler.openInputStream(new String[]{rootPath}, "comic-girl.jpg");
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            IOUtil.trans(in, out, 4096);
            ftpContent = out.toByteArray();
        }
        if (Arrays.equals(ftpContent, content)) {
            CT.trace("文件内容相等!");
        }

        // 3. 断线重连。
        CT.trace("");
        CT.trace("3. 断线重连...");
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(content);
                OutputStream out = ftpHandler.openOutputStream(new String[]{rootPath}, "comic-girl.jpg")
        ) {
            // 从 in 中读取 1KB 数据，写入 out。
            byte[] bytes = new byte[1024];
            int len = in.read(bytes);
            out.write(bytes, 0, len);
            out.flush();

            CT.trace("已经向 comic-girl.jpg 中写入 1KB 数据");
            CT.trace("请关闭 FTP 服务, 并观察 dwarf-ftp 的断线文件处理机制");
            System.out.print("请按回车键继续...");
            scanner.nextLine();

            // 从 in 中读取剩余数据，写入 out。
            len = in.read(bytes);
            out.write(bytes, 0, len);
            out.flush();
        } catch (Exception e) {
            LOGGER.warn("存储文件失败, 异常信息如下: ", e);
        }
        CT.trace("请重新启动 FTP 服务, 并观察 dwarf-ftp 的断线文件处理机制");
        System.out.print("请按回车键继续...");
        scanner.nextLine();
        while (true) {
            try (
                    ByteArrayInputStream in = new ByteArrayInputStream(content);
                    OutputStream out = ftpHandler.openOutputStream(new String[]{rootPath}, "comic-girl.jpg")
            ) {
                IOUtil.trans(in, out, 4096);
            } catch (Exception e) {
                CT.trace("文件没有存储成功, 此时 FTP 服务还未启动完毕");
                LOGGER.warn("存储文件失败, 异常信息如下: ", e);
                System.out.print("请按回车键继续, 或直接关闭测试...");
                scanner.nextLine();
                continue;
            }
            CT.trace("由于断线重连机制被启用, 文件已经存储完毕");
            System.out.print("请按回车键继续...");
            scanner.nextLine();
            break;
        }

        // 4. 文件删除。
        CT.trace("");
        CT.trace("4. 文件删除...");
        ftpHandler.deleteFile(new String[]{rootPath}, "comic-girl.jpg");
        ftpHandler.deleteFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        ftpHandler.removeDirectory(new String[]{rootPath, "漫画女孩"});
        CT.trace("示例演示完毕, 感谢您测试与使用!");

        System.exit(0);
    }
}
