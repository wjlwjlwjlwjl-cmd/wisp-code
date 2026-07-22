```plaintext
  _   _ _______  ___   _ ____       ____ _____  _    ____ _  __
 | \ | | ____\ \/ / | | / ___|     / ___|_   _|/ \  / ___| |/ /
 |  \| |  _|  \  /| | | \___ \ ____\___ \ | | / _ \| |   | ' / 
 | |\  | |___ /  \| |_| |___) |_____|__) || |/ ___ \ |___| . \ 
 |_| \_|_____/_/\_\\___/|____/     |____/ |_/_/   \_\____|_|\_\
```

# NEXUS-STACK，基于 Spring Cloud 的 Java Web 应用开发脚手架

> 集成 MySQL、Nacos、RabbitMQ、Redis、Caffeine 等，封装常用工具类，接入阿里云 OSS、腾讯位置服务等外部 API，搭建 B、C 端用户登陆结构（邮箱 + 密码认证，暂未找到个人短信验证码 SDK）

## 脚手架结构

```shell
├── deploy
├── nexus-admin
├── nexus-common
├── nexus-file
├── nexus-gateway
├── nexus-mstemplate
└── nexus-portal
```

* deploy，提供 Mysql、RabbitMQ、Nacos、Redis 的 Docker 快速部署与表结构、用户信息等的同步配置（默认用户名、密码见 [用户配置](./user_info.md#中间件用户与密码配置)    
* nexus-admin，提供管理业务结构，如：字典服务、参数服务、地图位置服务（经纬度定位、行政区划列表获取）、B 端用户管理、C 端用户管理等
* nexus-common，封装项目通用结构、工具类、中间件操作封装，如：

  * nexus-common-cache，缓存服务，搭建二级缓存结构，使用 Caffeine 作为一级本地缓存，Redis 作为远程二级缓存，提供自动更新缓存数据、启动时数据预热等功能
  * nexus-common-core，存放各种常用工具类：

    * `AESUtil`，封装 Hutool 相应接口，提供文本的加密、解密
    * `BeanCopyUtil`，封装 Spring 的 BeanUtils 提供列表的 Bean 拷贝
    * `JsonUtil`，序列化、反序列化封装（解决嵌套类型、List、Map 的泛型擦除）
    * `PageUtil`，总页数 + 页大小 => 分页数量
    * `ServletUtil`，封装 Servlet 常用方法
    * `StringUtil`，字符串常用操作
    * `TimeStamp`，时间戳常用操作
    * `VerifyUtil`，通用校验工具类

  * nexus-common-domain，封装各种常量（线程池、缓存、JWT token 相关等）、DTO/VO 基类、统一状态码与状态码承载类、服务异常等
  * nexus-common-rabbitmq，封装 RabbitMQ 常用操作
  * nexus-common-redis，封装 Redis 常用操作、分布式锁的获取与释放
  * nexus-common-security，统一异常处理（对接口错误产生的异常进行处理）、JWT 相关操作封装与相应 DTO 对象定义

* nexus-file，文件存储操作，这里指的是远端存储。通过封装阿里云 OSS 接口，提供两种上传方式：1. 上传服务器，服务器上传阿里云 OSS 服务器；2. 客户端向服务器获取签名，由客户端持签名自行上传阿里云 OSS 服务器

* nexus-gateway，为请求提供路由功能，并通过过滤器进行 Cookie-Session 用户鉴权，同时提供网关异常捕获

* nexus-template，模板微服务，没有实际功能，只是对其他模块的调用起到演示作用