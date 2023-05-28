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

运行 `src/test` 下的示例以观察全部特性。

| 示例类名            | 说明     |
|-----------------|--------|
| ProcessExample  | 流程示例   |
| StreamExample   | 流的使用示例 |
| ListFileExample | 列出文件示例 |

## 安装说明

1. 下载源码。  
   使用 git 进行源码下载。
   ```
   git clone git@github.com:DwArFeng/dwarfeng-ftp.git
   ```
   对于中国用户，可以使用 gitee 进行高速下载。
   ```
   git clone git@gitee.com:dwarfeng/dwarfeng-ftp.git
   ```

2. 项目安装。  
   进入项目根目录，执行 maven 命令
   ```
   mvn clean source:jar install
   ```

3. enjoy it

---

## 如何使用

1. 运行 `src/test` 下的 `Example` 以观察全部特性。
2. 观察项目结构，将其中的配置运用到其它的 subgrade 项目中。

### 单例模式

加载 `com.dwarfeng.ftp.configuration.SingletonConfiguration`，即可获得单例模式的 `FtpHandler`。  
在项目的 `application-context-scan.xml` 中追加包扫描，示例如下:

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

    <!-- 扫描 configuration 包。 -->
    <context:component-scan base-package="com.dwarfeng.ftp.configuration"/>
</beans>
```

### 多实例模式

不使用包扫描，使用 xml 或者配置类生成 `FtpHandlerImpl` 实例。  
在项目的 `bean-definition.xml` 中追加配置，示例如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.springframework.org/schema/beans"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd"
>

    <bean name="instance1" class="com.dwarfeng.ftp.handler.FtpHandlerImpl">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="ftpHost" value="${ftp.host.1}"/>
        <constructor-arg name="ftpPort" value="${ftp.port.1}"/>
        <constructor-arg name="ftpUserName" value="${ftp.username.1}"/>
        <constructor-arg name="ftpPassword" value="${ftp.password.1}"/>
        <constructor-arg name="serverCharset" value="${ftp.server_charset.1}"/>
        <constructor-arg name="connectTimeout" value="${ftp.connect_timeout.1}"/>
        <constructor-arg name="noopInterval" value="${ftp.noop_interval.1}"/>
    </bean>

    <bean name="instance2" class="com.dwarfeng.ftp.handler.FtpHandlerImpl">
        <constructor-arg name="scheduler" ref="scheduler"/>
        <constructor-arg name="ftpHost" value="${ftp.host.2}"/>
        <constructor-arg name="ftpPort" value="${ftp.port.2}"/>
        <constructor-arg name="ftpUserName" value="${ftp.username.2}"/>
        <constructor-arg name="ftpPassword" value="${ftp.password.2}"/>
        <constructor-arg name="serverCharset" value="${ftp.server_charset.2}"/>
        <constructor-arg name="connectTimeout" value="${ftp.connect_timeout.2}"/>
        <constructor-arg name="noopInterval" value="${ftp.noop_interval.2}"/>
    </bean>
</beans>
```

### 任意数量的实例模式

自行设计 `FtpHandler` 的工厂类，调用相关工厂方法生成 `FtpHandlerImpl` 实例。
需要注意的是：生成的 `FtpHandlerImpl` 在使用之前需要调用 `FtpHandlerImpl#start()` 启动处理器；同时在使用完毕之后，
需要调用 `FtpHandlerImpl#stop()` 关闭处理器。
