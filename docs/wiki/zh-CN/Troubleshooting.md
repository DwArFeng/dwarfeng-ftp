# Troubleshooting - 故障排查

## 综述

本文档面向运维与实施人员，目标是快速定位 `dwarfeng-ftp` 在接入和运行阶段的常见故障。

与开发向的异常处理说明不同，本文强调“现场可执行”的排查步骤，包括配置核对、网络检查、服务状态确认、日志定位与回滚思路。
对无法仅凭文档确认的行为，本文会明确标注“需实测或源码复核”。

## 排查准备

开始排查前，建议先收集以下信息，避免在定位过程中反复补资料：

| 信息项          | 示例                                                        | 用途              |
|--------------|-----------------------------------------------------------|-----------------|
| 目标地址         | `ftp.host=10.0.0.8`                                       | 判断连接目标是否正确      |
| 端口           | `ftp.port=21`                                             | 判断控制连接端口是否一致    |
| 账号信息         | `ftp.username` / `ftp.password`                           | 判断登录失败是否由凭据引起   |
| 字符集          | `ftp.server_charset=UTF-8`                                | 判断中文路径是否会乱码     |
| 连接参数         | `ftp.connect_timeout` / `ftp.noop_interval`               | 判断连接稳定性和重连行为    |
| 数据连接参数       | `ftp.data_connection_mode` / `ftp.data_timeout`           | 判断传输阻塞与超时根因     |
| 运行配置文件       | `ftp/connection.properties`、`application-context-ftp.xml` | 判断环境配置是否被正确加载   |
| 服务端信息        | vsftpd 运行状态、配置文件位置                                        | 判断服务端能力与策略      |
| chroot 与目录权限 | `chroot_local_user`、用户 home 目录权限、`ls -ld` 输出              | 判断是否触发服务端安全策略拒绝 |
| 网络环境         | 是否有 NAT、防火墙、ACL                                           | 判断数据通道是否被拦截     |
| 日志位置         | 应用日志、FTP 服务器日志                                            | 提取异常时序与失败命令     |
| 原生客户端记录      | 系统 `ftp` 客户端登录/切目录/上传结果                                   | 做应用外对照，缩小故障边界   |

建议至少准备两份配置对照：

1. 代码仓中的参考模板：`dwarfeng-ftp-core/src/test/resources/ftp/connection.properties`。
2. 当前环境的实际生效配置（例如 `conf/*.properties` 或容器注入参数）。

## 无法连接到 FTP 服务

### 典型现象

- 应用侧启动后无法完成 FTP 初始连接。
- 业务调用报 `FtpConnectException`（消息：`FTP connection failed`）。
- 日志中出现“FTP 连接失败，将会启用重连机制尝试重新连接”。

### 排查步骤

1. 核对 `ftp.host` 与 `ftp.port` 是否指向正确目标。
2. 在应用节点直接测试端口连通性（例如 `Test-NetConnection <host> -Port <port>`）。
3. 登录 FTP 服务器检查服务状态（例如 `systemctl status vsftpd`）。
4. 检查防火墙、云安全组、网络 ACL 是否放通控制端口（通常为 `21`）。
5. 核对 `ftp.connect_timeout` 是否明显过小（源码约束：必须大于 `1000`）。
6. 核对 `ftp.noop_interval < ftp.connect_timeout`，避免配置校验直接失败。

### 说明与边界

- `FtpHandlerImpl#start()` 在首次连接失败时会进入重连路径，但这不代表网络问题已被解决，仍需按上述步骤定位根因。
- 若环境存在 DNS 漂移或 VIP 切换，建议记录故障发生时的实际解析结果（需实测）。
- 若端口连通但会话仍不可用，请继续排查“登录失败”中的 `chroot` 目录权限问题；该问题属于服务端策略，不是本项目参数校验故障。

## 登录失败

### 典型现象

- 能连通端口，但登录失败。
- 抛出 `FtpLoginException`（消息：`Wrong username or password`）。

### 排查步骤

