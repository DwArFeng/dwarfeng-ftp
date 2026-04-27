# Usage Guide - 使用指南

## 综述

本文档用于系统说明 `dwarfeng-ftp` 的落地使用方式。

`dwarfeng-ftp` 是一个基于 `Apache Commons Net` 的 FTP 处理器组件，
核心目标是让业务侧以统一 API 完成文件上传、下载、流式传输、目录管理与高级文件操作。

相比直接操作 `FTPClient`，该组件额外提供了以下能力：

- 统一的 `FtpHandler` 抽象，屏蔽多数协议细节。
- 启动后自动保活（周期 `NOOP`）与断线重连。
- 自动创建不存在的目录路径。
- 面向业务语义的高级方法（如 `copyFile`、`clearDirectory`、`descFile`、`moveFile`）。
- 统一异常包装与线程安全实现。

本文将按“快速接入 -> 参数配置 -> API 说明 -> 运行约束 -> 最佳实践”的顺序展开，目标是让您可以直接复制配置并落地到自己的工程中。

## 快速开始

### 添加依赖

在工程 `pom.xml` 中添加依赖：

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--suppress MavenModelInspection, MavenModelVersionMissed -->
<project
        xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
        http://maven.apache.org/xsd/maven-4.0.0.xsd"
>

    <!-- 省略其他配置 -->
    <dependencies>
        <!-- 省略其他配置 -->
        <dependency>
            <groupId>com.dwarfeng</groupId>
            <artifactId>dwarfeng-ftp</artifactId>
            <version>${dwarfeng-ftp.version}</version>
        </dependency>
        <!-- 省略其他配置 -->
    </dependencies>
    <!-- 省略其他配置 -->
</project>
```

### 基础 Spring 配置

`dwarfeng-ftp` 的单例接入通常需要三部分配置：

1. 扫描 `SingletonConfiguration`。
2. 提供调度器 `scheduler`（用于 NOOP 保活任务）。
3. 加载 `ftp.*` 配置参数。

`application-context-scan.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
>

    <!-- 扫描 dwarfeng-ftp 的配置类。 -->
    <context:component-scan base-package="com.dwarfeng.ftp.configuration" use-default-filters="false">
        <context:include-filter
                type="assignable"
                expression="com.dwarfeng.ftp.configuration.SingletonConfiguration"
        />
    </context:component-scan>
</beans>
```

`application-context-task.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:task="http://www.springframework.org/schema/task"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/task
        http://www.springframework.org/schema/task/spring-task.xsd"
>

    <!-- 装配 scheduler。 -->
    <task:scheduler id="scheduler" pool-size="1"/>
</beans>
```

`application-context-placeholder.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
>

    <!-- 加载 classpath 下的 ftp 配置。 -->
    <context:property-placeholder
            location="classpath:ftp/*.properties"
            local-override="true"
    />
</beans>
```

### 准备最小化参数

`src/main/resources/ftp/connection.properties`：

```properties
# FTP 的主机名称。
ftp.host=your-host-here
# FTP 的端口号。
ftp.port=21
# FTP 的登录用户名。
ftp.username=your-username-here
# FTP 的登录密码。
ftp.password=your-password-here
# FTP 的服务端字符集。
ftp.server_charset=UTF-8
# FTP 的连接超时时长（毫秒）。
ftp.connect_timeout=5000
# FTP 的 noop 指令周期（毫秒），需要小于 ftp.connect_timeout。
ftp.noop_interval=4000
# FTP 的缓冲区大小。
ftp.buffer_size=4096
# FTP 的临时文件目录。
ftp.temporary_file_directory_path=temp
# FTP 临时文件的前缀。
ftp.temporary_file_prefix=ftp-
# FTP 临时文件的后缀。
ftp.temporary_file_suffix=.tmp
# FTP 文件拷贝功能的内存缓冲区大小。
ftp.file_copy_memory_buffer_size=1048576
# FTP 的数据连接模式（0/1/2/3）。
ftp.data_connection_mode=0
# FTP 的数据超时时间。
ftp.data_timeout=-1
# ftp.data_connection_mode=1 时生效。
ftp.active_remote_data_connection_mode_server_host=your-host-here
# ftp.data_connection_mode=1 时生效。
ftp.active_remote_data_connection_mode_server_port=20
```

### 创建第一个使用类

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@SuppressWarnings("UnnecessaryModifier")
public class FoobarQuickStart {

    public static void main(String[] args) throws Exception {
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/application-context*.xml"
        )) {
            ctx.registerShutdownHook();
            ctx.start();

            FtpHandler ftpHandler = ctx.getBean(FtpHandler.class);

            String[] barPath = new String[]{"foobar", "quick-start"};
            String bazName = "hello.txt";
            byte[] bazContent = "hello dwarfeng-ftp".getBytes(StandardCharsets.UTF_8);

            ftpHandler.storeFile(barPath, bazName, bazContent);
            byte[] bazReadBack = ftpHandler.retrieveFile(barPath, bazName);

            System.out.println("内容一致: " + Arrays.equals(bazContent, bazReadBack));

            ftpHandler.deleteFile(barPath, bazName);
            ftpHandler.removeDirectory(barPath);
            ftpHandler.removeDirectory(new String[]{"foobar"});
        }
    }
}
```

### 启动与验证

可以通过以下方式验证接入结果：

1. 启动应用后无初始化异常。
2. `storeFile` 后，FTP 服务端可看到目标文件。
3. `retrieveFile` 返回内容与原始字节数组一致。
4. `deleteFile` 与 `removeDirectory` 能完成资源清理。

如果在第 2-4 步出现异常，可先检查 `ftp.host`、`ftp.username`、`ftp.password` 与 FTP 服务端权限配置。

