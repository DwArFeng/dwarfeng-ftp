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

运行 `src/test` 下的示例以观察全部特性。

| 示例类名                                           | 说明     |
|------------------------------------------------|--------|
| com.dwarfeng.ftp.example.ProcessExample        | 流程示例   |
| com.dwarfeng.ftp.example.StreamExample         | 流的使用示例 |
| com.dwarfeng.ftp.example.ListFileExample       | 列出文件示例 |
| com.dwarfeng.ftp.example.RenameFileExample     | 列出文件示例 |
| com.dwarfeng.ftp.example.ClearDirectoryExample | 清空目录示例 |
| com.dwarfeng.ftp.example.CopyFileExample       | 复制文件示例 |
| com.dwarfeng.ftp.example.DescFileExample       | 描述文件示例 |

## 文档

该项目的文档位于 [docs](./docs) 目录下，包括：

### wiki

wiki 为项目的开发人员为本项目编写的详细文档，包含不同语言的版本，主要入口为：

1. [简介](./docs/wiki/zh_CN/Introduction.md) - 镜像的 `README.md`，与本文件内容基本相同。
2. [目录](./docs/wiki/zh_CN/Contents.md) - 文档目录。

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

1. 运行 `src/test` 下的 `Example` 以观察全部特性。
2. 观察项目结构，将其中的配置运用到其它的 subgrade 项目中。

### 单实例模式

加载 `com.dwarfeng.ftp.configuration.SingletonConfiguration`，即可获得单例模式的 `FtpHandler`。  
在项目的 `application-context-scan.xml` 中追加 `com.dwarfeng.ftp.configuration` 包中相应 bean 的扫描，示例如下:

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

    <!--扫描 dwarfeng-ftp 的包。 -->
    <context:component-scan base-package="com.dwarfeng.ftp.configuration" use-default-filters="false">
        <context:include-filter type="assignable" expression="com.dwarfeng.ftp.configuration.SingletonConfiguration"/>
    </context:component-scan>
</beans>
```

或者只扫描 `com.dwarfeng.ftp.configuration` 包中的 `SingletonConfiguration`，示例如下:

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

    <!-- 扫描 configuration 包中的 SingletonConfiguration -->
    <context:component-scan base-package="com.dwarfeng.ftp.configuration" use-default-filters="false">
        <context:include-filter
                type="assignable"
                expression="com.dwarfeng.ftp.configuration.SingletonConfiguration"
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
    <!-- 第 1 个实例 -->
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
    </bean>
    <bean name="config1" factory-bean="configBuilder1" factory-method="build"/>
    <bean name="instance1" class="com.dwarfeng.ftp.handler.FtpHandlerImpl" init-method="start" destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config1"/>
    </bean>

    <!-- 第 2 个实例 -->
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
    </bean>
    <bean name="config2" factory-bean="configBuilder2" factory-method="build"/>
    <bean name="instance2" class="com.dwarfeng.ftp.handler.FtpHandlerImpl" init-method="start" destroy-method="stop">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="config" ref="config2"/>
    </bean>
</beans>
```

### 任意数量的实例模式

自行设计 `FtpHandler` 的工厂类，调用相关工厂方法生成 `FtpHandlerImpl` 实例。

需要注意的是：使用者需要自行管理 `FtpHandlerImpl` 实例的生命周期，包括在适合的时机调用 `start` 和 `stop` 方法。