1. 核对 `ftp.username`、`ftp.password` 是否与服务端一致。
2. 确认目标账号在 FTP 服务端可登录且未被禁用。
3. 检查服务端策略是否允许该账号写入目标目录（只读账号会导致后续写操作失败）。
4. 核对是否连到了错误的 FTP 目标（测试、预发、生产地址混用是高频问题）。
5. 核对服务端配置是否允许本地用户登录（vsftpd 常见项：`local_enable`）。
6. 使用原生 FTP 客户端用同账号执行一次登录，记录服务端原始返回信息用于对照。

### chroot 根目录写权限错误

当 vsftpd 开启 `chroot_local_user=YES` 且策略不可修改时，如果 FTP 用户的 chroot 根目录本身可写，
服务端可能拒绝登录或导致后续操作异常。该现象常被误判为账号错误或网络问题。

建议按以下步骤检查：

1. 确认用户 home（例如 `/data/ftp`）是否为 chroot 根目录。
2. 检查根目录权限与属主（示例：`ls -ld /data/ftp`）。
3. 若根目录可写，调整为不可写根目录，并将业务写操作下沉到子目录。  
   例如现有安装文档基线配置：`chmod 500 /data/ftp`。
4. 在子目录授予业务所需权限后，重新执行登录、上传、删除验证。

边界说明：

- `allow_writeable_chroot=YES` 可用于允许可写 chroot 根目录，但在“服务端配置不可编辑”前提下不可作为执行方案。
- 该问题属于 FTP 服务端安全策略边界，应用侧重试通常无法根治。

### 字符集与目标混淆说明

- 登录阶段通常不是 `server_charset` 的主要故障点，但如果账号/路径策略依赖特定编码，仍需对齐 `ftp.server_charset` 与服务端实际编码。
- 若账号与地址均正确但仍失败，建议抓取服务端认证日志进一步复核（需实测）。
- 如果 `dwarfeng-ftp` 与原生 FTP 客户端在同账号下均失败，优先按服务端账号策略与目录权限方向排查。

## 文件传输阻塞或超时

### 典型现象

- 控制连接建立成功，但上传/下载卡住。
- 大文件传输长时间无响应，或间歇性超时。

### 排查重点

1. 在应用外先做一次原生 FTP 客户端同动作复现（登录、`cd`、上传、下载），确认是否为服务端/网络共性问题。
2. 核对 `ftp.data_connection_mode` 是否匹配网络环境。`0` 本地主动、`1` 远程主动、`2` 本地被动、`3` 远程被动。
3. 客户端在 NAT/防火墙后时，优先验证被动模式（通常 `2`）是否更稳定。
4. 若使用主动模式（`0`），建议将 `ftp.data_timeout` 设为大于 `0`，避免数据连接长期阻塞。
5. 若使用远程主动模式（`1`），确认 `ftp.active_remote_data_connection_mode_server_host` 与
   `ftp.active_remote_data_connection_mode_server_port` 已正确配置。
6. 检查服务端被动端口范围是否已配置并放通。该端口范围不属于本项目参数，需要在 FTP 服务端侧配置并实测。
7. 检查是否存在流未关闭导致的“应用内阻塞”：`openInputStream` / `openOutputStream` 在流关闭前会持有处理器锁，其它调用会被阻塞。
8. 对 `copyFile` 场景，检查临时目录磁盘空间与权限：`ftp.temporary_file_directory_path`、`ftp.file_copy_memory_buffer_size`。

### 运行建议

- 流式传输统一使用 `try-with-resources`，避免遗漏关闭。
- 对不稳定网络设置合理的 `data_timeout`，并保留失败重试与监控告警。

## 中文目录或文件名乱码

### 典型现象

- 上传后的中文文件名在服务端显示乱码。
- 列表接口返回的文件名与实际期望不一致。

### 排查步骤

1. 核对 `ftp.server_charset` 与服务端字符集是否一致（常见为 `UTF-8`）。
2. 使用同一账号通过其它 FTP 客户端（如 FileZilla）验证同目录文件名表现，区分“服务端编码问题”与“客户端展示问题”。
3. 检查历史文件：已以错误编码写入的文件名，通常不会因修改配置自动恢复。
4. 若跨平台混用（Windows/Linux）且目录名包含中文，建议先在测试环境做端到端读写验证（需实测）。

