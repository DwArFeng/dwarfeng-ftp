# Extra Features - 额外功能详解

dwarfeng-ftp 是一个基于 Apache Commons Net 的 FTP 客户端库，在标准 FTP 协议基础上提供了许多额外的便利功能。
本文档详细介绍了这些额外功能的实现原理和使用方法。

## 目录

1. [自动连接管理](#自动连接管理)
2. [流式文件操作](#流式文件操作)
3. [高级文件操作](#高级文件操作)
4. [线程安全设计](#线程安全设计)

## 自动连接管理

### 连接保持机制

dwarfeng-ftp 实现了自动连接保持机制，通过定期发送 `NOOP` 指令来维持与 FTP 服务器的连接：

**技术实现**：

连接保持机制通过 `NoopSendTask` 类实现，该类作为定时任务运行：

- 使用 `ScheduledExecutorService` 定期执行 `NOOP` 指令。
- 当 NOOP 指令失败时，自动尝试重新连接。
- 所有连接操作都通过 `ReentrantLock` 确保线程安全。

### 自动重连机制

当检测到连接异常时，系统会自动尝试重新连接：

**技术实现**：

自动重连机制通过 `ensureStatus()` 方法实现：

- 在执行任何 FTP 操作前，先发送 `NOOP` 指令检查连接状态。
- 当 `NOOP` 指令失败时，调用 `connectAndLogin()` 方法重新建立连接。
- 重连失败时会抛出异常，确保操作的可信性。

### 配置参数

- `noopInterval`: `NOOP` 指令发送间隔（默认 `4000ms`）。
- `connectTimeout`: 连接超时时间（默认 `5000ms`）。
- `dataTimeout`: 数据连接超时时间（默认 `-1`，永不超时，需要在使用时按需修改）。

## 流式文件操作

### 输入流操作

dwarfeng-ftp 提供了流式的文件读取功能，支持大文件的高效处理：

接口方法：

```java
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 打开指定文件的输入流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法会在调用前检查 FTP 服务器的状态，如果 FTP 服务器不可用，会抛出异常。
     *
     * <p>
     * 文件在读取过程中，如果 FTP 服务器不可用，会抛出 {@link java.io.IOException} 异常。
     *
     * <p>
     * 该方法不会关闭流，需要调用者自行关闭，请 <b>务必</b> 在调用该方法结束后关闭流，否则会造成 FTP 服务器行为异常。
     *
     * <p>
     * 对于线程安全的实现，从调用开始直到用户关闭流的这段时间内，其它线程调用处理器的任何方法都应该被阻塞。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
     */
    @SuppressWarnings("JavadocReference")
    InputStream openInputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输入流。
     *
     * @param fileLocation 文件位置
     * @return 文件的输入流。
     * @throws HandlerException 处理器异常。
     * @see #openInputStream(String[], String)
     * @since 1.1.10
     */
    @SuppressWarnings("JavadocReference")
    InputStream openInputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

使用示例：

```java
public void processFile() throws Exception {
    try (InputStream in = ftpHandler.openInputStream(new String[]{"path"}, "file.txt")) {
        // 处理输入流。
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            // 处理数据。
            processData(buffer, bytesRead);
        }
    }
}
```

### 输出流操作

同样支持流式的文件写入：

接口方法：

```java
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 打开指定文件的输出流。
     *
     * <p>
     * 该方法返回的流应该在获取后立即使用，在关闭流之前，不得调用该处理器的任何其它方法。<br>
     * 该方法返回的流只能在本线程中使用，不应该在其他线程中使用。
     *
     * <p>
     * 该方法会在调用前检查 FTP 服务器的状态，如果 FTP 服务器不可用，会抛出异常。
     *
     * <p>
     * 文件在写入过程中，如果 FTP 服务器不可用，会抛出 {@link java.io.IOException} 异常。
     *
     * <p>
     * 该方法不会关闭流，需要调用者自行关闭，请 <b>务必</b> 在调用该方法结束后关闭流，否则会造成 FTP 服务器行为异常。
     *
     * <p>
     * 对于线程安全的实现，从调用开始直到用户关闭流的这段时间内，其它线程调用处理器的任何方法都应该被阻塞。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
     */
    @SuppressWarnings("JavadocReference")
    OutputStream openOutputStream(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 打开指定文件的输出流。
     *
     * @param fileLocation 文件位置。
     * @return 文件的输出流。
     * @throws HandlerException 处理器异常。
     * @see #openOutputStream(String[], String)
     * @since 1.1.10
     */
    @SuppressWarnings("JavadocReference")
    OutputStream openOutputStream(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

使用示例：

```java
public void writeFile(byte[] data) throws Exception {
    try (OutputStream out = ftpHandler.openOutputStream(new String[]{"path"}, "file.txt")) {
        // 写入数据。
        out.write(data);
        out.flush();
    }
}
```

### 流式存储和获取

提供了基于流的文件存储和获取方法：

接口方法：

```java
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 通过流的形式存储文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @param in        文件的输入流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.3
     */
    @SuppressWarnings("JavadocReference")
    void storeFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull InputStream in
    ) throws HandlerException;

    /**
     * 通过流的形式存储文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param fileLocation 文件位置。
     * @param in           文件的输入流。
     * @throws HandlerException 处理器异常。
     * @see #storeFileByStream(String[], String, InputStream)
     * @since 1.1.10
     */
    @SuppressWarnings("JavadocReference")
    void storeFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull InputStream in) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称
     * @param out       待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @since 1.1.6
     */
    @SuppressWarnings("JavadocReference")
    void retrieveFileByStream(
            @Nonnull String[] filePaths, @Nonnull String fileName, @Nonnull OutputStream out
    ) throws HandlerException;

    /**
     * 通过流的形式获取文件。
     *
     * <p>
     * FtpHandler 本身不维护流的生命周期，请在调用该方法前妥善启动流，并在调用该方法结束后妥善关闭流。
     *
     * @param fileLocation 文件位置。
     * @param out          待写入文件内容的输出流。
     * @throws HandlerException 处理器异常。
     * @see #retrieveFileByStream(String[], String, OutputStream)
     * @since 1.1.10
     */
    @SuppressWarnings("JavadocReference")
    void retrieveFileByStream(@Nonnull FtpFileLocation fileLocation, @Nonnull OutputStream out) throws HandlerException;

    // 其他方法省略...
}
```

## 高级文件操作

### 文件复制

实现了 FTP 协议标准之外的文件复制功能：

接口方法：

```java
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 复制文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先获取源文件的内容，再存储到目标文件中。
     * 对于大文件，可能还需要将文件存储到文件系统中。
     * 该方法的执行时间与文件的大小呈正相关。尽量不要在大文件上调用该方法。
     *
     * @param oldFilePaths 旧的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void copyFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 复制文件。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先获取源文件的内容，再存储到目标文件中。
     * 对于大文件，可能还需要将文件存储到文件系统中。
     * 该方法的执行时间与文件的大小呈正相关。尽量不要在大文件上调用该方法。
     *
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see #copyFile(String[], String, String[], String)
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void copyFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException;

    // 其他方法省略...
}
```

**实现特点**：

- 读取源文件内容并写入目标文件。
- 同时适用于小文件和大文件，处理小文件时保持高效，处理大文件时避免内存溢出。

**技术实现**：

- 始终保持固定大小的内存缓冲区（默认 `1MB`）。
- 数据优先写入内存缓冲区，直到缓冲区写满。
- 内存缓冲区写满后，剩余数据自动写入临时文件。
- 内存空间得到充分利用，不会浪费任何空间。

### 文件移动

提供了语义化的文件移动功能：

接口方法：

```java
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 移动文件。
     *
     * <p>
     * 移动文件与重命名文件的作用以及实现是完全一样的。增加该方法是为了满足语义上的需求，以及部分开发人员的使用习惯。
     *
     * @param oldFilePaths 旧的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param oldFileName  旧的文件的名称。
     * @param neoFilePaths 新的目录的路径。<br>
     *                     路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param neoFileName  新的文件的名称。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void moveFile(
            @Nonnull String[] oldFilePaths, @Nonnull String oldFileName,
            @Nonnull String[] neoFilePaths, @Nonnull String neoFileName
    ) throws HandlerException;

    /**
     * 移动文件。
     *
     * <p>
     * 移动文件与重命名文件的作用以及实现是完全一样的。增加该方法是为了满足语义上的需求，以及部分开发人员的使用习惯。
     *
     * @param oldFileLocation 旧的文件位置。
     * @param neoFileLocation 新的文件位置。
     * @throws HandlerException 处理器异常。
     * @see #moveFile(String[], String, String[], String)
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void moveFile(@Nonnull FtpFileLocation oldFileLocation, @Nonnull FtpFileLocation neoFileLocation)
            throws HandlerException;

    // 其他方法省略...
}
```

### 文件描述

提供了获取文件详细信息的功能：

接口方法：

```java
import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 描述文件。
     *
     * <p>
     * 当对应的文件存在时，返回对应的文件对象；当对应的文件不存在时，返回 null。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先列出目标文件所在目录下的所有文件，再查找目标文件。
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @param fileName  文件的名称。
     * @return 指定文件对应的文件对象。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    FtpFile descFile(@Nonnull String[] filePaths, @Nonnull String fileName) throws HandlerException;

    /**
     * 描述文件。
     *
     * <p>
     * 当对应的文件存在时，返回对应的文件对象；当对应的文件不存在时，返回 null。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要先列出目标文件所在目录下的所有文件，再查找目标文件。
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param fileLocation 文件位置。
     * @return 指定文件对应的文件对象。
     * @throws HandlerException 处理器异常。
     * @see #descFile(String[], String)
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    FtpFile descFile(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

返回的 `FtpFile` 对象包含：

- 文件名。
- 文件类型（文件/目录/符号链接）。
- 文件大小。

### 配置参数

- `fileCopyMemoryBufferSize`: 文件复制内存缓冲区大小（默认：`1048576` 字节 = `1MB`）。
- `temporaryFileDirectoryPath`: 临时文件目录路径（默认：系统临时目录）。
- `temporaryFilePrefix`: 临时文件前缀（默认：`"ftp-"`）。
- `temporaryFileSuffix`: 临时文件后缀（默认：`".tmp"`）。

## 目录管理功能

### 清空目录

实现了递归清空目录的功能：

接口方法：

```java
import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 清空目录。
     *
     * <p>
     * 递归地清空目录下的所有文件以及子目录。清空目录不会删除目录本身。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要按照递归逻辑多次列出目录下的文件并分别多次调用删除文件以及删除目录的方法，
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @throws HandlerException 处理器异常。
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void clearDirectory(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 清空目录。
     *
     * <p>
     * 递归地清空目录下的所有文件以及子目录。清空目录不会删除目录本身。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * <p>
     * 该功能不是 FTP 协议的标准功能，其实现需要按照递归逻辑多次列出目录下的文件并分别多次调用删除文件以及删除目录的方法，
     * 该方法的执行时间与目录下的文件数量呈正相关。尽量不要在大目录上调用该方法。
     *
     * @param fileLocation 文件位置。
     * @throws HandlerException 处理器异常。
     * @see #clearDirectory(String[])
     * @since 1.2.0
     */
    @SuppressWarnings("JavadocReference")
    void clearDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

**实现特点**：

- 递归删除目录下的所有文件和子目录。
- 不会删除目录本身。
- 执行时间与目录下文件数量呈正相关。

### 目录删除

提供了安全的目录删除功能：

接口方法：

```java
import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 删除目录。
     *
     * <p>
     * 在 FTP 协议中，删除一个目录，需要保证该目录下没有文件，否则会抛出异常。
     *
     * <p>
     * 如果希望删除一个包含文件的目录，需要先递归地删除目录下的所有文件和子目录。
     * 此时，可以使用 {@link #clearDirectory(String[])} 方法，该方法会清空目录下的所有文件，包括子目录。<br>
     * {@link #clearDirectory(String[])} 调用完成后，再调用本方法删除目录本身。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。<br>
     *                  如果需要删除的目录是根目录，那么该数组长度为 0。
     * @throws HandlerException 处理器异常。
     * @see #clearDirectory(String[])
     * @since 1.1.8
     */
    @SuppressWarnings("JavadocReference")
    void removeDirectory(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 删除目录。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * <p>
     * 在 FTP 协议中，删除一个目录，需要保证该目录下没有文件，否则会抛出异常。
     *
     * <p>
     * 如果希望删除一个包含文件的目录，需要先递归地删除目录下的所有文件和子目录。
     * 此时，可以使用 {@link #clearDirectory(String[])} 方法，该方法会清空目录下的所有文件，包括子目录。<br>
     * {@link #clearDirectory(String[])} 调用完成后，再调用本方法删除目录本身。
     *
     * @see #removeDirectory(String[])
     * @see #clearDirectory(FtpFileLocation)
     * @since 1.1.10
     */
    @SuppressWarnings("JavadocReference")
    void removeDirectory(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

**注意事项**：

- 只能删除空目录。
- 删除包含文件的目录需要先调用 `clearDirectory()`。

### 文件列表

提供了多种文件列表功能：

接口方法：

```java
import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import javax.annotation.Nonnull;

public interface FtpHandler extends StartableHandler {

    // 其他方法省略...

    /**
     * 列出指定路径下的所有文件。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @return 指定目录下所有文件组成的数组。
     * @throws HandlerException 处理器异常。
     * @since 1.1.4
     */
    @SuppressWarnings("JavadocReference")
    FtpFile[] listFiles(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * @param fileLocation 文件位置。
     * @return 指定目录下所有文件组成的数组。
     * @see #listFiles(String[])
     * @since 1.1.10
     */
    FtpFile[] listFiles(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 如果有专用的目录（这种目录通常只存放一种类型的文件），那么直接获取名称会更方便一些。
     *
     * @param filePaths 目录路径。<br>
     *                  路径从根文件出发，一直到达最后一个目录，所有目录按照顺序组成数组。
     * @return 指定目录下所有文件的名称（不带路径前缀）组成的数组。
     * @throws HandlerException 处理器异常。
     * @since 1.1.4
     */
    @SuppressWarnings("JavadocReference")
    String[] listFileNames(@Nonnull String[] filePaths) throws HandlerException;

    /**
     * 列出指定路径下的所有文件。
     *
     * <p>
     * 执行该方法时，只使用 {@link FtpFileLocation#getFilePaths()} 方法返回的路径，
     * 忽略 {@link FtpFileLocation#getFileName()} 方法返回的文件名。
     *
     * @param fileLocation 文件位置。
     * @return 指定目录下所有文件的名称（不带路径前缀）组成的数组。
     * @see #listFileNames(String[])
     * @since 1.1.10
     */
    String[] listFileNames(@Nonnull FtpFileLocation fileLocation) throws HandlerException;

    // 其他方法省略...
}
```

## 线程安全设计

### 锁机制

dwarfeng-ftp 使用 `ReentrantLock` 确保线程安全：

**技术实现**：

线程安全通过以下机制保证：

- 所有公共方法都使用 `ReentrantLock` 进行同步。
- 流操作期间会持有锁，直到流关闭才释放。
- 连接保持和重连操作都在锁保护下进行。

### 流操作的线程安全

流操作具有特殊的线程安全机制：

- 流打开时会加锁。
- 流关闭时才会解锁。
- 在流使用期间，其他线程调用任何方法都会被阻塞。

## 使用示例

### 基本文件操作

示例代码：

```java
public void basicFileOperations() throws Exception {
    // 存储文件。
    ftpHandler.storeFile(new String[]{"dir1", "dir2"}, "file.txt", content);

    // 获取文件。
    byte[] content = ftpHandler.retrieveFile(new String[]{"dir1", "dir2"}, "file.txt");

    // 检查文件是否存在。
    boolean exists = ftpHandler.existsFile(new String[]{"dir1", "dir2"}, "file.txt");

    // 删除文件。
    ftpHandler.deleteFile(new String[]{"dir1", "dir2"}, "file.txt");
}
```

### 流式操作

示例代码：

```java
public void streamOperations() throws Exception {
    // 流式存储。
    try (
            InputStream in = new FileInputStream("local.txt");
            OutputStream out = ftpHandler.openOutputStream(new String[]{"remote"}, "remote.txt")
    ) {
        IOUtil.trans(in, out, 4096);
    }

    // 流式获取。
    try (
            InputStream in = ftpHandler.openInputStream(new String[]{"remote"}, "remote.txt");
            OutputStream out = new FileOutputStream("local.txt")
    ) {
        IOUtil.trans(in, out, 4096);
    }
}
```

### 高级操作

示例代码：

```java
public void advancedOperations() throws Exception {
    // 复制文件。
    ftpHandler.copyFile(
            new String[]{"source"}, "source.txt",
            new String[]{"target"}, "target.txt"
    );

    // 移动文件。
    ftpHandler.moveFile(
            new String[]{"source"}, "source.txt",
            new String[]{"target"}, "target.txt"
    );

    // 清空目录。
    ftpHandler.clearDirectory(new String[]{"directory"});

    // 列出文件。
    FtpFile[] files = ftpHandler.listFiles(new String[]{"directory"});
    for (FtpFile file : files) {
        System.out.println("文件名: " + file.getName() + ", 类型: " + file.getType() + ", 大小: " + file.getSize());
    }
}
```
