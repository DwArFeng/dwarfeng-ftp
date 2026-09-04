# ChangeLog

## Release_3.0.1_20260831_build_A

### 功能构建

- `dwarfeng-ftp-core` 子模块类优化注释、文档注释格式、代码换行格式。
  - com.dwarfeng.ftp.stack.handler.FtpHandler。
  - com.dwarfeng.ftp.node.configuration.ServiceExceptionMapperConfiguration。

- 优化部分类的代码语法。
  - com.dwarfeng.ftp.api.integration.springtelqos.FtpCommand。
  - com.dwarfeng.ftp.impl.handler.FtpHandlerImpl。
  - com.dwarfeng.ftp.impl.handler.FtpQosHandlerImpl。
  - com.dwarfeng.ftp.stack.util.FtpConfigUtil。
  - com.dwarfeng.ftp.node.example.ProcessExample。

### Bug 修复

- 修复部分类中静态注解不正确的 bug。
  - com.dwarfeng.ftp.api.integration.springtelqos.FtpCommand。
  - com.dwarfeng.ftp.impl.handler.FtpHandlerImpl。
  - com.dwarfeng.ftp.impl.handler.FtpQosHandlerImpl。
  - com.dwarfeng.ftp.impl.service.FtpQosServiceImpl。
  - com.dwarfeng.ftp.node.configuration.FtpConfigDefinitionParser。
  - com.dwarfeng.ftp.node.configuration.FtpHandlerDefinitionParser。
  - com.dwarfeng.ftp.node.configuration.FtpQosDefinitionParser。
  - com.dwarfeng.ftp.sdk.util.FtpExceptionHelper。
  - com.dwarfeng.ftp.sdk.util.FtpFileUtil。
  - com.dwarfeng.ftp.sdk.util.FtpQosExceptionHelper。
  - com.dwarfeng.ftp.stack.handler.FtpHandler。
  - com.dwarfeng.ftp.stack.handler.FtpQosHandler。
  - com.dwarfeng.ftp.stack.service.FtpQosService。
  - com.dwarfeng.ftp.stack.struct.FtpFileLocation。

### 功能移除

- 移除 `dwarfeng-ftp-core` 模块中不需要的 JPMS 依赖。
  - `requires spring.core;`。

---

## Release_3.0.0_20260827_build_A

### 功能构建

- 更新 README.md。

- Wiki 更新。
  - docs/wiki/zh-CN/ExtraFeatures.md。
  - docs/wiki/zh-CN/Introduction.md。
  - docs/wiki/zh-CN/QuickStart.md。
  - docs/wiki/zh-CN/Troubleshooting.md。
  - docs/wiki/zh-CN/UsageGuide.md。

- 升级 JDK 版本至 25。

### Bug 修复

- (无)

### 功能移除

- (无)

---

## 更早的版本

[View all changelogs](./changelogs)
