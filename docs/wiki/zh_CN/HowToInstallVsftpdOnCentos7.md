# How to Install vsftpd on CentOS 7 - 如何在 CentOS 7 上安装 vsftpd

## 更换 CentOS 7 安装源

1. 使用 SFTP 等方式，备份 `/etc/yum.repos.d/CentOS-Base.repo` 文件。
2. 使用 SFTP 等方式，将以下内容保存为 `/etc/yum.repos.d/CentOS-Base.repo` 文件：

   ```ini
   # CentOS-Base.repo
   #
   # The mirror system uses the connecting IP address of the client and the
   # update status of each mirror to pick mirrors that are updated to and
   # geographically close to the client.  You should use this for CentOS updates
   # unless you are manually picking other mirrors.
   #
   # If the mirrorlist= does not work for you, as a fall back you can try the 
   # remarked out baseurl= line instead.
   #
   #
   
   [base]
   name=CentOS-$releasever - Base
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=os&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/os/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #released updates 
   [updates]
   name=CentOS-$releasever - Updates
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=updates&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/updates/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #additional packages that may be useful
   [extras]
   name=CentOS-$releasever - Extras
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=extras&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/extras/$basearch/
   gpgcheck=1
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   #additional packages that extend functionality of existing packages
   [centosplus]
   name=CentOS-$releasever - Plus
   #mirrorlist=http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=centosplus&infra=$infra
   baseurl=http://vault.centos.org/7.9.2009/centosplus/$basearch/
   gpgcheck=1
   enabled=0
   gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
   
   ```

## 安装 vsftpd

1. 执行以下命令安装 vsftpd：

   ```bash
   sudo yum install -y vsftpd
   ```

## 配置 vsftpd

