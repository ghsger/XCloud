# XCloud

### 作者想说

* 我是高校在校生，疫情期间（2020年初-2020年中），自学Java后端课程（白嫖培训机构网课） ，当然掌握精通肯定不现实，有兴趣的可以加入哦！
* 现在说一下XCloud这个项目的开发初衷，因为***疫情网课***的缘故， 大学的老师在今年的第二学期爱上了这些***网课软件***，需要在这些软件上交作业，导致我上课需要带一个U盘，
  有时候机房的电脑还不读U盘，聪明的同学们会用邮件把作业发送到自己的手机上， 比较繁琐。某度之类的网盘使用门槛又比较高。
* 基于以上这些原因，还有我私人想要再做一个项目练手的原因， 先写了一个***XCloud浏览器***版本，基本网盘功能全部实现。 因为一直忙在后端课程和项目，别的课听的很少，而Android课又到了期末课程设计，
  所以看了几天Android书，写了一个***XCloud Android***版本，作者水平有限，不足之处还有很多，多包涵！
* 下面开始介绍***XCloud***。

***

## 项目架构

#### 简单的前台系统结构图

<img src="http://www.zf233.com.cn/static/markdown/xcloud/detial.png" width="700">

### App

* Android Java语言
* 三方库
    * okhttp: 3.14.2
    * okio: 1.17.4
    * gson: 2.8.6

### 后台

* SpringBoot
* MyBatis
* MySQL
* Redis:缓存
* Druid:阿里高性能数据库链接池
* FastDFS:文件服务器
* Nginx
* Tomcat
* Maven

## 页面展示

* #### 欢迎&导航&主界面
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/welcome.jpeg" width="200px">
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/main.jpeg" width="200px">
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/home.jpeg" width="200px">
* #### 用户信息&登陆&注册界面
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/user.jpeg" width="200px">
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/login.jpeg" width="200px">
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/regist.jpeg" width="200px">
* #### 开发者信息界面（没啥用）
    <img src="http://www.zf233.com.cn/static/markdown/xcloud/developers.jpeg" width="200px">

### 本地开发运行部署

#### Xcloud App项目

* 版本
    * JDK版本: 1.8
    * Android Gradle Plugin Version: 4.1.1
    * Gradle Version: 6.5
* 下载zip直接解压，启动Android Studio导入项目
* 安装git后执行克隆命令
* Android Studio 创建项目选择 Get from Version Control </br>并填写此项目仓库URL地址https://github.com/zhang-sexy/XCloud.git

#### Xcloud后台项目

* 后台项目请移步到本人XCloud-Server仓库 </br>https://github.com/zhang-sexy/XCloud-Server.git

### 写在最后

* 个人学习使用遵循GPL开源协议
* Logo已申请版权，请勿商用
* 其他UI图标来自<a href="https://http://www.iconfont.cn">Iconfont-阿里巴巴矢量图标库</a> 以及<a href="https://http://www.easyicon.net">
  easyicon</a>
