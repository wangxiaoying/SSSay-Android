#SSSay-Android

先做一个简单的版本，实现简单的功能出来。

##运行前配置
修改`com.momoz.sssay.utils.SSSayConfig`中的HOST_URL中的IP地址；

##jar包
- httpmime-4.1.1.jar
- json-simple-1.1.1.jar

##代码风格注意
- 所有的string变量都写在string.xml中，以为之后更换语言方便。

##代码结构介绍

###com.momoz.sssay
这个package是用来存放所有的界面activity，每个activity都是一个界面。

###com.momoz.sssay.backends
这个package中的每一个类，都是对应一个后台API的返回值的处理。

###com.momoz.sssay.utils
这个package顾名思义，其中所有的类都是一些相对独立，对于整个系统起到奠基或者辅助作用的类。