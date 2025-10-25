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

```java
// 打开文件输入流。
InputStream openInputStream(String[] filePaths, String fileName) throws HandlerException;

// 使用示例。
@SuppressWarnings({"StatementWithEmptyBody", "UnusedAssignment"})
public void processFile() throws Exception {
    try (InputStream in = ftpHandler.openInputStream(new String[]{"path"}, "file.txt")) {
        // 处理输入流。
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            // 处理数据。
        }
    }
}
```

### 输出流操作

同样支持流式的文件写入：

```java
// 打开文件输出流。
OutputStream openOutputStream(String[] filePaths, String fileName) throws HandlerException;

// 使用示例。
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

```java
// 通过流存储文件。
void storeFileByStream(String[] filePaths, String fileName, InputStream in) throws HandlerException;

// 通过流获取文件。
void retrieveFileByStream(String[] filePaths, String fileName, OutputStream out) throws HandlerException;
```

## 高级文件操作

### 文件复制

实现了 FTP 协议标准之外的文件复制功能：

```java
// 复制文件。
void copyFile(String[] oldFilePaths, String oldFileName, String[] neoFilePaths, String neoFileName)
        throws HandlerException;
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

```java
// 移动文件（实际为重命名）。
default void moveFile(String[] oldFilePaths, String oldFileName, String[] neoFilePaths, String neoFileName)
        throws HandlerException {
    renameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
}
```

### 文件描述

提供了获取文件详细信息的功能：

```java
// 描述文件。
FtpFile descFile(String[] filePaths, String fileName) throws HandlerException;
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

```java
// 清空目录（递归删除所有文件和子目录）。
void clearDirectory(String[] filePaths) throws HandlerException;
```

**实现特点**：

- 递归删除目录下的所有文件和子目录。
- 不会删除目录本身。
- 执行时间与目录下文件数量呈正相关。

### 目录删除

提供了安全的目录删除功能：

```java
// 删除目录。
void removeDirectory(String[] filePaths) throws HandlerException;
```

**注意事项**：

- 只能删除空目录。
- 删除包含文件的目录需要先调用 `clearDirectory()`。

### 文件列表

提供了多种文件列表功能：

```java
// 列出文件（返回 FtpFile 对象数组）。
FtpFile[] listFiles(String[] filePaths) throws HandlerException;

// 列出文件名（返回字符串数组）。
String[] listFileNames(String[] filePaths) throws HandlerException;
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

```java
// 基本文件操作示例。
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

```java
// 流式操作示例。
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

```java
// 高级操作示例。
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
