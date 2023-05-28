package com.dwarfeng.ftp.example;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

/**
 * 列出文件示例。
 *
 * @author DwArFeng
 * @since 1.1.7
 */
public class ListFileExample {

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
        System.out.println("这是一个示例, 用于演示 dwarfeng-ftp 的列出文件功能");
        System.out.println("该示例将会在你配置的 ftp 目录下新建名为 foobar 的文件夹, 如果您的 ftp 已经有这个文件夹了, " +
                "请指定一个不存在的文件夹");
        System.out.print("请指定一个文件夹用于演示功能, 不填默认为 foobar...");
        String rootPath = scanner.nextLine();
        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "foobar";
        }

        // 1. 创建演示文件。
        byte[] content = ResourceUtil.getContent();
        System.out.println();
        System.out.println("1. 创建演示文件...");
        ftpHandler.storeFile(new String[]{rootPath}, "comic-girl.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath}, "漫画女孩.jpg", content);
        System.out.println("文件新建完毕, 您将会看到文件被创建");
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 2. 列出文件。
        System.out.println();
        System.out.println("2. 列出文件...");
        FtpFile[] ftpFiles = ftpHandler.listFiles(new String[]{rootPath});
        System.out.println("列出文件夹 " + rootPath + " 下的文件: ");
        for (FtpFile ftpFile : ftpFiles) {
            System.out.println("  " + ftpFile.toString());
        }
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 3. 列出文件名。
        System.out.println();
        System.out.println("3. 列出文件名...");
        String[] fileNames = ftpHandler.listFileNames(new String[]{rootPath});
        System.out.println("列出文件夹 " + rootPath + " 下的文件名: ");
        for (String fileName : fileNames) {
            System.out.println("  " + fileName);
        }
        System.out.print("按回车键继续...");
        scanner.nextLine();

        // 4. 文件删除。
        System.out.println();
        System.out.println("4. 删除所有的测试文件...");
        ftpHandler.deleteFile(new String[]{rootPath}, "comic-girl.jpg");
        ftpHandler.deleteFile(new String[]{rootPath}, "漫画女孩.jpg");
        System.out.println("示例演示完毕, 感谢您测试与使用!");

        ctx.stop();
        ctx.close();
        System.exit(0);
    }
}
