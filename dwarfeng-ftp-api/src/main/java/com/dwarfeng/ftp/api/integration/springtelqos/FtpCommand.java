package com.dwarfeng.ftp.api.integration.springtelqos;

import com.dwarfeng.ftp.sdk.util.Constants;
import com.dwarfeng.ftp.stack.bean.dto.FtpFile;
import com.dwarfeng.ftp.stack.service.FtpQosService;
import com.dwarfeng.ftp.stack.struct.FtpFileLocation;
import com.dwarfeng.springtelqos.sdk.command.CliCommand;
import com.dwarfeng.springtelqos.sdk.configuration.TelqosCommand;
import com.dwarfeng.springtelqos.sdk.util.CliCommandUtil;
import com.dwarfeng.springtelqos.stack.command.CommandDescriptor;
import com.dwarfeng.springtelqos.stack.command.CommandExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FTP 指令。
 *
 * <p>
 * 该指令提供 FTP QoS 服务的命令行入口，覆盖处理器管理与文件操作能力。
 *
 * @author DwArFeng
 * @since 2.0.0
 */
@TelqosCommand
public class FtpCommand extends CliCommand {

    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String IDENTITY = "ftp";

    // region 指令选项

    private static final String COMMAND_OPTION_LIST_HANDLERS = "lh";
    private static final String COMMAND_OPTION_LIST_HANDLERS_LONG_OPT = "list-handlers";
    private static final String COMMAND_OPTION_IS_STARTED = "is";
    private static final String COMMAND_OPTION_IS_STARTED_LONG_OPT = "is-started";
    private static final String COMMAND_OPTION_START = "st";
    private static final String COMMAND_OPTION_STOP = "sp";
    private static final String COMMAND_OPTION_EXISTS = "ex";
    private static final String COMMAND_OPTION_UPLOAD = "up";
    private static final String COMMAND_OPTION_UPLOAD_LONG_OPT = "upload";
    private static final String COMMAND_OPTION_DOWNLOAD = "dn";
    private static final String COMMAND_OPTION_DOWNLOAD_LONG_OPT = "download";
    private static final String COMMAND_OPTION_DELETE_FILE = "df";
    private static final String COMMAND_OPTION_DELETE_FILE_LONG_OPT = "delete-file";
    private static final String COMMAND_OPTION_REMOVE_DIRECTORY = "rd";
    private static final String COMMAND_OPTION_REMOVE_DIRECTORY_LONG_OPT = "remove-directory";
    private static final String COMMAND_OPTION_LIST_FILES = "lf";
    private static final String COMMAND_OPTION_LIST_FILES_LONG_OPT = "list-files";
    private static final String COMMAND_OPTION_LIST_FILE_NAMES = "ln";
    private static final String COMMAND_OPTION_LIST_FILE_NAMES_LONG_OPT = "list-file-names";
    private static final String COMMAND_OPTION_DESC_FILE = "dc";
    private static final String COMMAND_OPTION_DESC_FILE_LONG_OPT = "desc-file";
    private static final String COMMAND_OPTION_RENAME_FILE = "rn";
    private static final String COMMAND_OPTION_RENAME_FILE_LONG_OPT = "rename-file";
    private static final String COMMAND_OPTION_MOVE_FILE = "mv";
    private static final String COMMAND_OPTION_MOVE_FILE_LONG_OPT = "move-file";
    private static final String COMMAND_OPTION_COPY_FILE = "cp";
    private static final String COMMAND_OPTION_COPY_FILE_LONG_OPT = "copy-file";
    private static final String COMMAND_OPTION_CLEAR_DIRECTORY = "cd";
    private static final String COMMAND_OPTION_CLEAR_DIRECTORY_LONG_OPT = "clear-directory";