## 配置详解

### 配置加载方式

#### 单例模式配置加载

使用 `SingletonConfiguration` 时，`ftp.*` 参数通过 `@Value` 注入。典型加载路径是：

- `classpath:ftp/*.properties`。
- 可选本地覆盖路径（例如 `file:conf/xxx/*.properties`）。

#### 多实例模式配置加载

使用 XML 或配置类手动创建多个处理器实例时，通常通过后缀区分参数，例如：

- `ftp.host.1`、`ftp.port.1`。
- `ftp.host.2`、`ftp.port.2`。

### 基础连接参数

#### `ftp.host`

- 类型：`String`。
- 默认值：无。
- 说明：FTP 服务器主机名或 IP。
- 约束：不能为空。

#### `ftp.port`

- 类型：`int`。
- 默认值：`21`。
- 说明：FTP 服务端口。
- 约束：`0 - 65535`。

#### `ftp.username`

- 类型：`String`。
- 默认值：无。
- 说明：FTP 登录用户名。
- 约束：不能为空。

#### `ftp.password`

- 类型：`String`。
- 默认值：无。
- 说明：FTP 登录密码。
- 约束：允许为 `null`。

#### `ftp.server_charset`

- 类型：`String`。
- 默认值：`UTF-8`。
- 说明：控制通道字符集，影响中文目录/文件名编码。
- 约束：不能为空。

### 连接与超时参数

#### `ftp.connect_timeout`

- 类型：`int`。
- 默认值：`5000`（毫秒）。
- 说明：连接超时时间。
- 约束：必须大于 `1000`。

#### `ftp.noop_interval`

- 类型：`long`。
- 默认值：`4000`（毫秒）。
- 说明：NOOP 保活周期。
- 约束：必须小于 `ftp.connect_timeout`。

#### `ftp.buffer_size`

- 类型：`int`。
- 默认值：`4096`。
- 说明：FTPClient 的缓冲区大小。
- 约束：允许负数，负数表示使用系统默认值。

### 临时文件与复制参数

#### `ftp.temporary_file_directory_path`

- 类型：`String`。
- 默认值：`java.io.tmpdir`。
- 说明：复制大文件时的临时文件目录。
- 约束：目录路径不能为空；若不存在将尝试创建；必须是目录且可读写。

#### `ftp.temporary_file_prefix`

- 类型：`String`。
- 默认值：`ftp-`。
- 说明：临时文件名前缀。
- 约束：不能为空。

#### `ftp.temporary_file_suffix`

- 类型：`String`。
- 默认值：`.tmp`。
- 说明：临时文件名后缀。
- 约束：不能为空。

#### `ftp.file_copy_memory_buffer_size`

- 类型：`int`。
- 默认值：`1048576`（1 MiB）。
- 说明：`copyFile` 的内存缓冲区大小。
- 约束：必须大于 `0`。

### 数据连接模式参数

#### `ftp.data_connection_mode`

- 类型：`int`。
- 默认值：`0`。
- 说明：数据连接模式。
- 枚举：
  `0` 本地主动模式（PORT）
  `1` 远程主动模式
  `2` 本地被动模式（PASV）
  `3` 远程被动模式
- 约束：只能取上述 4 个值。

#### `ftp.data_timeout`

- 类型：`int`。
- 默认值：`-1`。
- 说明：数据连接超时时间（毫秒），`<=0` 表示永不超时。
- 建议：在 `data_connection_mode=0` 场景下，建议设置为大于 `0`。

#### `ftp.active_remote_data_connection_mode_server_host`

- 类型：`String`。
- 默认值：`null`。
- 说明：远程主动模式服务主机。
- 约束：仅在 `data_connection_mode=1` 时必填。

#### `ftp.active_remote_data_connection_mode_server_port`

- 类型：`int`。
- 默认值：`-1`。
- 说明：远程主动模式服务端口。
- 约束：仅在 `data_connection_mode=1` 时必填，且范围 `0 - 65535`。

### 完整参数模板

```properties
# FTP 的主机名称。
ftp.host=your-host-here
# FTP 的端口号。
ftp.port=21
# FTP 的登录用户名。
ftp.username=your-username-here
# FTP 的登录密码。
ftp.password=your-password-here
# FTP 的服务端字符集。
ftp.server_charset=UTF-8
# FTP 的连接超时时长（毫秒）。
ftp.connect_timeout=5000
# FTP 的 noop 指令周期。该值需要小于 ftp.connect_timeout。
ftp.noop_interval=4000
# FTP 的缓冲区大小。
ftp.buffer_size=4096
# FTP 的临时文件目录。
ftp.temporary_file_directory_path=temp
# FTP 临时文件的前缀。
ftp.temporary_file_prefix=ftp-
# FTP 临时文件的后缀。
ftp.temporary_file_suffix=.tmp
# FTP 文件复制功能的内存缓冲区大小。
ftp.file_copy_memory_buffer_size=1048576
# FTP 的数据连接模式。
ftp.data_connection_mode=0
# FTP 的数据超时时间。
ftp.data_timeout=3000
# FTP 远程主动数据连接模式下的服务主机地址。
ftp.active_remote_data_connection_mode_server_host=your-host-here
# FTP 远程主动数据连接模式下的服务端口。
ftp.active_remote_data_connection_mode_server_port=20
```

### 参数校验规则总结

配置构建为 `FtpConfig` 时会触发校验，常见约束如下：

- `host`、`username`、`serverCharset`、临时文件参数不能为 `null`。
- `port` 与远程主动模式端口必须在 `0 - 65535`。
- `connectTimeout` 必须大于 `1000`。
- `noopInterval` 必须小于 `connectTimeout`。
- `fileCopyMemoryBufferSize` 必须大于 `0`。
- `dataConnectionMode` 只能是 `0/1/2/3`。
- `dataConnectionMode=1` 时，远程主动模式 host/port 必填。

