# ChangeLog

## Release_1.3.4_20250803_build_A

### 功能构建

- 依赖升级。
  - 升级 `commons-lang3` 依赖版本为 `3.18.0` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.5.11.a` 以规避漏洞。

- README.md 文档内容优化。
  - 将 `单例模式` 节改名为 `单实例模式`，以与其所在章下其它节命名统一。
  - 优化 `任意数量的实例模式` 中的文档描述。

- 优化部分 Configuration 类中的常量命名。
  - com.dwarfeng.ftp.configuration.SingletonConfiguration。

### Bug 修复

- README.md 文档内容修复。
  - 修复 `单例模式` 中不正确的文档，包括不正确的配置示例。
  - 修复 `多实例模式` 中不正确的文档，包括不正确的配置示例。

### 功能移除

- (无)

---

## Release_1.3.3_20250601_build_A

### 功能构建

- 优化 `src/test` 下的部分示例的控制台输出文案。
  - com.dwarfeng.ftp.example.ClearDirectoryExample。
  - com.dwarfeng.ftp.example.CopyFileExample。
  - com.dwarfeng.ftp.example.DescFileExample。
  - com.dwarfeng.ftp.example.ListFileExample。
  - com.dwarfeng.ftp.example.MoveFileExample。
  - com.dwarfeng.ftp.example.ProcessExample。
  - com.dwarfeng.ftp.example.RenameFileExample。
  - com.dwarfeng.ftp.example.StreamExample。

- Wiki 编写。
  - docs/wiki/zh_CN/HowToInstallVsftpdOnCentos7.md。
  - docs/wiki/zh_CN/QuickStart.md。

- 更新 README.md。

- Wiki 更新。
  - docs/wiki/zh_CN/Introduction.md。
  - docs/wiki/zh_CN/Contents.md。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.5.10.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.3.2_20250504_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh_CN/VersionBlacklist.md。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.5.9.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.3.1_20250428_build_A

### 功能构建

- 优化 `connection.properties` 注释。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.3.0_20250415_build_A

### 功能构建

- Wiki 编写。
  - docs/wiki/zh_CN/DataConnectModes.md。

- 新增 `FtpConfig` 配置项。
  - 新增 `FtpConfig.dataConnectionMode`，以用于指定数据连接模式。
  - 新增 `FtpConfig.dataTimeout`，以用于指定数据超时时间。
  - 新增 `FtpConfig.activeRemoteDataConnectionModeServerHost`，以用于指定远程主动数据连接模式下的服务主机地址。
  - 新增 `FtpConfig.activeRemoteDataConnectionModeServerPort`，以用于指定远程主动数据连接模式下的服务端口。

### Bug 修复

- (无)

### 功能移除

- 去除 `FtpHandlerImpl` 中未生效的代码。

---

## Release_1.2.4_20250324_build_A

### 功能构建

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.5.8.a` 以规避漏洞。

- 更新 README.md。

