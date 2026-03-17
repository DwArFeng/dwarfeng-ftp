# Config Parameters - 配置参数详解

本文档详细说明了 dwarfeng-ftp 的配置参数，包括各配置项的含义、默认值、校验规则以及配置示例。
配置参数通过 properties 文件或 Spring 占位符注入，最终构建为 `FtpConfig` 对象供 `FtpHandlerImpl` 使用。

## 配置加载方式

### 单例模式

使用 `SingletonConfiguration` 时，通过 Spring 的 `@Value` 注解从 properties 中读取配置。
需要确保 Spring 的 `property-placeholder` 已加载包含 `ftp.*` 前缀的配置文件。

配置文件通常位于 `classpath:ftp/*.properties` 或通过 `file:conf/xxx/*.properties` 指定。
本地文件可覆盖 classpath 中的配置。

### 多实例模式

使用 XML 或配置类手动创建多个 `FtpHandlerImpl` 实例时，通过占位符区分不同实例的配置。
例如：`${ftp.host.1}`、`${ftp.port.1}` 对应第一个实例，`${ftp.host.2}`、`${ftp.port.2}` 对应第二个实例。

## 基础连接参数

### ftp.host

FTP 服务器的主机名称或 IP 地址。类型：String，必填。

### ftp.port

FTP 服务器的端口号，取值范围 0 - 65535。类型：int，默认值：21。

### ftp.username

FTP 的登录用户名。类型：String，必填。

### ftp.password

FTP 的登录密码。可为空。类型：String。

### ftp.server_charset

FTP 服务端的字符集，用于编码文件名与路径，支持中文等。类型：String，默认值：UTF-8。

## 连接与超时参数

### ftp.connect_timeout

连接超时时间，单位毫秒。必须大于 1000。类型：int，默认值：5000。

### ftp.noop_interval

NOOP 指令发送间隔，单位毫秒。必须小于 ftp.connect_timeout。类型：long，默认值：4000。

### ftp.buffer_size

数据传输的缓冲区大小，单位字节。负数表示使用系统默认值。类型：int，默认值：4096。

## 文件操作参数

### ftp.temporary_file_directory_path

临时文件目录路径。用于文件复制等操作时缓存大文件。目录需存在或可创建，且具备读写权限。类型：String，默认值：java.io.tmpdir。

### ftp.temporary_file_prefix

临时文件的前缀，用于区分不同实例产生的临时文件。类型：String，默认值：ftp-。

### ftp.temporary_file_suffix

临时文件的后缀，用于规范文件的扩展名。类型：String，默认值：.tmp。

### ftp.file_copy_memory_buffer_size

文件复制内存缓冲区大小，单位字节。文件超过此值时，剩余部分写入临时文件。必须大于 0。类型：int，默认值：1048576。

## 数据连接模式参数

数据连接模式决定了 FTP 数据传输时建立连接的方式。四种模式的详细说明请参阅 [Data Connect Modes](./DataConnectModes.md)。

### ftp.data_connection_mode

数据连接模式。0 = 本地主动，1 = 远程主动，2 = 本地被动，3 = 远程被动。类型：int，默认值：0。

### ftp.data_timeout

数据连接超时时间，单位毫秒。小于等于 0 表示永不超时。本地主动模式（0）时建议设为大于 0，
以避免服务端故障或网络波动导致程序阻塞。类型：int，默认值：-1。

### ftp.active_remote_data_connection_mode_server_host

远程主动模式（1）下的服务主机地址。仅当 ftp.data_connection_mode=1 时必填。类型：String，默认值：null。

### ftp.active_remote_data_connection_mode_server_port

远程主动模式（1）下的服务端口。仅当 ftp.data_connection_mode=1 时必填，取值范围 0 - 65535。类型：int，默认值：-1。

## 配置示例

### 单例模式

在 `connection.properties` 中配置：

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
ftp.file_copy_memory_buffer_size=4096
# FTP 的数据连接模式。
ftp.data_connection_mode=0
# FTP 的数据超时时间。
ftp.data_timeout=-1
```

### 多实例模式

在 Spring XML 中为多个实例配置不同的占位符：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress SpringBeanConstructorArgInspection, SpringXmlModelInspection, SpringPlaceholdersInspection -->
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd"
>
    <!-- 第 1 个实例。 -->
    <bean name="configBuilder1" class="com.dwarfeng.ftp.struct.FtpConfig.Builder">
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
    <bean name="config1" factory-bean="configBuilder1" factory-method="build"/>
    <bean name="instance1" class="com.dwarfeng.ftp.handler.FtpHandlerImpl" init-method="start" destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config1"/>
    </bean>

    <!-- 第 2 个实例。 -->
    <bean name="configBuilder2" class="com.dwarfeng.ftp.struct.FtpConfig.Builder">
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
    <bean name="config2" factory-bean="configBuilder2" factory-method="build"/>
    <bean name="instance2" class="com.dwarfeng.ftp.handler.FtpHandlerImpl" init-method="start" destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config2"/>
    </bean>
</beans>
```

在 properties 中对应配置 `ftp.host.1`、`ftp.host.2` 等。

## 参数校验规则

配置在构建 `FtpConfig` 时会进行校验，校验逻辑由 `FtpConfigUtil` 实现。常见约束如下：

- 主机、用户名不能为 null。
- 端口、数据超时端口必须在 0 - 65535 之间。
- 连接超时必须大于 1000 毫秒。
- NOOP 间隔必须小于连接超时。
- 临时文件目录必须存在或可创建，且为目录且有读写权限。
- 文件复制内存缓冲区大小必须大于 0。
- 数据连接模式必须为 0、1、2、3 之一。
- 当数据连接模式为 1（远程主动）时，远程服务主机地址和端口必填且有效。

违反上述规则时，将抛出 `NullPointerException` 或 `IllegalArgumentException`。

## 参阅

- [Data Connect Modes](./DataConnectModes.md) - 数据连接模式，详细介绍了 FTP 四种数据连接模式的区别以及使用场景。
- [Quick Start](./QuickStart.md) - 快速开始，用最快的方式体验本项目。
- [Extra Features](./ExtraFeatures.md) - 额外功能详解，详细介绍了 dwarfeng-ftp 在标准 FTP 协议基础上提供的额外便利功能。