违反约束时通常会抛出 `NullPointerException` 或 `IllegalArgumentException`。

### 场景化配置模板

以下模板用于快速落地不同网络环境，您可以在此基础上微调。

#### 模板 A：内网稳定环境（本地主动）

```properties
ftp.host=10.0.0.8
ftp.port=21
ftp.username=foo
ftp.password=bar
ftp.server_charset=UTF-8
ftp.connect_timeout=5000
ftp.noop_interval=4000
ftp.buffer_size=4096
ftp.temporary_file_directory_path=temp
ftp.temporary_file_prefix=ftp-
ftp.temporary_file_suffix=.tmp
ftp.file_copy_memory_buffer_size=1048576
ftp.data_connection_mode=0
ftp.data_timeout=3000
ftp.active_remote_data_connection_mode_server_host=your-host-here
ftp.active_remote_data_connection_mode_server_port=20
```

#### 模板 B：互联网/NAT 环境（本地被动）

```properties
ftp.host=example-ftp-host
ftp.port=21
ftp.username=foo
ftp.password=bar
ftp.server_charset=UTF-8
ftp.connect_timeout=8000
ftp.noop_interval=6000
ftp.buffer_size=8192
ftp.temporary_file_directory_path=temp
ftp.temporary_file_prefix=ftp-
ftp.temporary_file_suffix=.tmp
ftp.file_copy_memory_buffer_size=2097152
ftp.data_connection_mode=2
ftp.data_timeout=5000
ftp.active_remote_data_connection_mode_server_host=your-host-here
ftp.active_remote_data_connection_mode_server_port=20
```

#### 模板 C：服务器到服务器传输（远程主动）

```properties
ftp.host=source-ftp-host
ftp.port=21
ftp.username=foo
ftp.password=bar
ftp.server_charset=UTF-8
ftp.connect_timeout=8000
ftp.noop_interval=6000
ftp.buffer_size=4096
ftp.temporary_file_directory_path=temp
ftp.temporary_file_prefix=ftp-
ftp.temporary_file_suffix=.tmp
ftp.file_copy_memory_buffer_size=1048576
ftp.data_connection_mode=1
ftp.data_timeout=5000
ftp.active_remote_data_connection_mode_server_host=target-ftp-host
ftp.active_remote_data_connection_mode_server_port=20
```

#### 模板 D：大文件处理优先

```properties
ftp.host=your-host-here
ftp.port=21
ftp.username=foo
ftp.password=bar
ftp.server_charset=UTF-8
ftp.connect_timeout=10000
ftp.noop_interval=8000
ftp.buffer_size=16384
ftp.temporary_file_directory_path=D:/ftp-temp
ftp.temporary_file_prefix=ftp-large-
ftp.temporary_file_suffix=.tmp
ftp.file_copy_memory_buffer_size=8388608
ftp.data_connection_mode=2
ftp.data_timeout=10000
ftp.active_remote_data_connection_mode_server_host=your-host-here
ftp.active_remote_data_connection_mode_server_port=20
```

#### 模板选型建议

1. 能稳定互通且可控网络优先模板 A。
2. 客户端处于防火墙/NAT 后优先模板 B。
3. 需要远程主动模式或 FXP 类场景选择模板 C。
4. 长时间、大文件吞吐敏感场景选择模板 D。

## 接入模式

### 单例模式（推荐）

单例模式由 `SingletonConfiguration` 提供，适用于绝大多数业务工程。

`application-context-scan.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
>

    <context:component-scan base-package="com.dwarfeng.ftp.configuration" use-default-filters="false">
        <context:include-filter
                type="assignable"
                expression="com.dwarfeng.ftp.configuration.SingletonConfiguration"
        />
    </context:component-scan>
</beans>
```

该模式下，`FtpHandler` 会按 Spring Bean 生命周期自动 `start/stop`。

### 多实例模式

多实例模式不依赖扫描配置类，直接通过 XML 手动创建多个 `FtpHandlerImpl` 实例。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress SpringBeanConstructorArgInspection, SpringXmlModelInspection, SpringPlaceholdersInspection -->
<beans
        xmlns:task="http://www.springframework.org/schema/task"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/task
        http://www.springframework.org/schema/task/spring-task.xsd"
