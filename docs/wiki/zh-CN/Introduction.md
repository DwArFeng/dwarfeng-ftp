# dwarfeng-ftp

Dwarfeng（赵扶风）的 FTP 服务，基于 `subgrade` 项目，在 `commons-io` 的基础上做了进一步封装，目前用于多个个人项目。

---

## 特性

1. Subgrade 架构支持。
2. 保存文件时创建不存在的目录。
3. 中文编码支持。
4. 自动保持连接。
5. 自动保持连接与断线重连。
6. 打开文件的输入流/输出流。
7. 文件重命名。
8. 清空目录。

运行 `dwarfeng-ftp-core/src/test` 下的示例以观察全部特性。

| 示例类名                                           | 说明      |
|------------------------------------------------|---------|
| com.dwarfeng.ftp.example.ProcessExample        | 流程示例    |
| com.dwarfeng.ftp.example.StreamExample         | 流的使用示例  |
| com.dwarfeng.ftp.example.ListFileExample       | 列出文件示例  |
| com.dwarfeng.ftp.example.RenameFileExample     | 重命名文件示例 |
| com.dwarfeng.ftp.example.MoveFileExample       | 移动文件示例  |
| com.dwarfeng.ftp.example.ClearDirectoryExample | 清空目录示例  |
| com.dwarfeng.ftp.example.CopyFileExample       | 复制文件示例  |
| com.dwarfeng.ftp.example.DescFileExample       | 描述文件示例  |

## 文档

该项目的文档位于 [docs](../../../docs) 目录下，包括：

### wiki

wiki 为项目的开发人员为本项目编写的详细文档，包含不同语言的版本，主要入口为：

1. [简介](./Introduction.md) - 即本文件。
2. [目录](./Contents.md) - 文档目录。

## 测试

该项目针对多个 FTP 服务端进行了测试，测试结果如下：

| FTP 服务器类型             | 测试结果 |
|-----------------------|------|
| vsftpd                | 通过   |
| Windows 10 内置 FTP 服务器 | 通过   |

## 安装说明

1. 下载源码。

   使用 git 进行源码下载。

   ```shell
   git clone git@github.com:DwArFeng/dwarfeng-ftp.git
   ```

   对于中国用户，可以使用 gitee 进行高速下载。

   ```shell
   git clone git@gitee.com:dwarfeng/dwarfeng-ftp.git
   ```

2. 项目安装。

   进入项目根目录，执行 maven 命令

   ```shell
   mvn clean source:jar install
   ```

3. 项目引入。

   在项目的 pom.xml 中添加如下依赖：

   ```xml
   <dependency>
       <groupId>com.dwarfeng</groupId>
       <artifactId>dwarfeng-ftp</artifactId>
       <version>${dwarfeng-ftp.version}</version>
   </dependency>
   ```

4. enjoy it.

## 如何使用

1. 运行 `dwarfeng-ftp-core/src/test/java/com/dwarfeng/ftp/example` 下的示例类以观察全部特性。
2. 观察项目结构，将其中的配置运用到其它的 subgrade 项目中。

### 单例模式

加载 `com.dwarfeng.ftp.node.configuration.SingletonConfiguration`，即可获得单例模式的 `FtpHandler`。  
在项目的 `application-context-scan.xml` 中追加 `com.dwarfeng.ftp.node.configuration` 包中全部 bean 的扫描，示例如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress SpringXmlModelInspection -->
<beans
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
>

    <!-- 扫描 configuration 包中的全部 bean。 -->
    <context:component-scan base-package="com.dwarfeng.ftp.node.configuration"/>
</beans>
```

或者只扫描 `com.dwarfeng.ftp.node.configuration` 包中的 `SingletonConfiguration`，示例如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress SpringXmlModelInspection -->
<beans
        xmlns:context="http://www.springframework.org/schema/context"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd"
>

    <!-- 扫描 configuration 包中的 SingletonConfiguration -->
    <context:component-scan base-package="com.dwarfeng.ftp.node.configuration" use-default-filters="false">
        <context:include-filter
                type="assignable"
                expression="com.dwarfeng.ftp.node.configuration.SingletonConfiguration"
        />
    </context:component-scan>
</beans>
```

### 多实例模式

不使用包扫描，使用 xml 或者配置类生成 `FtpHandlerImpl` 实例。  
在项目的 `bean-definition.xml` 中追加配置，示例如下:

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
    <bean name="configBuilder1" class="com.dwarfeng.ftp.stack.struct.FtpConfig.Builder">
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
    <bean name="instance1" class="com.dwarfeng.ftp.impl.handler.FtpHandlerImpl" init-method="start"
          destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config1"/>
    </bean>

    <!-- 第 2 个实例。 -->
    <bean name="configBuilder2" class="com.dwarfeng.ftp.stack.struct.FtpConfig.Builder">
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
    <bean name="instance2" class="com.dwarfeng.ftp.impl.handler.FtpHandlerImpl" init-method="start"
          destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config2"/>
    </bean>
</beans>
```

### XSD 配置

从 `2.0.0.a` 版本开始，可以使用 `dwarfeng-ftp` 命名空间装配 `FtpConfig`、`FtpHandler` 与 `FtpQosService`。  
在项目的 `application-context-ftp.xml` 中追加配置，示例如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 以下注释用于抑制 idea 中 .md 的警告，实际并无错误，在使用时可以连同本注释一起删除。 -->
<!--suppress SpringPlaceholdersInspection -->
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:ftp="http://dwarfeng.com/schema/dwarfeng-ftp"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://dwarfeng.com/schema/dwarfeng-ftp
        http://dwarfeng.com/schema/dwarfeng-ftp/dwarfeng-ftp.xsd"
>

    <ftp:config
            host="${ftp.host}"
            username="${ftp.username}"
            password="${ftp.password}"
            port="${ftp.port}"
            server-charset="${ftp.server_charset}"
            connect-timeout="${ftp.connect_timeout}"
            noop-interval="${ftp.noop_interval}"
            buffer-size="${ftp.buffer_size}"
            temporary-file-directory-path="${ftp.temporary_file_directory_path}"
            temporary-file-prefix="${ftp.temporary_file_prefix}"
            temporary-file-suffix="${ftp.temporary_file_suffix}"
            file-copy-memory-buffer-size="${ftp.file_copy_memory_buffer_size}"
            data-connection-mode="${ftp.data_connection_mode}"
            data-timeout="${ftp.data_timeout}"
            active-remote-data-connection-mode-server-host="${ftp.active_remote_data_connection_mode_server_host}"
            active-remote-data-connection-mode-server-port="${ftp.active_remote_data_connection_mode_server_port}"
    />
    <ftp:handler/>
    <ftp:qos/>
</beans>
```

### 任意数量的实例模式

自行设计 `FtpHandler` 的工厂类，调用相关工厂方法生成 `FtpHandlerImpl` 实例。
需要注意的是：生成的 `FtpHandlerImpl` 在使用之前需要调用 `FtpHandlerImpl#start()` 启动处理器；同时在使用完毕之后，
需要调用 `FtpHandlerImpl#stop()` 关闭处理器。
