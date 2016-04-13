

包含SMSSDK/WeMall-Client/social_sdk_library_project三个项目以及Api目录下的client.php/update.xml接口文件,其中WeMall-Client依赖于项目SMSSDK、social_sdk_library_project

使用指南:本客户端是WeMall开源微信商城的Android版,首先需要您部署WeMall微信商城才能使用,使用方法如下

##### 一：安装wemall微信商城篇

###### (1):将WeMall-Server项目(WeMall开源商城)安装到您的Web服务器，测试环境这里推荐使用wamp环境，快捷部署
###### (2):wemall微信商城安装完毕后将Api目录下的client.php和update.xml接口文件复制到WeMall开源商城Web服务器的Api目录下

#####二：导入客户端代码并编译生成自己的版本篇

###### (1):将SMSSDK/social_sdk_library_project项目导入到你的IDE（Eclipse或者Android Studio）</br>
###### (2):将WeMall-Client项目导入到你的IDE，然后需要可能会报错，这里重新配置项目依赖，依赖于SMSSDK/social_sdk_library_project</br>
###### (3):修改WeMall-Client项目源码中的Myconfig.java,将服务端地址设置为你们的WeMall微信商城地址</br>
###### (4):配置完毕后即可测试运行wemall安卓客户端了</br>
###### (5):当客户端编译新的版本时,将apk上传到服务器后配置Api/update.xml文件即可，当用户检测新版本时，即可收到新的版本提示信息</br>

###### 注1:本客户端兼容Wemall微信商城3.x版本，只要WeMall商城安装正常，Api目录下的文件上传到服务器指定地址，客户端即可正常访问</br>
###### 注2:直接下载客户端源代码打包也可运行客户端,默认接入我的演示服务端</br>
###### 注3:第一次分享项目，第一次写Android应用，技术不精，说的不明白的可联系QQ793554262，闲暇时间可以交流咨询<br>
###### 附加信息
演示服务端地址www.uaide.net/wemall/  
测试管理员admin密码admin,请不要随意删除服务器已存在数据
*** 
###### 日志记录
2015-4-13  
修复注册时用户名在某些情况下被截断的问题

2015-4-14  
修复部分快速点击导致的重复弹窗
更改服务端接口地址为Server/Api/Client.php

2015-4-14  
代码维护更新中，目前不是很完善，继续。。。。。

2015-4-16  
修改README文档

2015-4-17  
完美解决左侧抽屉菜单和viewpager不能兼容左右滑动的问题

2015-1124
升级U盟分享SDK