    private static final String[] COMMAND_OPTION_ARRAY = new String[]{
            COMMAND_OPTION_LIST_HANDLERS,
            COMMAND_OPTION_IS_STARTED,
            COMMAND_OPTION_START,
            COMMAND_OPTION_STOP,
            COMMAND_OPTION_EXISTS,
            COMMAND_OPTION_UPLOAD,
            COMMAND_OPTION_DOWNLOAD,
            COMMAND_OPTION_DELETE_FILE,
            COMMAND_OPTION_REMOVE_DIRECTORY,
            COMMAND_OPTION_LIST_FILES,
            COMMAND_OPTION_LIST_FILE_NAMES,
            COMMAND_OPTION_DESC_FILE,
            COMMAND_OPTION_RENAME_FILE,
            COMMAND_OPTION_MOVE_FILE,
            COMMAND_OPTION_COPY_FILE,
            COMMAND_OPTION_CLEAR_DIRECTORY
    };

    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_HANDLER_NAME = "hn";
    private static final String COMMAND_SUB_OPTION_HANDLER_NAME_LONG_OPT = "handler-name";
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_LOCAL_PATH = "lp";
    private static final String COMMAND_SUB_OPTION_LOCAL_PATH_LONG_OPT = "local-path";
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_REMOTE_FILE_PATH = "rfp";
    private static final String COMMAND_SUB_OPTION_REMOTE_FILE_PATH_LONG_OPT = "remote-file-path";
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH = "rdp";
    private static final String COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH_LONG_OPT = "remote-directory-path";
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH = "orfp";
    private static final String COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH_LONG_OPT = "old-remote-file-path";
    @SuppressWarnings({"SpellCheckingInspection", "RedundantSuppression"})
    private static final String COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH = "nrfp";
    private static final String COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH_LONG_OPT = "new-remote-file-path";

    // endregion

    private final FtpQosService ftpQosService;

    public FtpCommand(FtpQosService ftpQosService) {
        super(IDENTITY);
        this.ftpQosService = ftpQosService;
    }

    @Override
    protected DescriptionProvider provideDescriptionProvider() {
        return context -> "FTP QoS 服务";
    }

    @Override
    protected CliSyntaxProvider provideCliSyntaxProvider() {
        return this::cliSyntaxProvider;
    }

