# FileServer
基于NanoHttpd二次开发的WebServer，用于文件上传

## FileServerCore
- 便于移植到Android上的核心
1. 使用IDEA打开FileSERVERCore
2. 配置 src/main/resources/server.properties
    - WEBDOC = FileServerWebDOC的根目录
    - DISKPATH = 上传文件所在的磁盘根目录
    - PORT = 端口号
    - UPLOAD = 上传文件夹所在路径
3. run main/webserver/client/ServerRunner.main()

## FileServerWebDoc
- 前端页面