>
    <task:scheduler id="scheduler" pool-size="2"/>

    <!-- 第 1 个实例。 -->
    <bean name="fooConfigBuilder" class="com.dwarfeng.ftp.struct.FtpConfig.Builder">
        <constructor-arg name="host" value="${ftp.host.1}"/>
        <constructor-arg name="username" value="${ftp.username.1}"/>
        <constructor-arg name="password" value="${ftp.password.1}"/>
        <property name="port" value="${ftp.port.1}"/>
        <property name="serverCharset" value="${ftp.server_charset.1}"/>
        <property name="connectTimeout" value="${ftp.connect_timeout.1}"/>
        <property name="noopInterval" value="${ftp.noop_interval.1}"/>
        <property name="bufferSize" value="${ftp.buffer_size.1}"/>
        <property name="temporaryFileDirectoryPath" value="${ftp.temporary_file_directory_path.1}"/>
        <property name="temporaryFilePrefix" value="${ftp.temporary_file_prefix.1}"/>
        <property name="temporaryFileSuffix" value="${ftp.temporary_file_suffix.1}"/>
        <property name="fileCopyMemoryBufferSize" value="${ftp.file_copy_memory_buffer_size.1}"/>
        <property name="dataConnectionMode" value="${ftp.data_connection_mode.1}"/>
        <property name="dataTimeout" value="${ftp.data_timeout.1}"/>
        <property
                name="activeRemoteDataConnectionModeServerHost"
                value="${ftp.active_remote_data_connection_mode_server_host.1}"
        />
        <property
                name="activeRemoteDataConnectionModeServerPort"
                value="${ftp.active_remote_data_connection_mode_server_port.1}"
        />
    </bean>
    <bean name="fooConfig" factory-bean="fooConfigBuilder" factory-method="build"/>
    <bean
            name="fooFtpHandler"
            class="com.dwarfeng.ftp.handler.FtpHandlerImpl"
            init-method="start"
            destroy-method="stop"
    >
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="fooConfig"/>
    </bean>

    <!-- 第 2 个实例。 -->
    <bean name="barConfigBuilder" class="com.dwarfeng.ftp.struct.FtpConfig.Builder">
        <constructor-arg name="host" value="${ftp.host.2}"/>
        <constructor-arg name="username" value="${ftp.username.2}"/>
        <constructor-arg name="password" value="${ftp.password.2}"/>
        <property name="port" value="${ftp.port.2}"/>
        <property name="serverCharset" value="${ftp.server_charset.2}"/>
        <property name="connectTimeout" value="${ftp.connect_timeout.2}"/>
        <property name="noopInterval" value="${ftp.noop_interval.2}"/>
        <property name="bufferSize" value="${ftp.buffer_size.2}"/>
        <property name="temporaryFileDirectoryPath" value="${ftp.temporary_file_directory_path.2}"/>
        <property name="temporaryFilePrefix" value="${ftp.temporary_file_prefix.2}"/>
        <property name="temporaryFileSuffix" value="${ftp.temporary_file_suffix.2}"/>
        <property name="fileCopyMemoryBufferSize" value="${ftp.file_copy_memory_buffer_size.2}"/>
        <property name="dataConnectionMode" value="${ftp.data_connection_mode.2}"/>
        <property name="dataTimeout" value="${ftp.data_timeout.2}"/>
        <property
                name="activeRemoteDataConnectionModeServerHost"
                value="${ftp.active_remote_data_connection_mode_server_host.2}"
        />
        <property
                name="activeRemoteDataConnectionModeServerPort"
                value="${ftp.active_remote_data_connection_mode_server_port.2}"
        />
    </bean>
    <bean name="barConfig" factory-bean="barConfigBuilder" factory-method="build"/>
    <bean
            name="barFtpHandler"
            class="com.dwarfeng.ftp.handler.FtpHandlerImpl"
            init-method="start"
            destroy-method="stop"
    >
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="barConfig"/>
    </bean>
</beans>
```

### 任意数量实例模式

当实例数量在运行时动态变化，可自定义工厂类统一管理处理器生命周期。

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.handler.FtpHandlerImpl;
import com.dwarfeng.ftp.struct.FtpConfig;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FooFtpHandlerFactory {

    private final ThreadPoolTaskScheduler scheduler;
    private final Map<String, FtpHandler> cache = new ConcurrentHashMap<>();

    public FooFtpHandlerFactory(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public FtpHandler getOrCreate(String key, FtpConfig config) throws Exception {
        FtpHandler exists = cache.get(key);
        if (exists != null) {
            return exists;
        }
        synchronized (this) {
            FtpHandler secondCheck = cache.get(key);
            if (secondCheck != null) {
                return secondCheck;
            }
            FtpHandler created = new FtpHandlerImpl(scheduler, config);
            created.start();
            cache.put(key, created);
            return created;
        }
    }

    public void close(String key) throws Exception {
        FtpHandler handler = cache.remove(key);
        if (handler != null) {
            handler.stop();
        }
    }
}
```

### 接入模式选择建议

- 只有一个 FTP 目标或统一配置时，优先使用单例模式。
- 固定多个 FTP 目标时，优先使用多实例模式。
- 目标数量动态变化时，使用自定义工厂并显式管理 `start/stop`。

## API 使用详解

### 接口总览

`FtpHandler` 的主要能力分为 5 类：

1. 文件基础操作：`existsFile`、`storeFile`、`retrieveFile`、`deleteFile`。
2. 目录操作：`listFiles`、`listFileNames`、`removeDirectory`、`clearDirectory`。
3. 流式操作：`storeFileByStream`、`retrieveFileByStream`、`openInputStream`、`openOutputStream`。
4. 高级操作：`renameFile`、`moveFile`、`copyFile`、`descFile`。
5. 生命周期：`start`、`stop`、`isStarted`。

### 方法签名参考

以下签名可用于快速确认 API 能力边界（为便于阅读，省略了部分注解与 deprecated 方法）：

