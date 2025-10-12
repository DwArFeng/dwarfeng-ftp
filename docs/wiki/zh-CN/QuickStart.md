# Quick Start - 快速开始

## 确认系统需求

- CPU：2核以上。
- 内存：2G 以上。
- 硬盘：50G 以上。
- CentOS 7。
- vsftpd 3.0.2。

## vsftpd 初始化

妥善设置好 vsftpd 的用户名、密码、存储目录等信息。

如果您的 Centos 7 系统中没有安装 vsftpd，可以按照以下的教程，在 10 分钟之内完成安装：

- [How to Install vsftpd on CentOS 7](./HowToInstallVsftpdOnCentos7.md) - 如何在 CentOS 7 上安装 vsftpd。

## 源码下载

使用 git 进行源码下载。

```shell
git clone git@github.com:DwArFeng/dwarfeng-ftp.git
```

对于中国用户，可以使用 gitee 进行高速下载。

```shell
git clone git@gitee.com:dwarfeng/dwarfeng-ftp.git
```

## 最小化配置

在下载的源码目录下，新建 `conf/test.ftp` 目录。

随后，将 `src/test/resources/ftp/connection.properties` 文件复制到新建好的目录下。

同时，需要将以下配置项更改为 ftp 服务器的实际值。

```properties
# FTP 的主机名称。
ftp.host=your-host-here
# FTP 的端口号。
ftp.port=21
# FTP 的登录用户名。
ftp.username=your-username-here
# FTP 的登录密码。
ftp.password=your-password-here
```

## 效果体验

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

需要注意的是，示例类会让您指定一个 FTP 的根目录，默认为 `foobar`，如果您使用 vsftpd 作为 FTP 服务器，
您需要提前创建好这个目录，这是因为在部分配置下，vsftpd 不允许用户直接在 FTP 服务对应的根目录下进行操作。
