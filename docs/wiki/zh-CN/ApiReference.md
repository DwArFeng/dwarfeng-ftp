# API Reference - API 参考

本文档用于集中说明 `dwarfeng-ftp` 对外提供的 FTP 处理器、QoS 处理器和 QoS 服务接口。
文档中的方法签名、异常类型和运行约束以当前源码为准；接入方式和完整配置示例请参考 [Usage Guide](./UsageGuide.md)。

## 综述

`dwarfeng-ftp` 的 API 分为三个层次：

1. `FtpHandler`：面向单个 FTP 连接的文件、目录、流和高级文件操作。
2. `FtpQosHandler`：按照处理器名称管理多个 `FtpHandler`，并提供统一的聚合入口。
3. `FtpQosService`：在 QoS 处理器之上封装 `ServiceException`，适合业务服务和 spring-telqos 运维调用。

核心实现位于 `dwarfeng-ftp-core` 模块。常用实现类为 `FtpHandlerImpl`、`FtpQosHandlerImpl` 和
`FtpQosServiceImpl`。Spring 单例、多实例和 XSD 配置方式请参考 [Usage Guide](./UsageGuide.md) 的接入模式章节。

## 接口总览

| 接口            | 主要职责                        | 对外异常                           |
|-----------------|---------------------------------|------------------------------------|
| `FtpHandler`    | 操作一个 FTP 处理器及其连接     | `HandlerException` 及其 FTP 子类型 |
| `FtpQosHandler` | 按名称选择并调用多个 FTP 处理器 | `HandlerException`                 |
| `FtpQosService` | 为 QoS 调用提供服务层异常包装   | `ServiceException`                 |

### FtpHandler

`FtpHandler` 继承 `StartableHandler`，用于访问一个 FTP 服务端。 接口支持字节数组操作、流式操作、目录管理、 文件描述和文件迁移等能力。除
`isStarted()` 外， 文件操作通常要求处理器已经启动。

`FtpHandlerImpl` 的当前构造方式是传入 `ThreadPoolTaskScheduler` 和 `FtpConfig`。 旧版的多参数构造器仍然存在，但已经标记为弃用，新的代码应优先通过
`FtpConfig` 集中管理连接参数。

### FtpQosHandler

`FtpQosHandler` 将多个 `FtpHandler` 放在一个名称映射中。 其文件操作方法在 `FtpHandler` 的参数前增加可空的 `handlerName`：

- 只有一个托管处理器时，`handlerName` 可以为 `null`，系统会自动选择该处理器。
- 有多个托管处理器时，`handlerName` 为 `null` 会产生处理器歧义异常。
- 没有托管处理器时会产生无处理器异常。
- 指定不存在的名称时会产生处理器未找到异常。

`listHandlerNames()` 返回按字典序排序且不可变的处理器名称列表。
`stopAllManagedHandlers()` 也按名称顺序停止所有托管处理器；单个处理器停止失败时会记录告警并继续处理其余处理器。

### FtpQosService

`FtpQosService` 与 `FtpQosHandler` 提供基本对应的方法， 但将底层 `HandlerException` 通过 `ServiceExceptionMapper` 转换为
`ServiceException`。 业务服务或运维命令需要服务层异常时，应使用该接口， 而不应直接依赖实现类的内部异常转换逻辑。

## 生命周期

`FtpHandlerImpl` 内部使用锁保护处理器状态和 FTPClient。普通方法会在操作完成后释放锁；打开输入流或输出流后，
锁会保持到对应流关闭。使用流期间，同一处理器上的其它调用可能阻塞。

### 启动与停止

| 方法           | 行为                                                                           | 使用说明                       |
|----------------|--------------------------------------------------------------------------------|--------------------------------|
| `start()`      | 初始化 FTPClient，设置字符集、缓冲区、数据超时和数据连接模式，并安排 NOOP 任务 | 可重复调用；已经启动时直接返回 |
| `stop()`       | 取消 NOOP 任务，尝试登出并断开 FTP 连接，释放 FTPClient                        | 可重复调用；未启动时直接返回   |
| `isStarted()`  | 返回处理器的启动状态                                                           | 不执行 FTP 文件操作            |
| `connect()`    | 委托给 `start()`                                                               | 已弃用                         |
| `disconnect()` | 委托给 `stop()`                                                                | 已弃用                         |

在 Spring XML 中，可以通过 `init-method="start"` 和 `destroy-method="stop"` 将生命周期交给容器管理。
当启动阶段的首次连接失败时，实现会记录警告、安排保活任务并进入已启动状态；后续 NOOP 检查或业务调用会尝试重新连接。

### 自动保活与断线重连

处理器按照 `FtpConfig.noopInterval` 周期发送 `NOOP` 指令，以维持控制连接活跃。 业务操作前也会执行连接状态检查；如果 `NOOP`
失败，实现会尝试重新连接和登录，重连失败后再将异常向上抛出。

连接相关参数的默认值和校验规则如下：