    private String cliSyntaxProvider(CommandDescriptor.Context context) throws Exception {
        String identity = context.getRuntimeIdentity();
        String[] patterns = new String[]{
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_LIST_HANDLERS),
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_IS_STARTED) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_START) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_STOP) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_EXISTS) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_FILE_PATH) +
                        " remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_UPLOAD) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_LOCAL_PATH) + " local-path] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_FILE_PATH) +
                        " remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_DOWNLOAD) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_FILE_PATH) +
                        " remote-file-path] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_LOCAL_PATH) + " local-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_DELETE_FILE) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_FILE_PATH) +
                        " remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_REMOVE_DIRECTORY) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH) +
                        " remote-directory-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_LIST_FILES) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH) +
                        " remote-directory-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_LIST_FILE_NAMES) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH) +
                        " remote-directory-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_DESC_FILE) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_FILE_PATH) +
                        " remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_RENAME_FILE) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH) +
                        " old-remote-file-path] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH) +
                        " new-remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_MOVE_FILE) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH) +
                        " old-remote-file-path] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH) +
                        " new-remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_COPY_FILE) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH) +
                        " old-remote-file-path] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH) +
                        " new-remote-file-path]",
                identity + " " + CliCommandUtil.concatOptionPrefix(COMMAND_OPTION_CLEAR_DIRECTORY) +
                        " [" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_HANDLER_NAME) + " handler-name] " +
                        "[" + CliCommandUtil.concatOptionPrefix(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH) +
                        " remote-directory-path]"
        };
        return CliCommandUtil.cliSyntax(patterns);
    }

    @Override
    protected List<Option> provideOptions() {
        List<Option> list = new ArrayList<>();

        list.add(Option.builder(COMMAND_OPTION_LIST_HANDLERS).longOpt(COMMAND_OPTION_LIST_HANDLERS_LONG_OPT)
                .optionalArg(true).hasArg(false).desc("列出所有可用的 FTP 处理器").build());
        list.add(Option.builder(COMMAND_OPTION_IS_STARTED).longOpt(COMMAND_OPTION_IS_STARTED_LONG_OPT)
                .optionalArg(true).hasArg(false).desc("查询 FTP 处理器是否已启动").build());
        list.add(Option.builder(COMMAND_OPTION_START).optionalArg(true).hasArg(false).desc("启动 FTP 处理器").build());
        list.add(Option.builder(COMMAND_OPTION_STOP).optionalArg(true).hasArg(false).desc("停止 FTP 处理器").build());

        list.add(
                Option.builder(COMMAND_OPTION_EXISTS).optionalArg(true).hasArg(false).desc("检查 FTP 文件是否存在")
                        .build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_UPLOAD).longOpt(COMMAND_OPTION_UPLOAD_LONG_OPT).optionalArg(true)
                        .hasArg(false).desc("上传本地文件到 FTP").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_DOWNLOAD).longOpt(COMMAND_OPTION_DOWNLOAD_LONG_OPT).optionalArg(true)
                        .hasArg(false).desc("下载 FTP 文件到本地").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_DELETE_FILE).longOpt(COMMAND_OPTION_DELETE_FILE_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("删除 FTP 文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_REMOVE_DIRECTORY).longOpt(COMMAND_OPTION_REMOVE_DIRECTORY_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("删除 FTP 目录").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_LIST_FILES).longOpt(COMMAND_OPTION_LIST_FILES_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("列出 FTP 目录文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_LIST_FILE_NAMES).longOpt(COMMAND_OPTION_LIST_FILE_NAMES_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("列出 FTP 目录文件名").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_DESC_FILE).longOpt(COMMAND_OPTION_DESC_FILE_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("描述 FTP 文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_RENAME_FILE).longOpt(COMMAND_OPTION_RENAME_FILE_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("重命名 FTP 文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_MOVE_FILE).longOpt(COMMAND_OPTION_MOVE_FILE_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("移动 FTP 文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_COPY_FILE).longOpt(COMMAND_OPTION_COPY_FILE_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("复制 FTP 文件").build()
        );
        list.add(
                Option.builder(COMMAND_OPTION_CLEAR_DIRECTORY).longOpt(COMMAND_OPTION_CLEAR_DIRECTORY_LONG_OPT)
                        .optionalArg(true).hasArg(false).desc("清空 FTP 目录").build()
        );

        list.add(
                Option.builder(COMMAND_SUB_OPTION_HANDLER_NAME).longOpt(COMMAND_SUB_OPTION_HANDLER_NAME_LONG_OPT)
                        .hasArg(true).type(String.class).desc("FTP 处理器名称").build()
        );
        list.add(
                Option.builder(COMMAND_SUB_OPTION_LOCAL_PATH).longOpt(COMMAND_SUB_OPTION_LOCAL_PATH_LONG_OPT)
                        .hasArg(true).type(String.class).desc("本地文件路径").build()
        );
        list.add(
                Option.builder(COMMAND_SUB_OPTION_REMOTE_FILE_PATH)
                        .longOpt(COMMAND_SUB_OPTION_REMOTE_FILE_PATH_LONG_OPT).hasArg(true).type(String.class)
                        .desc("远端文件路径").build()
        );
        list.add(
                Option.builder(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH)
                        .longOpt(COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH_LONG_OPT).hasArg(true).type(String.class)
                        .desc("远端目录路径").build()
        );
        list.add(
                Option.builder(COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH)
                        .longOpt(COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH_LONG_OPT).hasArg(true).type(String.class)
                        .desc("旧远端文件路径").build()
        );
        list.add(
                Option.builder(COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH)
                        .longOpt(COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH_LONG_OPT).hasArg(true).type(String.class)
                        .desc("新远端文件路径").build()
        );

        return list;
    }

    @Override
    protected void executeWithCmd(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        Pair<String, Integer> pair = CliCommandUtil.analyseCommand(cmd, COMMAND_OPTION_ARRAY);
        if (pair.getRight() != 1) {
            context.sendMessage(CliCommandUtil.optionMismatchMessage(COMMAND_OPTION_ARRAY));
            context.sendMessage(context.getCommandManual(context.getRuntimeIdentity()));
            return;
        }

        switch (pair.getLeft()) {
            case COMMAND_OPTION_LIST_HANDLERS:
                handleListHandlers(context);
                break;
            case COMMAND_OPTION_IS_STARTED:
                handleIsStarted(context, cmd);
                break;
            case COMMAND_OPTION_START:
                handleStart(context, cmd);
                break;
            case COMMAND_OPTION_STOP:
                handleStop(context, cmd);
                break;
            case COMMAND_OPTION_EXISTS:
                handleExists(context, cmd);
                break;
            case COMMAND_OPTION_UPLOAD:
                handleUpload(context, cmd);
                break;
            case COMMAND_OPTION_DOWNLOAD:
                handleDownload(context, cmd);
                break;
            case COMMAND_OPTION_DELETE_FILE:
                handleDeleteFile(context, cmd);
                break;
            case COMMAND_OPTION_REMOVE_DIRECTORY:
                handleRemoveDirectory(context, cmd);
                break;
            case COMMAND_OPTION_LIST_FILES:
                handleListFiles(context, cmd);
                break;
            case COMMAND_OPTION_LIST_FILE_NAMES:
                handleListFileNames(context, cmd);
                break;
            case COMMAND_OPTION_DESC_FILE:
                handleDescFile(context, cmd);
                break;
            case COMMAND_OPTION_RENAME_FILE:
                handleRenameFile(context, cmd);
                break;
            case COMMAND_OPTION_MOVE_FILE:
                handleMoveFile(context, cmd);
                break;
            case COMMAND_OPTION_COPY_FILE:
                handleCopyFile(context, cmd);
                break;
            case COMMAND_OPTION_CLEAR_DIRECTORY:
                handleClearDirectory(context, cmd);
                break;
            default:
                throw new IllegalStateException("不应该执行到此处, 请联系开发人员");
        }
    }

    private void handleListHandlers(CommandExecutor.Context context) throws Exception {
        List<String> handlerNames = ftpQosService.listHandlerNames();
        context.sendMessage("可用的处理器名称:");
        if (handlerNames.isEmpty()) {
            context.sendMessage("  (Empty)");
            return;
        }
        for (int i = 0; i < handlerNames.size(); i++) {
            String handlerName = handlerNames.get(i);
            context.sendMessage(String.format("  %3d: %s", i + 1, handlerName));
        }
    }

    private void handleIsStarted(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        boolean started = ftpQosService.isStarted(handlerName);
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName) + ", 已启动: " + started);
    }

    private void handleStart(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        ftpQosService.start(handlerName);
        context.sendMessage("启动成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
    }

    private void handleStop(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        ftpQosService.stop(handlerName);
        context.sendMessage("停止成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
    }

    private void handleExists(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteFilePath = parseRemoteFilePath(context, cmd);
        FtpFileLocation fileLocation = parseRemoteFileLocation(remoteFilePath);
        boolean exists = ftpQosService.existsFile(handlerName, fileLocation);
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端文件路径: " + remoteFilePath);
        context.sendMessage("存在: " + exists);
    }

    private void handleUpload(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String localPath = parseLocalPath(context, cmd);
        String remoteFilePath = parseRemoteFilePath(context, cmd);

        File localFile = parseUploadFile(localPath);
        FtpFileLocation fileLocation = parseRemoteFileLocation(remoteFilePath);

        try (InputStream in = Files.newInputStream(localFile.toPath())) {
            ftpQosService.storeFileByStream(handlerName, fileLocation, in);
        }

        context.sendMessage("上传成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("本地文件路径: " + localFile.getAbsolutePath());
        context.sendMessage("远端文件路径: " + remoteFilePath);
    }

    private void handleDownload(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteFilePath = parseRemoteFilePath(context, cmd);
        String localPath = parseLocalPath(context, cmd);

        File localFile = parseDownloadFile(localPath);
        FtpFileLocation fileLocation = parseRemoteFileLocation(remoteFilePath);

        try (OutputStream out = Files.newOutputStream(
                localFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        )) {
            ftpQosService.retrieveFileByStream(handlerName, fileLocation, out);
        }

        context.sendMessage("下载成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端文件路径: " + remoteFilePath);
        context.sendMessage("本地文件路径: " + localFile.getAbsolutePath());
    }

    private void handleDeleteFile(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteFilePath = parseRemoteFilePath(context, cmd);

        ftpQosService.deleteFile(handlerName, parseRemoteFileLocation(remoteFilePath));

        context.sendMessage("删除文件成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端文件路径: " + remoteFilePath);
    }

    private void handleRemoveDirectory(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteDirectoryPath = parseRemoteDirectoryPath(context, cmd);

        ftpQosService.removeDirectory(handlerName, parseRemoteDirectoryPath(remoteDirectoryPath));

        context.sendMessage("删除目录成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端目录路径: " + remoteDirectoryPath);
    }

    private void handleListFiles(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteDirectoryPath = parseRemoteDirectoryPath(context, cmd);

        FtpFile[] ftpFiles = ftpQosService.listFiles(handlerName, parseRemoteDirectoryPath(remoteDirectoryPath));

        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端目录路径: " + remoteDirectoryPath);
        context.sendMessage("目录文件:");
        if (ftpFiles.length == 0) {
            context.sendMessage("  (Empty)");
            return;
        }
        for (int i = 0; i < ftpFiles.length; i++) {
            FtpFile ftpFile = ftpFiles[i];
            context.sendMessage(String.format(
                    "  %3d: name=%s, type=%s, size=%d",
                    i + 1, ftpFile.getName(), formatFileType(ftpFile.getType()), ftpFile.getSize()
            ));
        }
    }

    private void handleListFileNames(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteDirectoryPath = parseRemoteDirectoryPath(context, cmd);

        String[] fileNames = ftpQosService.listFileNames(handlerName, parseRemoteDirectoryPath(remoteDirectoryPath));

        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端目录路径: " + remoteDirectoryPath);
        context.sendMessage("目录文件名:");
        if (fileNames.length == 0) {
            context.sendMessage("  (Empty)");
            return;
        }
        for (int i = 0; i < fileNames.length; i++) {
            context.sendMessage(String.format("  %3d: %s", i + 1, fileNames[i]));
        }
    }

    private void handleDescFile(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteFilePath = parseRemoteFilePath(context, cmd);

        FtpFile ftpFile = ftpQosService.descFile(handlerName, parseRemoteFileLocation(remoteFilePath));

        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端文件路径: " + remoteFilePath);
        if (ftpFile == null) {
            context.sendMessage("描述结果: null");
            return;
        }
        context.sendMessage("描述结果:");
        context.sendMessage("  name: " + ftpFile.getName());
        context.sendMessage("  type: " + formatFileType(ftpFile.getType()));
        context.sendMessage("  size: " + ftpFile.getSize());
    }

    private void handleRenameFile(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String oldRemoteFilePath = parseOldRemoteFilePath(context, cmd);
        String newRemoteFilePath = parseNewRemoteFilePath(context, cmd);

        ftpQosService.renameFile(
                handlerName,
                parseRemoteFileLocation(oldRemoteFilePath),
                parseRemoteFileLocation(newRemoteFilePath)
        );

        context.sendMessage("重命名成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("旧远端文件路径: " + oldRemoteFilePath);
        context.sendMessage("新远端文件路径: " + newRemoteFilePath);
    }

    private void handleMoveFile(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String oldRemoteFilePath = parseOldRemoteFilePath(context, cmd);
        String newRemoteFilePath = parseNewRemoteFilePath(context, cmd);

        ftpQosService.moveFile(
                handlerName,
                parseRemoteFileLocation(oldRemoteFilePath),
                parseRemoteFileLocation(newRemoteFilePath)
        );

        context.sendMessage("移动成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("旧远端文件路径: " + oldRemoteFilePath);
        context.sendMessage("新远端文件路径: " + newRemoteFilePath);
    }

    private void handleCopyFile(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String oldRemoteFilePath = parseOldRemoteFilePath(context, cmd);
        String newRemoteFilePath = parseNewRemoteFilePath(context, cmd);

        ftpQosService.copyFile(
                handlerName,
                parseRemoteFileLocation(oldRemoteFilePath),
                parseRemoteFileLocation(newRemoteFilePath)
        );

        context.sendMessage("复制成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("旧远端文件路径: " + oldRemoteFilePath);
        context.sendMessage("新远端文件路径: " + newRemoteFilePath);
    }

    private void handleClearDirectory(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        String handlerName = parseHandlerName(context, cmd);
        String remoteDirectoryPath = parseRemoteDirectoryPath(context, cmd);

        ftpQosService.clearDirectory(handlerName, parseRemoteDirectoryPath(remoteDirectoryPath));

        context.sendMessage("清空目录成功!");
        context.sendMessage("处理器名称: " + normalizeHandlerNameForOutput(handlerName));
        context.sendMessage("远端目录路径: " + remoteDirectoryPath);
    }

    @Nullable
    private String parseHandlerName(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        if (cmd.hasOption(COMMAND_SUB_OPTION_HANDLER_NAME)) {
            return StringUtils.trimToNull(cmd.getOptionValue(COMMAND_SUB_OPTION_HANDLER_NAME));
        }

        List<String> handlerNames = ftpQosService.listHandlerNames();
        if (handlerNames.size() <= 1) {
            return null;
        }

        context.sendMessage("可用的处理器名称:");
        for (int i = 0; i < handlerNames.size(); i++) {
            context.sendMessage(String.format("  %3d: %s", i + 1, handlerNames.get(i)));
        }
        context.sendMessage("请输入处理器名称:");
        return StringUtils.trimToNull(context.receiveMessage());
    }

    private String parseLocalPath(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        return parseRequiredSubOption(context, cmd, COMMAND_SUB_OPTION_LOCAL_PATH, "请输入本地文件路径:");
    }

    private String parseRemoteFilePath(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        return parseRequiredSubOption(context, cmd, COMMAND_SUB_OPTION_REMOTE_FILE_PATH, "请输入远端文件路径:");
    }

    private String parseRemoteDirectoryPath(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        return parseRequiredSubOption(
                context, cmd, COMMAND_SUB_OPTION_REMOTE_DIRECTORY_PATH, "请输入远端目录路径（根目录可输入 /）:"
        );
    }

    private String parseOldRemoteFilePath(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        return parseRequiredSubOption(context, cmd, COMMAND_SUB_OPTION_OLD_REMOTE_FILE_PATH, "请输入旧远端文件路径:");
    }

    private String parseNewRemoteFilePath(CommandExecutor.Context context, CommandLine cmd) throws Exception {
        return parseRequiredSubOption(context, cmd, COMMAND_SUB_OPTION_NEW_REMOTE_FILE_PATH, "请输入新远端文件路径:");
    }

    private String parseRequiredSubOption(
            CommandExecutor.Context context, CommandLine cmd, String option, String prompt
    ) throws Exception {
        if (cmd.hasOption(option)) {
            String value = StringUtils.trimToNull(cmd.getOptionValue(option));
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        context.sendMessage(prompt);
        String value = StringUtils.trimToNull(context.receiveMessage());
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return value;
    }

    private FtpFileLocation parseRemoteFileLocation(String remoteFilePath) {
        String normalized = StringUtils.trimToEmpty(remoteFilePath);
        normalized = Strings.CS.replace(normalized, "\\", "/");
        if (
                StringUtils.isNotEmpty(normalized) &&
                        !Strings.CS.equals(normalized, "/") && Strings.CS.endsWith(normalized, "/")
        ) {
            throw new IllegalArgumentException("远端文件路径不能以目录分隔符结尾");
        }

        String[] segments = parseRemotePathSegments(normalized);
        if (segments.length == 0) {
            throw new IllegalArgumentException("远端文件路径不能为空");
        }

        String fileName = segments[segments.length - 1];
        String[] filePaths = Arrays.copyOf(segments, segments.length - 1);
        return new FtpFileLocation(filePaths, fileName);
    }

    private String[] parseRemoteDirectoryPath(String remoteDirectoryPath) {
        return parseRemotePathSegments(remoteDirectoryPath);
    }

    private String[] parseRemotePathSegments(String remotePath) {
        String normalized = StringUtils.trimToEmpty(remotePath);
        normalized = Strings.CS.replace(normalized, "\\", "/");

        if (StringUtils.isEmpty(normalized) || Strings.CS.equals(normalized, "/")) {
            return new String[0];
        }

        String[] splits = StringUtils.split(normalized, '/');
        List<String> segmentList = new ArrayList<>();
        for (String split : splits) {
            String segment = StringUtils.trimToNull(split);
            if (StringUtils.isNotEmpty(segment)) {
                segmentList.add(segment);
            }
        }
        return segmentList.toArray(new String[0]);
    }

    private File parseUploadFile(String localPath) {
        File localFile = new File(localPath);
        if (!localFile.exists()) {
            throw new IllegalArgumentException("本地文件不存在: " + localPath);
        }
        if (!localFile.isFile()) {
            throw new IllegalArgumentException("本地路径不是文件: " + localPath);
        }
        if (!localFile.canRead()) {
            throw new IllegalArgumentException("本地文件不可读: " + localPath);
        }
        return localFile;
    }

    private File parseDownloadFile(String localPath) {
        File localFile = new File(localPath);
        if (localFile.exists() && localFile.isDirectory()) {
            throw new IllegalArgumentException("本地路径是目录, 不能用于下载文件: " + localPath);
        }
        File parent = localFile.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean mkdirFlag = parent.mkdirs();
            if (!mkdirFlag && !parent.exists()) {
                throw new IllegalStateException("无法创建本地目录: " + parent.getAbsolutePath());
            }
        }
        return localFile;
    }

    private String formatFileType(int ftpFileType) {
        switch (ftpFileType) {
            case Constants.FTP_FILE_TYPE_FILE:
                return "file";
            case Constants.FTP_FILE_TYPE_DIRECTORY:
                return "directory";
            case Constants.FTP_FILE_TYPE_SYMBOLIC_LINK:
                return "symbolic-link";
            case Constants.FTP_FILE_TYPE_UNKNOWN:
                return "unknown";
            default:
                return "undefined(" + ftpFileType + ")";
        }
    }

    private String normalizeHandlerNameForOutput(@Nullable String handlerName) {
        return StringUtils.defaultIfBlank(handlerName, "<default>");
    }
}