- Wiki 编写。
  - 构建 wiki 目录结构。
  - docs/wiki/en_US/Contents.md。
  - docs/wiki/en_US/Introduction.md。
  - docs/wiki/zh_CN/Contents.md。
  - docs/wiki/zh_CN/Introduction.md。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.2.3_20241117_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.39` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.5.7.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.2.2_20240730_build_A

### 功能构建

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.37` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.5.5.a` 以规避漏洞。

- 优化 `README.md` 中的内容。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.2.1_20240514_build_A

### 功能构建

- 优化部分示例代码。
  - com.dwarfeng.ftp.example.DescFileExample。

- 优化部分类的文档注释。
  - com.dwarfeng.ftp.handler.FtpHandler。

### Bug 修复

- 修复 FtpHandlerImpl 中的 bug。
  - 修复描述文件时，当文件不存在时抛出异常的 bug。

### 功能移除

- (无)

---

## Release_1.2.0_20240506_build_A

### 功能构建

- 优化 `FtpHandlerImpl` 中方法的行为分析注解。
  - 去除部分方法上不必要的 `@SkipRecord` 注解。

- 优化 `README.md` 中的内容。

- 增加异常类型及其相关判断逻辑。
  - com.dwarfeng.ftp.exception.FtpHandlerStoppedException。

- `FtpConfig` 结构优化。
  - 优化 `FtpConfig` 构造器方法，由 `FtpConfig.Builder` 构造生成时可避免重复的参数校验。
  - 优化 `FtpConfig.Builder` 的参数校验方式，在 build 方法中统一校验参数，避免参数设置顺序改变造成参数校验不通过。

- 优化 `FtpHandlerImpl` 中部分方法抛出的异常类型。
  - FtpHandlerImpl.openInputStream(java.lang.String[], java.lang.String)。
  - FtpHandlerImpl.openInputStream(com.dwarfeng.ftp.struct.FtpFileLocation)。
  - FtpHandlerImpl.openOutputStream(java.lang.String[], java.lang.String)。
  - FtpHandlerImpl.openOutputStream(com.dwarfeng.ftp.struct.FtpFileLocation)。

- `FtpHandler` 增加新方法。
  - FtpHandler.clearDirectory。
  - FtpHandler.copyFile。
  - FtpHandler.descFile。
  - FtpHandler.moveFile。

- 优化部分类的文档注释。
  - com.dwarfeng.ftp.handler.FtpHandler。

- 日志功能优化。
  - 优化默认日志配置，默认配置仅向控制台输出 `INFO` 级别的日志。
  - 优化日志配置结构，提供 `conf/logging/settings.xml` 配置文件及其不同平台的参考配置文件，以供用户自定义日志配置。

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.31` 以规避漏洞。
  - 升级 `slf4j` 依赖版本为 `1.7.36` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.5.3.a` 以规避漏洞。

### Bug 修复

- 修复 FtpHandlerImpl 中的 bug。
  - 修复连接部分 FTP 服务器时，调用 `existsFile` 方法，当目标文件不存在时抛出异常的 bug。

### 功能移除

- (无)

---

## Release_1.1.13_20240102_build_A

### 功能构建

- 优化 FtpHandlerImpl 中的部分代码。
  - 优化 `FtpHandlerImpl.openInputStream` 方法获取的输出流的关闭时的异常处理逻辑。
  - 优化 `FtpHandlerImpl.openOutputStream` 方法获取的输出流的关闭时的异常处理逻辑。

- 优化空值判断方法，使用 `Objects.isNull` 替代部分 `xxx == null`。
  - com.dwarfeng.ftp.util.FtpConfigUtil。
  - com.dwarfeng.ftp.util.FtpFileLocationUtil。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.12_20231228_build_A

### 功能构建

- 优化测试配置。
  - spring/application-context-scan.xml。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.4.7.a` 并解决兼容性问题，以应用其新功能。
  - 升级 `spring` 依赖版本为 `5.3.31` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.11_20231029_build_A

### 功能构建

- 优化流方法。
  - 优化 FtpHandlerImpl.openInputStream 方法返回的输入流，增强其不规范使用时的处理能力。
  - 优化 FtpHandlerImpl.openInputStream 方法返回的输入流的关闭逻辑，并细化其日志输出。
  - 优化 FtpHandlerImpl.openOutputStream 方法返回的输出流，增强其不规范使用时的处理能力。
  - 优化 FtpHandlerImpl.openOutputStream 方法返回的输出流的关闭逻辑，并细化其日志输出。

- 过期方法。
  - com.dwarfeng.ftp.handler.FtpHandler.connect。
  - com.dwarfeng.ftp.handler.FtpHandler.disconnect。

### Bug 修复

- 修复流操作相关 bug。
  - 修复 FtpHandlerImpl.openInputStream 方法抛出异常时，同步锁不会释放的 bug。
  - 修复 FtpHandlerImpl.openOutputStream 方法抛出异常时，同步锁不会释放的 bug。

### 功能移除

- (无)

---

## Release_1.1.10_20230604_build_A

### 功能构建

- 优化 `FtpHandler` 中的方法。
  - 在所有文件操作方法中，增加了一个使用 `FtpFileLocation` 作为参数的对位方法。
  - 在此之前，文件操作方法中的参数有 `filePaths` 和 `fileName` 两个，这两个参数的组合可以定位一个文件，
    其它框架引用时需要分别传入，造成不便。
  - 添加对位方法后，其它框架引用时只需要传入一个 `FtpFileLocation` 参数即可，同时，旧方法仍然保留。

- 增加 `FtpFileLocation` 结构体，以便用一个参数定位一个文件/文件夹。
  - `FtpFileLocation` 包含了 `filePaths` 和 `fileName` 两个字段。
  - 该实体用于简化 `FtpHandler` 中的方法的入口参数。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.9_20230602_build_A

### 功能构建

- 优化 FTP 处理器实现。
  - 大幅优化了 `FtpHandlerImpl.openOutputStream` 方法获取的输出流的性能。
  - 优化 `FtpHandlerImpl.openInputStream` 方法的线程安全性。
  - 优化 `FtpHandlerImpl.openOutputStream` 方法的线程安全性。