- `connectTimeout` 默认为 `5000` 毫秒，必须大于 `1000` 毫秒。
- `noopInterval` 默认值为 `4000` 毫秒，必须小于 `connectTimeout`。
- `dataTimeout` 默认值为 `-1`，小于等于 `0` 表示不设置有限的数据超时。

### 启动状态与调用前提

除状态查询外，文件和目录操作在执行前都会确认处理器已经启动。 未启动时会抛出 `FtpHandlerStoppedException`。
处理器启动并不代表首次连接一定已经成功，连接状态仍由后续 NOOP 或业务检查维护。

## API 使用详解

文件操作通常提供两类重载：

1. `String[] filePaths` 与 `String fileName` 分开传入。
2. 使用 `FtpFileLocation` 封装路径和文件名。

路径数组从 FTP 根目录开始，按目录层级顺序排列；空数组表示根目录。文件名参数只用于文件操作，目录操作只使用目录路径。
除特别说明外，参数要求与接口中的 `@NotNull` 标记一致，执行失败时抛出 `HandlerException` 或具体 FTP 子类型。

### 基础文件操作

| 方法           | 返回值    | 说明                                           |
|----------------|-----------|------------------------------------------------|
| `existsFile`   | `boolean` | 检查目标文件是否存在；目标不存在时返回 `false` |
| `storeFile`    | `void`    | 以字节数组写入或覆盖目标文件                   |
| `retrieveFile` | `byte[]`  | 读取目标文件的全部内容并返回字节数组           |
| `deleteFile`   | `void`    | 删除目标文件；删除失败时抛出文件删除异常       |

调用这些方法时，实现会先进入指定目录；不存在的目录会尝试创建。因此，业务侧应为账号准备合适的目录创建权限， 并避免把未经校验的外部路径直接传入。

读取整个文件会将内容放入内存。对于大文件，应使用流式方法；上传和下载失败分别通常对应
`FtpFileStoreException` 与 `FtpFileRetrieveException`。

### 目录操作

| 方法              | 返回值      | 说明                                       |
|-------------------|-------------|--------------------------------------------|
| `listFiles`       | `FtpFile[]` | 返回目录下文件、目录或链接的描述对象       |
| `listFileNames`   | `String[]`  | 只返回目录下条目的名称，不带路径前缀       |
| `removeDirectory` | `void`      | 删除目录本身；目录必须为空，根目录不能删除 |
| `clearDirectory`  | `void`      | 递归删除目录下文件和子目录，但保留目录本身 |

`clearDirectory` 不是 FTP 协议的单条标准命令，而是通过列出目录、删除文件和删除子目录递归实现， 执行时间与目录内容数量相关。
在大目录上使用前，应评估执行时间、权限和失败后的清理策略。

### 流式操作

`storeFileByStream` 从调用者提供的 `InputStream` 读取内容并写入 FTP 文件，
`retrieveFileByStream` 将 FTP 文件写入调用者提供的 `OutputStream`。这两类方法不负责关闭调用者传入的流。

`openInputStream` 和 `openOutputStream` 返回与 FTP 数据连接关联的流。调用者必须在使用结束后关闭流，推荐使用
`try-with-resources`：

```java

@SuppressWarnings({"UnusedAssignment", "StatementWithEmptyBody"})
public void readFile(FtpHandler ftpHandler) throws Exception {
    try (InputStream in = ftpHandler.openInputStream(new String[]{"foobar"}, "readme.txt")) {
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) != -1) {
            // 处理本次读取的数据。
        }
    }
}
```

流式 API 有以下调用约束：

1. 获取流后应立即消费，不要长时间持有。
2. 流只能在创建它的线程中使用。
3. 流未关闭前，不要调用同一 `FtpHandler` 的其它方法。
4. 流打开失败时抛出 `FtpStreamOpenException`；传输过程中可能出现 `IOException`。

打开流后，处理器的锁会保持到流关闭。未关闭流可能造成其它线程长时间等待、FTP 数据连接未完成或后续操作失败。

### 高级文件操作

| 方法         | 说明                                                                 |
|--------------|----------------------------------------------------------------------|
| `renameFile` | 将源文件移动到新的完整路径；如果目标文件已存在，实现会先删除目标文件 |
| `moveFile`   | 与 `renameFile` 作用和实现相同，为语义更明确的别名                   |
| `copyFile`   | 读取源文件并写入目标文件；大文件可能使用临时文件系统存储             |
| `descFile`   | 查询指定文件的 `FtpFile` 描述；目标不存在时返回 `null`               |

`copyFile` 和 `descFile` 都不是 FTP 协议中的单条标准操作。 复制操作的耗时与文件大小相关，文件描述操作的耗时与目标目录条目数量相关。
业务侧应避免在超大文件或超大目录上无控制地频繁调用这些方法。

## 路径模型与 FtpFileLocation

### 数组路径模型

最常用的文件位置表达方式是 `String[] filePaths + String fileName`：

```java
public void storeFileByArrayPath(FtpHandler ftpHandler, byte[] content) throws Exception {
    String[] filePaths = new String[]{"foobar", "docs"};
    ftpHandler.storeFile(filePaths, "readme.txt", content);
}
```

