package com.dwarfeng.ftp.node.example;

import com.dwarfeng.dutil.basic.io.CT;
import com.dwarfeng.ftp.stack.handler.FtpHandler;
import com.dwarfeng.ftp.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;
import java.util.UUID;

/**
 * 清空目录示例。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class ClearDirectoryExample {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/application-context*.xml"
        );
        ctx.registerShutdownHook();
        ctx.start();

        FtpHandler ftpHandler = ctx.getBean(FtpHandler.class);

        Scanner scanner = new Scanner(System.in);

        // 显示欢迎信息并获取展示功能用的根文件夹。
        CT.trace("开发者您好!");
        CT.trace("这是一个示例, 用于演示 dwarfeng-ftp 的清空目录功能");
        CT.trace("该示例将会在你配置的 ftp 目录下新建名为 foobar 的文件夹, 如果您的 ftp 已经有这个文件夹了, " +
                "请指定一个不存在的文件夹");
        System.out.print("请指定一个文件夹用于演示功能, 不填默认为 foobar...");
        String rootPath = scanner.nextLine();
        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "foobar";
        }

        // 1. 创建演示文件。
        String middlePath = UUID.randomUUID().toString();
        byte[] content = ResourceUtil.getContent();
        CT.trace("");
        CT.trace("1. 创建演示文件...");
        ftpHandler.storeFile(new String[]{rootPath, middlePath}, "comic-girl.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath, middlePath}, "漫画女孩.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath, middlePath, "comic-girl"}, "comic-girl.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath, middlePath, "漫画女孩"}, "漫画女孩.jpg", content);
        CT.trace("文件新建完毕, 您将会在中间目录 " + middlePath + " 下看到文件被创建");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 2. 清空目录。
        CT.trace("");
        CT.trace("2. 清空目录...");
        ftpHandler.clearDirectory(new String[]{rootPath, middlePath});
        CT.trace("清空目录 " + rootPath + "/" + middlePath + " 下的文件");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 4. 文件删除。
        CT.trace("");
        CT.trace("4. 文件删除...");
        ftpHandler.removeDirectory(new String[]{rootPath, middlePath});
        CT.trace("示例演示完毕, 感谢您测试与使用!");

        System.exit(0);
    }
}
