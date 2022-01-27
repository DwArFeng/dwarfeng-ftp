# dwarfeng-ftp

Dwarfeng（赵扶风）的 FTP 服务，基于 `subgrade` 项目，在 `commons-io` 的基础上做了进一步封装，目前用于多个个人项目。

---

## 特性

1. Subgrade 架构支持。
2. 保存文件时创建不存在的目录。
3. 中文编码支持。
4. 自动保持连接。
5. 自动保持连接与断线重连。

运行 `src/test` 下的 `Example` 以观察全部特性。

## 安装说明

1. 下载源码

   使用 git 进行源码下载。
   ```
   git clone git@github.com:DwArFeng/dwarfeng-ftp.git
   ```
   对于中国用户，可以使用 gitee 进行高速下载。
   ```
   git clone git@gitee.com:dwarfeng/dwarfeng-ftp.git
   ```

2. 项目安装

   进入项目根目录，执行 maven 命令
   ```
   mvn clean source:jar install
   ```

3. enjoy it

---

## 如何使用

1. 运行 `src/test` 下的 `Example` 以观察全部特性。
2. 观察项目结构，将其中的配置运用到其它的 subgrade 项目中。