- 优化 FTP 处理器的配置机制。
  - 提供了 `FtpConfig` 的构造器类 `FtpConfig.Builder`，类中包含了部分参数的默认值，使得 `FtpConfig` 更容易构造。
  - 配置检查被提前到了 `FtpConfig` 和 `FtpConfig.Builder` 中，使得异常的配置能够更早地被发现。
  - 优化了 `SingletonConfiguration` 字段的 `@Value` 注解，使得 `placeholder` 不存在时能够以默认值替代。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.8_20230530_build_A

### 功能构建

- 优化示例代码。
  - 示例代码演示结束后，不仅删除文件，还删除文件夹。

- 规范 `FtpHandler` 中的方法名称，过时部分命名不合理的方法。

- `FtpHandler` 增加新方法。
  - FtpHandler.renameFile。

- 使用 `FtpConfig` 结构体代替多项配置，简化构造器方法。

- 优化 FtpHandlerImpl 中的方法，使其更加合理。
  - Ftp 服务器参数异常时，在构造器中抛出异常，而不是在方法中抛出异常。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.7_20230528_build_A

### 功能构建

- 优化 `StreamExample` 中的代码，使其更加合理。

- 增加 `ListFileExample`，用于演示列出文件的功能。

- `FtpHandlerImpl` 中增加 `bufferSize` 属性，用于指定缓冲区大小。
  - 该属性默认值为 `4096`。
  - FtpHandlerImpl.storeFileByStream 方法中，使用该属性指定输入流的缓冲区大小。
  - FtpHandlerImpl.retrieveFileByStream 方法中，使用该属性指定输出流的缓冲区大小。

### Bug 修复

- 修复了在某些情况下，`FtpHandler` 写入非 ASCII 文件名时，文件名编码不正确的问题。

### 功能移除

- (无)

---

## Release_1.1.6_20230527_build_A

### 功能构建

- 规范 `FtpHandler` 中的方法名称，过时部分命名不合理的方法。

- 优化 `FtpHandlerImpl` 中的方法，使其更加合理。
  - Ftp 服务器状态异常时，调用 `FtpHandler` 中的方法可以更快的抛出异常。

- `FtpHandler` 增加新方法。
  - FtpHandler.openInputStream。
  - FtpHandler.openOutputStream。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.5_20230421_build_A

### 功能构建

- 代码优化。
  - 为部分代码增加 @SuppressWarnings 注解，以消除不合理的警报。
  - 去除部方法定义中不会抛出的异常声明。

- FtpHandler 继承了 StartableHandler 接口。

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.27` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.3.3.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.4_20230203_build_A

### 功能构建

- 增加列出指定路径下所有文件的功能。

- 依赖升级。
  - 升级 `commons-net` 依赖版本为 `3.9.0`。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.3_20221212_build_A

### 功能构建

- 为了实现短生命周期（多例）使用，暴露生命周期方法。
  - com.dwarfeng.ftp.handler.FtpHandler.connect。
  - com.dwarfeng.ftp.handler.FtpHandler.disconnect。

- 增加了以流的形式操作文件的方法。
  - com.dwarfeng.ftp.handler.FtpHandler.storeFileByStream。
  - com.dwarfeng.ftp.handler.FtpHandler.getFileContentByStream。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.3.0.a`。

### Bug 修复

- (无)

### 功能移除

- 删除不需要的依赖。
  - 删除 `dutil` 依赖。

---

## Release_1.1.2_20221118_build_A

### 功能构建

- 依赖升级。
  - 升级 `commons-lang3` 依赖版本为 `3.12.0`。
  - 升级 `commons-net` 依赖版本为 `3.8.0`。
  - 升级 `subgrade` 依赖版本为 `1.2.14.a`。

- 优化 `README.md` 的格式。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.1_20220921_build_A

### 功能构建

- FtpHandlerImpl 增加行为分析注解。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.1.0_20220917_build_A

### 功能构建

- 改进 `FtpHandlerImpl` 结构，使其支持多实例。

- 增加单例配置，并解决兼容问题。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.2_20220912_build_A

### 功能构建

- 插件升级。
  - 升级 `maven-deploy-plugin` 插件版本为 `2.8.2`。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.2.10.a`。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.1_20220703_build_A

### 功能构建

- 依赖升级。
  - 升级 `junit` 依赖版本为 `4.13.2` 以规避漏洞。
  - 升级 `spring` 依赖版本为 `5.3.20` 以规避漏洞。
  - 升级 `log4j2` 依赖版本为 `2.17.2` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.2.8.a` 以规避漏洞。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## Release_1.0.0_20220127_build_A

### 功能构建

- 项目结构建立，程序清理测试通过。

- 实现了 FtpHandler 核心机制，并编写示例代码。

### Bug 修复

- (无)

### 功能移除

- (无)
