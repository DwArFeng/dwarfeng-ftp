package com.dwarfeng.ftp.example;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

/**
 * 移动文件示例。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class MoveFileExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveFileExample.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/application-context*.xml"
        );
        ctx.registerShutdownHook();
        ctx.start();

        FtpHandler ftpHandler = ctx.getBean(FtpHandler.class);

        Scanner scanner = new Scanner(System.in);

        // 显示欢迎信息并获取展示功能用的根文件夹。
        System.out.println("开发者您好!");
        System.out.println("这是一个示例, 用于演示 dwarfeng-ftp 的移动文件功能");
        System.out.println("该示例将会在你配置的 ftp 目录下新建名为 foobar 的文件夹, 如果您的 ftp 已经有这个文件夹了, " +
                "请指定一个不存在的文件夹");
        System.out.print("请指定一个文件夹用于演示功能, 不填默认为 foobar...");
        String rootPath = scanner.nextLine();
        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "foobar";
        }

        // 1. 创建演示文件。
        byte[] content = ResourceUtil.getContent();
        byte[] alterContent = ResourceUtil.getAlterContent();
        System.out.println();
        System.out.println("1. 创建演示文件...");
        ftpHandler.storeFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg", alterContent);
        System.out.println("文件新建完毕, 您将会看到文件被创建");
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 2. 移动文件。
        System.out.println();
        System.out.println("2. 移动文件...");
        ftpHandler.moveFile(
                new String[]{rootPath, "comic-girl"}, "comic-girl.jpg",
                new String[]{rootPath, "漫画の少女"}, "漫画の少女.jpg"
        );
        System.out.println("文件移动完毕, 您将会看到文件被移动");
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 3. 旧文件不存在的情形。
        System.out.println();
        System.out.println("3. 旧文件不存在的情形...");
        try {
            ftpHandler.moveFile(
                    new String[]{rootPath, "aaa"}, "aaa.jpg",
                    new String[]{rootPath, "bbb"}, "bbb.jpg"
            );
        } catch (Exception e) {
            LOGGER.warn("移动失败, 异常信息如下", e);
        }
        System.out.println("您将在警告级别以下的日志中看到异常信息");
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 4. 文件移动，覆盖已存在的文件。
        System.out.println();
        System.out.println("4. 文件移动，覆盖已存在的文件...");
        ftpHandler.moveFile(
                new String[]{rootPath, "漫画の少女"}, "漫画の少女.jpg",
                new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg"
        );
        System.out.println("文件移动完毕, 您将会看到文件被移动, 且已经存在的目标文件被覆盖");
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 5. 文件删除
        System.out.println();
        System.out.println("5. 文件删除...");
        ftpHandler.deleteFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        ftpHandler.removeDirectory(new String[]{rootPath, "aaa"});
        ftpHandler.removeDirectory(new String[]{rootPath, "comic-girl"});
        ftpHandler.removeDirectory(new String[]{rootPath, "漫画の少女"});
        ftpHandler.removeDirectory(new String[]{rootPath, "漫画女孩"});
        System.out.println("示例演示完毕, 感谢您测试与使用!");

        ctx.stop();
        ctx.close();
        System.exit(0);
    }
}