上述参数表示 FTP 路径 `/foobar/docs/readme.txt`。目录操作只需要传入目录数组，例如
`listFiles(new String[]{"foobar", "docs"})`。

### 对象路径模型

可以使用 `FtpFileLocation` 将目录路径和文件名封装为一个对象：

```java
public void storeFileByLocation(FtpHandler ftpHandler, byte[] content) throws Exception {
    FtpFileLocation fileLocation = new FtpFileLocation(
            new String[]{"foobar", "docs"},
            "readme.txt"
    );
    ftpHandler.storeFile(fileLocation, content);
}
```

文件操作会同时使用 `getFilePaths()` 和 `getFileName()`；`removeDirectory`、`clearDirectory`、`listFiles` 和
`listFileNames` 只使用 `getFilePaths()`，忽略 `getFileName()`。

### 使用建议

`FtpFileLocation` 是 `final` 类，提供 JavaBean 风格的访问器和基于路径内容的 `equals`、`hashCode` 与 `toString`。
虽然类注释将其描述为不可变类，但当前实现直接保存并返回构造时传入的 `String[]`，不会复制数组。 因此，创建位置对象后不应再修改该数组，也不应把
`getFilePaths()` 返回的数组暴露给不受信任的调用方。

业务系统建议集中封装根路径和相对路径拼接逻辑，统一使用一种路径表达方式，并在边界处校验目录名和文件名。

## 返回值与 DTO

### FtpFile

`listFiles` 和 `descFile` 返回 `FtpFile`，其字段含义如下：

| 字段   | 类型     | 含义                                        |
|--------|----------|---------------------------------------------|
| `name` | `String` | FTP 条目的名称，不带路径前缀                |
| `type` | `int`    | FTP 条目类型，使用 `Constants` 中的类型常量 |
| `size` | `long`   | FTP 服务端报告的条目大小                    |

`FtpFile` 是一个可序列化 DTO，提供无参构造器、全参构造器和标准 getter/setter。 当 `descFile` 找不到目标时返回 `null`
，业务层需要先判断返回值再读取字段。

### 文件类型常量

| 常量                                    | 数值 | 含义     |
|-----------------------------------------|------|----------|
| `Constants.FTP_FILE_TYPE_FILE`          | `0`  | 普通文件 |
| `Constants.FTP_FILE_TYPE_DIRECTORY`     | `10` | 目录     |
| `Constants.FTP_FILE_TYPE_SYMBOLIC_LINK` | `20` | 符号链接 |
| `Constants.FTP_FILE_TYPE_UNKNOWN`       | `30` | 未知类型 |

实现会将 Apache Commons Net 的文件、目录和符号链接类型映射到上述常量，其它服务端类型映射为未知类型。

## 版本与弃用方法

### 连接方法

`connect()` 和 `disconnect()` 已标记为 `@Deprecated`，分别委托给 `start()` 和 `stop()`。 新代码应直接使用
`StartableHandler` 生命周期方法，以便与 Spring 生命周期和其它可启动处理器保持一致。

### 文件获取方法

`getFileContent(String[], String)` 是 `retrieveFile(String[], String)` 的兼容别名，
`getFileContentByStream(String[], String, OutputStream)` 是 `retrieveFileByStream(String[], String, OutputStream)`
的兼容别名。这两个旧方法只保留用于兼容已有代码，新代码应使用 `retrieve` 命名的方法。

`removeDirectory(String[], String)` 也已弃用。迁移时将目录名追加到路径数组，再调用 `removeDirectory(String[])`； 这样可以与
`FtpFileLocation` 重载的目录语义保持一致。

### 版本信息摘要

| 能力                                                 | 源码标注版本     |
|------------------------------------------------------|------------------|
| `FtpHandler` 基础接口                                | `1.0.0`          |
| 流式存储与获取方法                                   | `1.1.3`、`1.1.6` |
| `FtpFileLocation` 相关重载                           | `1.1.10`         |
| 文件列表与 `FtpFile` DTO                             | `1.1.4`          |
| `clearDirectory`、`copyFile`、`descFile`、`moveFile` | `1.2.0`          |
| 四种数据连接模式                                     | `1.3.0`          |
| `FtpQosHandler` 与 `FtpQosService`                   | `2.0.0`          |

具体版本兼容性和历史缺陷请结合 [Version Blacklist](./VersionBlacklist.md) 与发布记录判断。

## 参阅

- [Usage Guide](./UsageGuide.md) - 使用指南，说明接入方式、配置方式和完整使用流程。
- [Config Parameters](./ConfigParameters.md) - 配置参数详解，说明 `FtpConfig` 各字段含义与校验规则。
- [Data Connect Modes](./DataConnectModes.md) - 数据连接模式，介绍四种数据连接模式的区别与使用场景。
- [Extra Features](./ExtraFeatures.md) - 额外功能详解，说明自动连接管理、流式操作和高级文件能力。
- [Troubleshooting](./Troubleshooting.md) - 故障排查，说明连接、传输、编码和目录操作问题的定位方法。
- [Quick Start](./QuickStart.md) - 快速开始，包含最小化配置和示例入口。
