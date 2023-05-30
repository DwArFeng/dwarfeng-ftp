# ChangeLog

### Release_1.1.8_20230530_build_A

#### 功能构建

- 优化实例代码。
  - 实例代码演示结束后，不仅删除文件，还删除文件夹。

- 规范 `FtpHandler` 中的方法名称，过时部分命名不合理的方法。

- `FtpHandler` 增加新方法。
  - FtpHandler.renameFile。

- 使用 `FtpConfig` 结构体代替多项配置，简化构造器方法。

- 优化 FtpHandlerImpl 中的方法，使其更加合理。
  - Ftp 服务器参数异常时，在构造器中抛出异常，而不是在方法中抛出异常。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.7_20230528_build_A

#### 功能构建

- 优化 `StreamExample` 中的代码，使其更加合理。

- 增加 `ListFileExample`，用于演示列出文件的功能。

- `FtpHandlerImpl` 中增加 `bufferSize` 属性，用于指定缓冲区大小。
  - 该属性默认值为 `4096`。
  - FtpHandlerImpl.storeFileByStream 方法中，使用该属性指定输入流的缓冲区大小。
  - FtpHandlerImpl.retrieveFileByStream 方法中，使用该属性指定输出流的缓冲区大小。

#### Bug修复

- 修复了在某些情况下，`FtpHandler` 写入非 ASCII 文件名时，文件名编码不正确的问题。

#### 功能移除

- (无)

---

### Release_1.1.6_20230527_build_A

#### 功能构建

- 规范 `FtpHandler` 中的方法名称，过时部分命名不合理的方法。

- 优化 `FtpHandlerImpl` 中的方法，使其更加合理。
  - Ftp 服务器状态异常时，调用 `FtpHandler` 中的方法可以更快的抛出异常。

- `FtpHandler` 增加新方法。
  - FtpHandler.openInputStream。
  - FtpHandler.openOutputStream。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.5_20230421_build_A

#### 功能构建

- 代码优化。
  - 为部分代码增加 @SuppressWarnings 注解，以消除不合理的警报。
  - 去除部方法定义中不会抛出的异常声明。

- FtpHandler 继承了 StartableHandler 接口。

- 依赖升级。
  - 升级 `spring` 依赖版本为 `5.3.27` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.3.3.a` 以规避漏洞。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.4_20230203_build_A

#### 功能构建

- 增加列出指定路径下所有文件的功能。

- 依赖升级。
  - 升级 `commons-net` 依赖版本为 `3.9.0`。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.3_20221212_build_A

#### 功能构建

- 为了实现短生命周期（多例）使用，暴露生命周期方法。
  - com.dwarfeng.ftp.handler.FtpHandler.connect。
  - com.dwarfeng.ftp.handler.FtpHandler.disconnect。

- 增加了以流的形式操作文件的方法。
  - com.dwarfeng.ftp.handler.FtpHandler.storeFileByStream。
  - com.dwarfeng.ftp.handler.FtpHandler.getFileContentByStream。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.3.0.a`。

#### Bug修复

- (无)

#### 功能移除

- 删除不需要的依赖。
  - 删除 `dutil` 依赖。

---

### Release_1.1.2_20221118_build_A

#### 功能构建

- 依赖升级。
  - 升级 `commons-lang3` 依赖版本为 `3.12.0`。
  - 升级 `commons-net` 依赖版本为 `3.8.0`。
  - 升级 `subgrade` 依赖版本为 `1.2.14.a`。

- 优化 `README.md` 的格式。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.1_20220921_build_A

#### 功能构建

- FtpHandlerImpl 增加行为分析注解。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.1.0_20220917_build_A

#### 功能构建

- 改进 `FtpHandlerImpl` 结构，使其支持多实例。

- 增加单例配置，并解决兼容问题。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.2_20220912_build_A

#### 功能构建

- 插件升级。
  - 升级 `maven-deploy-plugin` 插件版本为 `2.8.2`。

- 依赖升级。
  - 升级 `subgrade` 依赖版本为 `1.2.10.a`。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.1_20220703_build_A

#### 功能构建

- 依赖升级。
  - 升级 `junit` 依赖版本为 `4.13.2` 以规避漏洞。
  - 升级 `spring` 依赖版本为 `5.3.20` 以规避漏洞。
  - 升级 `log4j2` 依赖版本为 `2.17.2` 以规避漏洞。
  - 升级 `subgrade` 依赖版本为 `1.2.8.a` 以规避漏洞。

#### Bug修复

- (无)

#### 功能移除

- (无)

---

### Release_1.0.0_20220127_build_A

#### 功能构建

- 项目结构建立，程序清理测试通过。

- 实现了 FtpHandler 核心机制，并编写示例代码。

#### Bug修复

- (无)

#### 功能移除

- (无)
