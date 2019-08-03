# FileServer
基于NanoHttpd二次开发的WebServer，用于文件上传
--- 
## FileServerCore
- 便于移植到Android上的核心
1. 使用IDEA打开FileServerRCore
2. 配置 src/main/resources/server.properties
    - WEBDOC = FileServerWebDOC的根目录
    - DISKPATH = 上传文件所在的磁盘根目录
    - PORT = 端口号
    - UPLOAD = 上传文件夹所在路径
3. run main/webserver/client/ServerRunner.main()
4. 当需进行一个版本的新更新时，应更新 MANIFEST.MF 中的版本号
--- 
## FileServerAndroid
- 包含Phone与Wear两个Model
- 当前仅移植Wear Model
1. 使用Android Studio打开FileServerAndroid
2. build wear
--- 
## FileServerWebDoc
- 前端页面
- 使用Vue.js与AntD前端框架开发
1. 克隆到本地后，应按照 https://vue.ant.design/docs/vue/use-with-vue-cli-cn/ 配置依赖
    - 可使用vue ui打开可视化UI进行配置
2. 需要的依赖有
    - 运行依赖
        - VueRouter
        - axios
    - 开发依赖
        - less
        - less loader
3. 可使用 npm run serve 进行热更新测试
4. 可使用 npm run build 进行编译
    - 编译后静态页面内容应在 文件夹根目录/dist/ 文件夹下
--- 
### 加入群交流
![setTile](https://raw.githubusercontent.com/lzjyzq2/FileServer/master/fileserverwebdoc/src/assets/joinQR.jpg)

### 如果喜欢可以捐助~~~
![捐助](https://raw.githubusercontent.com/lzjyzq2/FileServer/master/fileserverwebdoc/src/assets/jz.png)
