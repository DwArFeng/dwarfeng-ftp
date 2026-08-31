package com.dwarfeng.ftp.node.example;

import com.dwarfeng.dutil.basic.sdk.io.CT;
import com.dwarfeng.ftp.sdk.util.Constants;
import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.handler.FtpHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 流程示例。
 *
 * @author DwArFeng
 * @since 1.0.0
 */
public class ProcessExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessExample.class);

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
        CT.trace("这是一个示例, 用于演示 dwarfeng-ftp 的功能");
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
        ftpHandler.storeFile(new String[]{rootPath}, "comic-girl.jpg", ResourceUtil.getContent());
        ftpHandler.storeFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg", ResourceUtil.getContent());
        ftpHandler.storeFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg", ResourceUtil.getContent());
        CT.trace("文件新建完毕, 您将会看到文件被创建");
        CT.trace("如果路径中的文件夹不存在, 则文件夹将会被创建, 中文名称被支持。");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 2. 演示文件替换。
        CT.trace("");
        CT.trace("2. 文件替换...");
        ftpHandler.storeFile(new String[]{rootPath}, "comic-girl.jpg", ResourceUtil.getAlterContent());
        ftpHandler.storeFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg", ResourceUtil.getAlterContent());
        ftpHandler.storeFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg", ResourceUtil.getAlterContent());
        CT.trace("文件新建完毕, 您将会看到文件被替换");
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 3. 文件读取。
        CT.trace("");
        CT.trace("3. 文件读取...");
        byte[] ftpContent;
        ftpContent = ftpHandler.retrieveFile(new String[]{rootPath}, "comic-girl.jpg");
        if (Arrays.equals(ftpContent, ResourceUtil.getAlterContent())) {
            CT.trace("文件内容相等!");
        }
        ftpContent = ftpHandler.retrieveFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg");
        if (Arrays.equals(ftpContent, ResourceUtil.getAlterContent())) {
            CT.trace("文件内容相等!");
        }
        ftpContent = ftpHandler.retrieveFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        if (Arrays.equals(ftpContent, ResourceUtil.getAlterContent())) {
            CT.trace("文件内容相等!");
        }
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 4. 列出文件。
        CT.trace("");
        CT.trace("4. 列出文件...");
        FtpFile[] ftpFiles = ftpHandler.listFiles(new String[]{rootPath});
        CT.trace(String.format("测试文件夹下目前一共有 %d 个文件: %n", ftpFiles.length));
        for (int i = 0; i < ftpFiles.length; i++) {
            FtpFile ftpFile = ftpFiles[i];
            String type = switch (ftpFile.getType()) {
                case Constants.FTP_FILE_TYPE_FILE -> "file";
                case Constants.FTP_FILE_TYPE_DIRECTORY -> "directory";
                case Constants.FTP_FILE_TYPE_SYMBOLIC_LINK -> "symbolic link";
                default -> "unknown";
            };
            CT.trace(String.format("%d: name: %s, type: %s, size: %d%n", i + 1, ftpFile.getName(), type, ftpFile.getSize()));
        }
        System.out.print("请按回车键继续...");
        scanner.nextLine();

        // 5. 断线重连。
        CT.trace("");
        CT.trace("5. 断线重连...");
        CT.trace("请关闭 FTP 服务, 并观察 dwarf-ftp 的断线文件处理机制");
        CT.trace("按下回车键后观察在断线模式下 dwarf-ftp 的文件操作");
        System.out.print("请按回车键继续...");
        scanner.nextLine();
        try {
            ftpHandler.storeFile(new String[]{rootPath}, "comic-girl.jpg", ResourceUtil.getContent());
        } catch (Exception e) {
            LOGGER.warn("存储文件失败, 异常信息如下: ", e);
        }
        CT.trace("请开启 FTP 服务, 并观察 dwarf-ftp 的断线重连处理机制");
        CT.trace("按下回车键后观察 dwarf-ftp 自动重连文件操作");
        System.out.print("请按回车键继续...");
        scanner.nextLine();
        while (true) {
            try {
                ftpHandler.storeFile(new String[]{rootPath}, "comic-girl.jpg", ResourceUtil.getContent());
                CT.trace("由于断线重连机制被启用, 文件已经存储完毕");
                System.out.print("请按回车键继续...");
                scanner.nextLine();
                break;
            } catch (Exception e) {
                CT.trace("文件没有存储成功, 此时 FTP 服务还未启动完毕");
                LOGGER.warn("存储文件失败, 异常信息如下: ", e);
                System.out.print("请按回车键继续, 或直接关闭测试...");
                scanner.nextLine();
            }
        }

        // 6. 文件删除。
        CT.trace("");
        CT.trace("6. 删除所有的测试文件...");
        ftpHandler.deleteFile(new String[]{rootPath}, "comic-girl.jpg");
        ftpHandler.deleteFile(new String[]{rootPath, "comic-girl"}, "comic-girl.jpg");
        ftpHandler.deleteFile(new String[]{rootPath, "漫画女孩"}, "漫画女孩.jpg");
        ftpHandler.removeDirectory(new String[]{rootPath, "comic-girl"});
        ftpHandler.removeDirectory(new String[]{rootPath, "漫画女孩"});
        CT.trace("示例演示完毕, 感谢您测试与使用!");

        System.exit(0);
    }
}