```java
package com.example.foobar;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.struct.FtpFileLocation;
import com.dwarfeng.subgrade.stack.exception.HandlerException;
import com.dwarfeng.subgrade.stack.handler.StartableHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class FtpHandlerSignatureReference {

    public interface SampleFtpHandler extends StartableHandler {

        boolean existsFile(String[] filePaths, String fileName) throws HandlerException;

        boolean existsFile(FtpFileLocation fileLocation) throws HandlerException;

        void storeFile(String[] filePaths, String fileName, byte[] content) throws HandlerException;

        void storeFile(FtpFileLocation fileLocation, byte[] content) throws HandlerException;

        byte[] retrieveFile(String[] filePaths, String fileName) throws HandlerException;

        byte[] retrieveFile(FtpFileLocation fileLocation) throws HandlerException;

        void storeFileByStream(String[] filePaths, String fileName, InputStream in) throws HandlerException;

        void storeFileByStream(FtpFileLocation fileLocation, InputStream in) throws HandlerException;

        void retrieveFileByStream(String[] filePaths, String fileName, OutputStream out) throws HandlerException;

        void retrieveFileByStream(FtpFileLocation fileLocation, OutputStream out) throws HandlerException;

        void deleteFile(String[] filePaths, String fileName) throws HandlerException;

        void deleteFile(FtpFileLocation fileLocation) throws HandlerException;

        void removeDirectory(String[] filePaths) throws HandlerException;

        void removeDirectory(FtpFileLocation fileLocation) throws HandlerException;

        FtpFile[] listFiles(String[] filePaths) throws HandlerException;

        FtpFile[] listFiles(FtpFileLocation fileLocation) throws HandlerException;

        String[] listFileNames(String[] filePaths) throws HandlerException;

        String[] listFileNames(FtpFileLocation fileLocation) throws HandlerException;

        InputStream openInputStream(String[] filePaths, String fileName) throws HandlerException;

        InputStream openInputStream(FtpFileLocation fileLocation) throws HandlerException;

        OutputStream openOutputStream(String[] filePaths, String fileName) throws HandlerException;

        OutputStream openOutputStream(FtpFileLocation fileLocation) throws HandlerException;

        void renameFile(
                String[] oldFilePaths, String oldFileName,
                String[] neoFilePaths, String neoFileName
        ) throws HandlerException;

        void renameFile(FtpFileLocation oldFileLocation, FtpFileLocation neoFileLocation) throws HandlerException;

        void clearDirectory(String[] filePaths) throws HandlerException;

        void clearDirectory(FtpFileLocation fileLocation) throws HandlerException;

        void copyFile(
                String[] oldFilePaths, String oldFileName,
                String[] neoFilePaths, String neoFileName
        ) throws HandlerException;

        void copyFile(FtpFileLocation oldFileLocation, FtpFileLocation neoFileLocation) throws HandlerException;

        FtpFile descFile(String[] filePaths, String fileName) throws HandlerException;

        FtpFile descFile(FtpFileLocation fileLocation) throws HandlerException;

        default void moveFile(
                String[] oldFilePaths, String oldFileName,
                String[] neoFilePaths, String neoFileName
        ) throws HandlerException {
            renameFile(oldFilePaths, oldFileName, neoFilePaths, neoFileName);
        }

        default void moveFile(FtpFileLocation oldFileLocation, FtpFileLocation neoFileLocation)
                throws HandlerException {
            renameFile(oldFileLocation, neoFileLocation);
        }
    }
}
```

### 常见方法组合

实际业务中通常不会孤立调用某个方法，而是按场景组合使用：

1. 上传替换：`existsFile` -> `storeFile`。
2. 下载回传：`retrieveFile` -> 业务层解析 -> 返回客户端。
3. 文件迁移：`copyFile` -> 校验 -> `deleteFile`。
4. 删除目录树：`clearDirectory` -> `removeDirectory`。
5. 在线流转：`openInputStream` -> 处理 -> `openOutputStream`。

### 基础文件操作

#### 判断文件是否存在

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class ExistsFileSnippet {

    public boolean exists(FtpHandler ftpHandler) throws Exception {
        return ftpHandler.existsFile(new String[]{"foobar", "docs"}, "foo.txt");
    }
}
```

#### 写入字节数组

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.nio.charset.StandardCharsets;

public class StoreFileSnippet {

    public void store(FtpHandler ftpHandler) throws Exception {
        byte[] bar = "hello".getBytes(StandardCharsets.UTF_8);
        ftpHandler.storeFile(new String[]{"foobar", "docs"}, "foo.txt", bar);
    }
}
```

#### 读取字节数组

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class RetrieveFileSnippet {

    public byte[] retrieve(FtpHandler ftpHandler) throws Exception {
        return ftpHandler.retrieveFile(new String[]{"foobar", "docs"}, "foo.txt");
    }
}
```

#### 删除文件

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class DeleteFileSnippet {

    public void delete(FtpHandler ftpHandler) throws Exception {
        ftpHandler.deleteFile(new String[]{"foobar", "docs"}, "foo.txt");
    }
}
```

#### 示例：`FoobarBinaryFileService`

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FoobarBinaryFileService {

    private final FtpHandler ftpHandler;

    public FoobarBinaryFileService(FtpHandler ftpHandler) {
        this.ftpHandler = ftpHandler;
    }

    public boolean writeAndCheck() throws Exception {
        String[] fooPath = new String[]{"foobar", "binary"};
        String barName = "bar.txt";

        byte[] source = "this is baz".getBytes(StandardCharsets.UTF_8);
        ftpHandler.storeFile(fooPath, barName, source);

        byte[] readBack = ftpHandler.retrieveFile(fooPath, barName);
        return Arrays.equals(source, readBack);
    }
}
```

### 目录操作

#### 列出目录下文件对象

```java
package com.example.foobar;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.handler.FtpHandler;

public class ListFilesSnippet {

    public void print(FtpHandler ftpHandler) throws Exception {
        FtpFile[] files = ftpHandler.listFiles(new String[]{"foobar", "docs"});
        for (FtpFile file : files) {
            System.out.println(file.getName() + " -> " + file.getType() + " -> " + file.getSize());
        }
    }
}
```

#### 仅列出文件名

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class ListFileNamesSnippet {

    public void print(FtpHandler ftpHandler) throws Exception {
        String[] names = ftpHandler.listFileNames(new String[]{"foobar", "docs"});
        for (String name : names) {
            System.out.println(name);
        }
    }
}
```

