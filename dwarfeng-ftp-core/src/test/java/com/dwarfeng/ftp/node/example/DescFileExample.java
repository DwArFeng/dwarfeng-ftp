package com.dwarfeng.ftp.node.example;

import com.dwarfeng.dutil.basic.sdk.io.CT;
import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.handler.FtpHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

/**
 * 描述文件示例。
 *
 * @author DwArFeng
 * @since 1.2.0
 */
public class DescFileExample {

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
        CT.trace("这是一个示例, 用于演示 dwarfeng-ftp 的描述文件功能");
        CT.trace("该示例将会在你配置的 ftp 目录下新建名为 foobar 的文件夹, 如果您的 ftp 已经有这个文件夹了, " +
                "请指定一个不存在的文件夹");
        System.out.print("请指定一个文件夹用于演示功能, 不填默认为 foobar...");
        String rootPath = scanner.nextLine();
        if (StringUtils.isEmpty(rootPath)) {
            rootPath = "foobar";
        }

        // 1. 创建演示文件。
        byte[] content = ResourceUtil.getContent();
        CT.trace("");
        CT.trace("1. 创建演示文件...");
        ftpHandler.storeFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg", content);
        ftpHandler.storeFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg", content);
        CT.trace("文件新建完毕, 您将会看到文件被创建");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 2. 描述存在的文件及目录。
        CT.trace("");
        CT.trace("2. 描述存在的文件及目录...");
        FtpFile ftpFile;
        ftpFile = ftpHandler.descFile(new String[]{rootPath}, "comic-girl");
        CT.trace(String.format("/%s/comic-girl: %s\n", rootPath, ftpFile));
        ftpFile = ftpHandler.descFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg");
        CT.trace(String.format("/%s/comic-girl/comic-girl.jpg: %s\n", rootPath, ftpFile));
        ftpFile = ftpHandler.descFile(new String[]{rootPath}, "漫画女孩");
        CT.trace(String.format("/%s/漫画女孩: %s\n", rootPath, ftpFile));
        ftpFile = ftpHandler.descFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        CT.trace(String.format("/%s/漫画女孩/漫画女孩.jpg: %s\n", rootPath, ftpFile));
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 3. 描述不存在的文件及目录。
        CT.trace("");
        CT.trace("3. 描述不存在的文件及目录...");
        ftpFile = ftpHandler.descFile(new String[]{rootPath}, "何もありません");
        CT.trace(String.format("/%s/何もありません: %s\n", rootPath, ftpFile));
        ftpFile = ftpHandler.descFile(new String[]{rootPath}, "何もありません.jpg");
        CT.trace(String.format("/%s/何もありません.jpg: %s\n", rootPath, ftpFile));
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 4. 文件删除。
        CT.trace("");
        CT.trace("4. 文件删除...");
        ftpHandler.deleteFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg");
        ftpHandler.deleteFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        ftpHandler.removeDirectory(new String[]{rootPath, "comic-girl"});
        ftpHandler.removeDirectory(new String[]{rootPath, "漫画女孩"});
        CT.trace("示例演示完毕, 感谢您测试与使用!");

        System.exit(0);
    }
}