### 边界说明

- 本项目通过 `FTPClient#setControlEncoding` 设置控制通道编码，但最终展示仍受服务端与客户端双方编码策略影响。

## 删除目录失败

### 典型现象

- 调用 `removeDirectory(...)` 失败，抛出 `FtpFileDeleteException`。
- 目录看似存在但无法删除。

### 排查步骤

1. 确认目录是否为空。`removeDirectory` 仅支持删除空目录，非空目录需先 `clearDirectory` 再删除。
2. 核对路径是否正确，避免删除到错误层级。
3. 检查账号是否具备目录删除权限。
4. 检查目录中是否存在服务端拒删对象（权限、锁定、只读策略等，需实测）。
5. 若批量清理，优先使用项目示例流程验证：`com.dwarfeng.ftp.example.ClearDirectoryExample`。

### 实现行为提示

- `clearDirectory` 会递归清空传入目录下内容，但保留该目录本身。
- 传入空路径数组删除目录会失败（根目录不允许删除）。

## 原生客户端交叉验证

当问题难以判断时，建议使用系统原生 FTP 客户端执行与应用相同的动作，以获得更直接的服务端反馈。

### Windows

1. 在“启用或关闭 Windows 功能”中启用 FTP 客户端（`FTP Client`）。
2. 使用命令行连接目标：`ftp <host>`。
3. 依次执行登录、切目录、上传/下载、删除，记录返回码与报错文本。

### CentOS

1. 安装基础客户端（示例）：`yum install -y ftp`。
2. 使用命令连接目标：`ftp <host>`。
3. 依次执行登录、切目录、上传/下载、删除，记录返回码与报错文本。

### 使用方法

1. 使用与应用完全一致的主机、端口、账号、目标目录。
2. 如果原生客户端也失败，优先排查服务端与网络环境。
3. 如果原生客户端成功而应用失败，再回到应用参数、调用时序与流关闭约束排查。

## 运维检查清单

### 上线前检查

1. 配置检查：`ftp.host`、`ftp.port`、`ftp.username`、`ftp.password`、`ftp.server_charset`。
2. 模式检查：`ftp.data_connection_mode` 是否匹配网络拓扑。
3. 超时检查：`ftp.connect_timeout > 1000`，`ftp.noop_interval < ftp.connect_timeout`。
4. 目录检查：`ftp.temporary_file_directory_path` 存在且可读写。
5. chroot 检查：若启用 `chroot_local_user=YES`，根目录建议不可写，业务写入放在子目录。
6. 服务检查：vsftpd 已启动，控制端口和被动端口范围已放通。
7. 冒烟检查：原生 FTP 客户端与应用各执行一次最小上传/下载验证。
8. 功能检查：执行一次上传、下载、清空目录、删除目录的最小回归。

### 故障发生时检查

1. 先确认控制连接（地址、端口、服务状态）。
2. 再确认登录状态（账号、密码、权限）。
3. 再确认数据通道（模式、防火墙、NAT、超时参数）。
4. 检查是否有流未关闭导致同实例阻塞。
5. 检查磁盘空间与临时目录权限，排除 `copyFile` 相关问题。
6. 用原生 FTP 客户端复现同动作，确认故障是否可在应用外稳定复现。
7. 对比最近配置变更与发布时间点，快速回退可疑参数。

## 参阅

- [Usage Guide](./UsageGuide.md) - 使用指南，说明接入方式、配置方式与 API 约束。
- [Config Parameters](./ConfigParameters.md) - 配置参数详解，说明 `FtpConfig` 各字段含义与校验规则。
- [Data Connect Modes](./DataConnectModes.md) - 数据连接模式说明，介绍 `0/1/2/3` 四种模式适用场景。
- [Quick Start](./QuickStart.md) - 快速开始，包含最小化配置与示例入口。
- [How to Install vsftpd on CentOS 7](./HowToInstallVsftpdOnCentos7.md) - vsftpd 安装与基础配置参考。