#### 删除目录

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class RemoveDirectorySnippet {

    public void remove(FtpHandler ftpHandler) throws Exception {
        // 目录必须为空。
        ftpHandler.removeDirectory(new String[]{"foobar", "docs"});
    }
}
```

#### 清空目录

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class ClearDirectorySnippet {

    public void clear(FtpHandler ftpHandler) throws Exception {
        // 清空后目录仍然存在。
        ftpHandler.clearDirectory(new String[]{"foobar", "docs"});
    }
}
```

#### 示例：`BarDirectoryService`

```java
package com.example.foobar;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.handler.FtpHandler;

public class BarDirectoryService {

    private final FtpHandler ftpHandler;

    public BarDirectoryService(FtpHandler ftpHandler) {
        this.ftpHandler = ftpHandler;
    }

    public void printFiles(String[] fooPath) throws Exception {
        FtpFile[] barFiles = ftpHandler.listFiles(fooPath);
        for (FtpFile baz : barFiles) {
            System.out.println(baz);
        }
    }
}
```

### 流式操作

#### 通过输入流上传

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.io.FileInputStream;
import java.io.InputStream;

public class UploadByInputStreamSnippet {

    public void upload(FtpHandler ftpHandler) throws Exception {
        try (InputStream in = new FileInputStream("foo.bin")) {
            ftpHandler.storeFileByStream(new String[]{"foobar", "stream"}, "foo.bin", in);
        }
    }
}
```

#### 通过输出流下载

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadToOutputStreamSnippet {

    public void download(FtpHandler ftpHandler) throws Exception {
        try (OutputStream out = new FileOutputStream("bar.bin")) {
            ftpHandler.retrieveFileByStream(new String[]{"foobar", "stream"}, "foo.bin", out);
        }
    }
}
```

#### 打开远端输入流

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.io.InputStream;

@SuppressWarnings("ALL")
public class OpenRemoteInputStreamSnippet {

    public void read(FtpHandler ftpHandler) throws Exception {
        try (InputStream in = ftpHandler.openInputStream(new String[]{"foobar", "stream"}, "foo.bin")) {
            byte[] buf = new byte[4096];
            while (in.read(buf) >= 0) {
                // 处理读取逻辑。
            }
        }
    }
}
```

#### 打开远端输出流

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OpenRemoteOutputStreamSnippet {

    public void write(FtpHandler ftpHandler) throws Exception {
        try (OutputStream out = ftpHandler.openOutputStream(new String[]{"foobar", "stream"}, "foo.bin")) {
            out.write("baz".getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }
}
```

#### 示例：`BazStreamService`

```java
package com.example.foobar;

import com.dwarfeng.dutil.basic.io.IOUtil;
import com.dwarfeng.ftp.handler.FtpHandler;

import java.io.*;

public class BazStreamService {

    private final FtpHandler ftpHandler;

    public BazStreamService(FtpHandler ftpHandler) {
        this.ftpHandler = ftpHandler;
    }

    public void upload(File fooFile) throws Exception {
        try (
                InputStream in = new FileInputStream(fooFile);
                OutputStream out = ftpHandler.openOutputStream(new String[]{"foobar", "stream"}, fooFile.getName())
        ) {
            IOUtil.trans(in, out, 4096);
        }
    }

    public void download(String barName, File bazTarget) throws Exception {
        try (
                InputStream in = ftpHandler.openInputStream(new String[]{"foobar", "stream"}, barName);
                OutputStream out = new FileOutputStream(bazTarget)
        ) {
            IOUtil.trans(in, out, 4096);
        }
    }
}
```

### 高级文件操作

#### 重命名

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class RenameFileSnippet {

    public void rename(FtpHandler ftpHandler) throws Exception {
        ftpHandler.renameFile(
                new String[]{"foobar", "from"}, "foo.txt",
                new String[]{"foobar", "to"}, "bar.txt"
        );
    }
}
```

#### 移动

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class MoveFileSnippet {

    public void move(FtpHandler ftpHandler) throws Exception {
        ftpHandler.moveFile(
                new String[]{"foobar", "from"}, "foo.txt",
                new String[]{"foobar", "to"}, "foo.txt"
        );
    }
}
```

#### 复制

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

public class CopyFileSnippet {

    public void copy(FtpHandler ftpHandler) throws Exception {
        ftpHandler.copyFile(
                new String[]{"foobar", "from"}, "foo.bin",
                new String[]{"foobar", "to"}, "foo-copy.bin"
        );
    }
}
```

`copyFile` 的实现会先读取源文件，再写入目标文件。对于较大文件，组件会将超出内存缓冲区的数据落到临时文件，避免一次性占满堆内存。

#### 描述文件

```java
package com.example.foobar;

import com.dwarfeng.ftp.bean.dto.FtpFile;
import com.dwarfeng.ftp.handler.FtpHandler;

public class DescFileSnippet {

    public void printDesc(FtpHandler ftpHandler) throws Exception {
        FtpFile foo = ftpHandler.descFile(new String[]{"foobar", "docs"}, "bar.txt");
        if (foo != null) {
            System.out.println(foo.getName());
            System.out.println(foo.getType());
            System.out.println(foo.getSize());
        }
    }
}
```

### DTO 与常量

`listFiles` / `descFile` 返回 `FtpFile`，包含字段：

- `name`：文件名。
- `type`：文件类型。
- `size`：文件大小。

文件类型可通过 `Constants` 判断：

```java
package com.example.foobar;

import com.dwarfeng.ftp.util.Constants;

public class FtpFileTypeSnippet {