1. 使用 SFTP 等方式，备份 `/etc/vsftpd/vsftpd.conf` 文件。
2. 使用 SFTP 等方式，将以下内容保存为 `/etc/vsftpd/vsftpd.conf` 文件：

   ```ini
   # Example config file /etc/vsftpd/vsftpd.conf
   #
   # The default compiled in settings are fairly paranoid. This sample file
   # loosens things up a bit, to make the ftp daemon more usable.
   # Please see vsftpd.conf.5 for all compiled in defaults.
   #
   # READ THIS: This example file is NOT an exhaustive list of vsftpd options.
   # Please read the vsftpd.conf.5 manual page to get a full idea of vsftpd's
   # capabilities.
   #
   # Allow anonymous FTP? (Beware - allowed by default if you comment this out).
   anonymous_enable=NO
   #
   # Uncomment this to allow local users to log in.
   # When SELinux is enforcing check for SE bool ftp_home_dir
   local_enable=YES
   #
   # Uncomment this to enable any form of FTP write command.
   write_enable=YES
   #
   # Default umask for local users is 077. You may wish to change this to 022,
   # if your users expect that (022 is used by most other ftpd's)
   local_umask=022
   #
   # Uncomment this to allow the anonymous FTP user to upload files. This only
   # has an effect if the above global write enable is activated. Also, you will
   # obviously need to create a directory writable by the FTP user.
   # When SELinux is enforcing check for SE bool allow_ftpd_anon_write, allow_ftpd_full_access
   #anon_upload_enable=YES
   #
   # Uncomment this if you want the anonymous FTP user to be able to create
   # new directories.
   #anon_mkdir_write_enable=YES
   #
   # Activate directory messages - messages given to remote users when they
   # go into a certain directory.
   dirmessage_enable=YES
   #
   # Activate logging of uploads/downloads.
   xferlog_enable=YES
   #
   # Make sure PORT transfer connections originate from port 20 (ftp-data).
   connect_from_port_20=YES
   #
   # If you want, you can arrange for uploaded anonymous files to be owned by
   # a different user. Note! Using "root" for uploaded files is not
   # recommended!
   #chown_uploads=YES
   #chown_username=whoever
   #
   # You may override where the log file goes if you like. The default is shown
   # below.
   #xferlog_file=/var/log/xferlog
   #
   # If you want, you can have your log file in standard ftpd xferlog format.
   # Note that the default log file location is /var/log/xferlog in this case.
   xferlog_std_format=YES
   #
   # You may change the default value for timing out an idle session.
   #idle_session_timeout=600
   #
   # You may change the default value for timing out a data connection.
   #data_connection_timeout=120
   #
   # It is recommended that you define on your system a unique user which the
   # ftp server can use as a totally isolated and unprivileged user.
   #nopriv_user=ftpsecure
   #
   # Enable this and the server will recognise asynchronous ABOR requests. Not
   # recommended for security (the code is non-trivial). Not enabling it,
   # however, may confuse older FTP clients.
   #async_abor_enable=YES
   #
   # By default the server will pretend to allow ASCII mode but in fact ignore
   # the request. Turn on the below options to have the server actually do ASCII
   # mangling on files when in ASCII mode. The vsftpd.conf(5) man page explains
   # the behaviour when these options are disabled.
   # Beware that on some FTP servers, ASCII support allows a denial of service
   # attack (DoS) via the command "SIZE /big/file" in ASCII mode. vsftpd
   # predicted this attack and has always been safe, reporting the size of the
   # raw file.
   # ASCII mangling is a horrible feature of the protocol.
   #ascii_upload_enable=YES
   #ascii_download_enable=YES
   #
   # You may fully customise the login banner string:
   #ftpd_banner=Welcome to blah FTP service.
   #
   # You may specify a file of disallowed anonymous e-mail addresses. Apparently
   # useful for combatting certain DoS attacks.
   #deny_email_enable=YES
   # (default follows)
   #banned_email_file=/etc/vsftpd/banned_emails
   #
   # You may specify an explicit list of local users to chroot() to their home
   # directory. If chroot_local_user is YES, then this list becomes a list of
   # users to NOT chroot().
   # (Warning! chroot'ing can be very dangerous. If using chroot, make sure that
   # the user does not have write access to the top level directory within the
   # chroot)
   chroot_local_user=YES
   #chroot_list_enable=YES
   # (default follows)
   #chroot_list_file=/etc/vsftpd/chroot_list
   #
   # You may activate the "-R" option to the builtin ls. This is disabled by
   # default to avoid remote users being able to cause excessive I/O on large
   # sites. However, some broken FTP clients such as "ncftp" and "mirror" assume
   # the presence of the "-R" option, so there is a strong case for enabling it.
   #ls_recurse_enable=YES
   #
   # When "listen" directive is enabled, vsftpd runs in standalone mode and
   # listens on IPv4 sockets. This directive cannot be used in conjunction
   # with the listen_ipv6 directive.
   listen=NO
   #
   # This directive enables listening on IPv6 sockets. By default, listening
   # on the IPv6 "any" address (::) will accept connections from both IPv6
   # and IPv4 clients. It is not necessary to listen on *both* IPv4 and IPv6
   # sockets. If you want that (perhaps because you want to listen on specific
   # addresses) then you must run two copies of vsftpd with two configuration
   # files.
   # Make sure, that one of the listen options is commented !!
   listen_ipv6=YES
   
   pam_service_name=vsftpd
   userlist_enable=NO
   tcp_wrappers=YES
   
   ```

3. 使用 SFTP 等方式，备份 `/etc/passwd` 文件。
4. 使用 SFTP 等方式，将 `/etc/passwd` 文件中 `ftp` 用户所在的配置行替换为如下内容：

   ```plaintext
   ftp:x:1000:1000::/data/ftp:/bin/bash
   ```

5. 创建 FTP 用户的存储目录 `/data/ftp`：

   ```bash
   sudo mkdir -p /data/ftp
   sudo chown ftp:ftp /data/ftp
   sudo chmod 500 /data/ftp
   ```

## 更改 ftp 用户的密码

1. 执行以下命令更改 ftp 用户的密码：

   ```bash
   sudo passwd ftp
   ```

   按照提示输入新密码，注意，密码需要符合系统的密码复杂度要求，并且需要按照提示输入两次以确认。

## 启动 vsftpd

1. 执行以下命令启动 vsftpd：

   ```bash
   sudo systemctl start vsftpd
   ```