    public static String typeToString(int type) {
        if (type == Constants.FTP_FILE_TYPE_FILE) {
            return "file";
        }
        if (type == Constants.FTP_FILE_TYPE_DIRECTORY) {
            return "directory";
        }
        if (type == Constants.FTP_FILE_TYPE_SYMBOLIC_LINK) {
            return "symbolic link";
        }
        return "unknown";
    }
}
```

## 路径模型与 FtpFileLocation

### 数组路径模型

最常用写法是 `String[] filePaths + String fileName`：

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.nio.charset.StandardCharsets;

public class ArrayPathSnippet {

    public void store(FtpHandler ftpHandler) throws Exception {
        String[] fooPath = new String[]{"foobar", "a", "b", "c"};
        ftpHandler.storeFile(fooPath, "bar.txt", "baz".getBytes(StandardCharsets.UTF_8));
    }
}
```

语义是：`/foobar/a/b/c/bar.txt`。

### 对象路径模型

也可使用 `FtpFileLocation` 封装位置：

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.ftp.struct.FtpFileLocation;

import java.nio.charset.StandardCharsets;

public class ObjectPathSnippet {

    public void store(FtpHandler ftpHandler) throws Exception {
        FtpFileLocation foo = new FtpFileLocation(new String[]{"foobar", "a", "b"}, "bar.txt");
        ftpHandler.storeFile(foo, "baz".getBytes(StandardCharsets.UTF_8));
    }
}
```

对于目录操作（如 `removeDirectory` / `clearDirectory`），`fileName` 会被忽略。

### 示例：`FooPathResolver`

在业务系统中，通常会先定义根路径，再拼接相对路径。

```java
package com.example.foobar;

public class FooPathResolver {

    private final String[] rootPath;

    public FooPathResolver(String[] rootPath) {
        this.rootPath = rootPath;
    }

    public String[] resolve(String[] relativePath) {
        String[] result = new String[rootPath.length + relativePath.length];
        System.arraycopy(rootPath, 0, result, 0, rootPath.length);
        System.arraycopy(relativePath, 0, result, rootPath.length, relativePath.length);
        return result;
    }
}
```

配合使用：

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;

import java.nio.charset.StandardCharsets;

public class PathResolverUsageSnippet {

    private final FooPathResolver fooPathResolver;
    private final FtpHandler ftpHandler;

    public PathResolverUsageSnippet(FooPathResolver fooPathResolver, FtpHandler ftpHandler) {
        this.fooPathResolver = fooPathResolver;
        this.ftpHandler = ftpHandler;
    }

    public void save() throws Exception {
        String[] bar = fooPathResolver.resolve(new String[]{"files"});
        ftpHandler.storeFile(bar, "baz.txt", "hello".getBytes(StandardCharsets.UTF_8));
    }
}
```

### 使用建议

- 团队内部统一一种路径表达方式，减少维护成本。
- 业务层建议集中封装路径解析，不要在多个类中硬编码目录数组。
- 若方法参数较多，优先使用 `FtpFileLocation` 提升可读性。

## 线程安全与生命周期

### 生命周期

`FtpHandler` 实现了 `StartableHandler`，核心生命周期如下：

1. `start()`：初始化 `FTPClient`，连接登录，设置数据模式，启动 NOOP 任务。
2. `stop()`：停止 NOOP 任务，登出并断开连接。
3. `isStarted()`：返回当前启动状态。

在 Spring 中推荐通过 `init-method="start"` 与 `destroy-method="stop"` 交由容器托管。

### 自动保活与断线重连

运行过程中，处理器会定期发送 `NOOP` 维持连接。

当 `NOOP` 或业务调用发现连接异常时，会尝试自动重连。

业务调用前还会执行连接状态检查；若连接断开，将即时触发重连流程。

### 线程安全模型

处理器内部使用 `ReentrantLock` 保证线程安全。

- 普通方法（如 `storeFile`、`retrieveFile`、`listFiles`）在方法结束时释放锁。
- `openInputStream` / `openOutputStream` 在返回流后不会立即释放锁。
- 只有当返回流被关闭后，锁才会释放。

这意味着：流未关闭期间，其它线程对同一 `FtpHandler` 的任何调用都将阻塞。

### 流式操作约束

对于 `openInputStream` / `openOutputStream`，请遵循以下约束：

1. 获取流后立即消费，不要长时间持有。
2. 必须在 `finally` 或 `try-with-resources` 中关闭。
3. 流未关闭前，不要在任何线程调用该处理器的其它方法。
4. 流对象只应在创建它的线程中使用。

不遵守上述约束，可能导致线程长时间等待、连接状态异常或传输失败。

### 异常处理建议

业务侧建议统一捕获 `HandlerException` 并映射为业务异常。

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.subgrade.stack.exception.HandlerException;

public class FoobarFacade {

    private final FtpHandler ftpHandler;

    public FoobarFacade(FtpHandler ftpHandler) {
        this.ftpHandler = ftpHandler;
    }

    public void save(byte[] data) {
        try {
            ftpHandler.storeFile(new String[]{"foobar", "facade"}, "foo.bin", data);
        } catch (HandlerException e) {
            throw new IllegalStateException("保存文件失败", e);
        }
    }
}
```

## 最佳实践

### 路径规划与命名规范

- 为每个业务域规划独立根目录，例如 `foobar-a`、`foobar-b`。
- 目录命名优先使用稳定英文标识，避免频繁变更。
- 文件名建议使用稳定主键或哈希，避免覆盖冲突。

### 小文件与大文件策略

- 小文件优先 `storeFile` / `retrieveFile`（字节数组直读写）。
- 大文件优先流式 API，避免高峰期堆内存抖动。
- 超大文件复制时，结合 `file_copy_memory_buffer_size` 与临时目录进行容量规划。

### 数据连接模式与超时建议

- 对互联网环境下常见场景，优先考虑被动模式（`2`）。
- 使用本地主动模式（`0`）时，建议设置 `ftp.data_timeout > 0`。
- 对高延迟网络，适当提高 `connect_timeout`，并保持 `noop_interval < connect_timeout`。

### 目录删除策略

- 目录非空时，先 `clearDirectory`，再 `removeDirectory`。
- 不要直接尝试删除业务根目录。
- 批量删除建议分批执行并打日志，便于失败恢复。

### 流式传输策略

- 强制使用 `try-with-resources`。
- 不要把 `openInputStream` / `openOutputStream` 返回对象跨线程传递。
- 一次传输完成后尽快关闭，减少处理器锁持有时间。

### 幂等与补偿

- 覆盖写场景，优先固定文件名，简化重试逻辑。
- 对关键删除操作可先 `existsFile` 再删除。
- 对批处理任务记录“已成功项”，异常后可续跑。

### 安全建议

- 生产环境通过外部配置注入账号密码，不要硬编码。
- 最小权限原则：FTP 账号只开放必要目录与读写权限。
- 避免在日志打印明文密码与敏感路径。

### 代码组织建议

推荐将 FTP 访问逻辑收敛到独立组件，避免在业务代码中到处出现路径数组与底层调用。

```java
package com.example.foobar;

import com.dwarfeng.ftp.handler.FtpHandler;
import com.dwarfeng.subgrade.stack.exception.HandlerException;

import java.nio.charset.StandardCharsets;

public class FoobarFileGateway {

    private static final String[] RELATIVE_CONFIG = new String[]{"config"};

    private final FtpHandler ftpHandler;
    private final FooPathResolver fooPathResolver;

    public FoobarFileGateway(FtpHandler ftpHandler, FooPathResolver fooPathResolver) {
        this.ftpHandler = ftpHandler;
        this.fooPathResolver = fooPathResolver;
    }

    public void saveConfig(String fileName, String json) throws HandlerException {
        String[] path = fooPathResolver.resolve(RELATIVE_CONFIG);
        ftpHandler.storeFile(path, fileName, json.getBytes(StandardCharsets.UTF_8));
    }

    public String loadConfig(String fileName) throws HandlerException {
        String[] path = fooPathResolver.resolve(RELATIVE_CONFIG);
        byte[] bytes = ftpHandler.retrieveFile(path, fileName);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
```

这种结构有三个好处：

1. 路径管理集中化，目录变更只改一处。
2. 异常映射集中化，便于统一错误码治理。
3. 便于在网关层补充审计、限流和重试策略。

### 监控与运维检查清单

建议在生产环境维护如下检查项：

1. FTP 服务可达性（主机、端口、登录）探针。
2. 周期上传/下载探针（验证业务目录权限）。
3. 临时目录容量告警（避免 `copyFile` 期间磁盘打满）。
4. 传输超时告警（关注 `data_timeout` 与网络波动）。
5. 失败重试次数与失败类型分布（便于定位网络/权限/目录问题）。

### 发布前检查清单

上线前建议逐项确认：

1. `ftp.*` 参数是否与目标环境一致。
2. `scheduler` 是否已注册且线程池参数满足负载。
3. 所有流式代码是否使用了 `try-with-resources`。
4. 目录删除逻辑是否执行了“先清空后删除”。
5. 是否对外层接口做了可观察性埋点（日志、指标、告警）。

## 常见问题

### 无法连接到 FTP 服务

排查顺序建议：

1. `ftp.host`、`ftp.port` 是否可达。
2. 用户名密码是否正确。
3. 服务端是否开放被动端口或允许当前连接模式。
4. 防火墙/NAT 是否拦截。

### 文件传输时出现阻塞

常见原因：

1. 本地主动模式下，服务端未回连，且 `data_timeout` 未设置为正数。
2. 某个线程打开了流但未关闭，导致处理器锁长期占用。

建议：

- 在模式 `0` 下设置 `ftp.data_timeout=3000` 或更高。
- 全面检查是否遗漏 `close()`。

### 中文目录或文件名乱码

通常是字符集不匹配。

请将 `ftp.server_charset` 设置为服务端实际编码，并保持客户端与服务端一致。

### 删除目录失败

FTP 协议只能删除空目录。处理流程应为：

1. `clearDirectory(path)`。
2. `removeDirectory(path)`。

### `copyFile` 执行较慢

这是正常现象。`copyFile` 并非 FTP 标准原子命令，底层是“读取源 -> 写入目标”。

优化建议：

- 避免在高峰期批量复制大文件。
- 适当增大 `ftp.file_copy_memory_buffer_size`。
- 将临时目录放在 I/O 性能更好的磁盘。

### `descFile` 返回 `null`

`descFile` 返回 `null` 代表目标不存在，这是正常语义。

如果需要异常语义，请在业务层自行转换：当返回 `null` 时抛业务异常。

### 为什么流式方法会阻塞其它调用

这是线程安全设计使然：`openInputStream` / `openOutputStream` 打开后，处理器会保持锁直到流关闭。

如果希望并发流式传输，请使用多个 `FtpHandler` 实例隔离。

### 本地配置与 classpath 配置冲突

当使用 `property-placeholder` 且开启本地覆盖时，请确认加载顺序与 `local-override` 配置。

建议统一规定环境配置目录，减少多源覆盖引起的歧义。

## 参阅

- [Quick Start](./QuickStart.md) - 快速开始，用最快的方式体验本项目。
- [Config Parameters](./ConfigParameters.md) - 配置参数详解，说明 FtpConfig 各配置项含义、默认值与校验规则。
- [Data Connect Modes](./DataConnectModes.md) - 数据连接模式，详细介绍 FTP 四种数据连接模式的区别与使用场景。
- [Extra Features](./ExtraFeatures.md) - 额外功能详解，说明自动连接管理、流式操作与高级文件能力。
- [Use with Maven](./UseWithMaven.md) - 通过 Maven 使用本项目。
