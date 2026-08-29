# 1. 初始化nacos配置数据
# 注意： 此前如果修改过nacos外接数据库名称，此处需确保名称一致

use `frameworkjava_nacos_test`;
INSERT INTO config_info (data_id,group_id,content,md5,gmt_create,gmt_modified,src_user,src_ip,app_name,tenant_id,c_desc,c_use,effect,`type`,c_schema,encrypted_data_key) VALUES
                                                                                                                                                                             ('share-common-test.yaml','DEFAULT_GROUP','# feign 配置
feign:
  okhttp:
    enabled: true
  httpclient:
    enabled: false
  client:
    config:
      default:
        connectTimeout: 10000
        readTimeout: 10000
  compression:
    request:
      enabled: true
    response:
      enabled: true','7a98e5666bfc562d278db12ea4cae5ed',now(),now(),'nacos','112.46.64.96','通用公共配置','frameworkjava-test','','','','yaml','',''),
                                                                                                                                                                             ('share-redis-test.yaml','DEFAULT_GROUP','spring:
  cache:
    type: redis
  data:
    redis:
      host: 192.168.160.131
      port: 6379
      password: bite@123','b846123425cb2e5cd3d6083d8a98b2b5',now(),now(),'nacos','172.19.0.1','通用Redis公共配置','frameworkjava-test','','','','yaml','',''),
                                                                                                                                                                             ('share-mysql-test.yaml','DEFAULT_GROUP','spring:
  datasource:
    url: jdbc:mysql://192.168.160.131:3306/frameworkjava_test?useSSL=false&autoReconnect=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: bitedev
    password: bite@123
    # 指定为HikariDataSource
    type: com.zaxxer.hikari.HikariDataSource
    # hikari连接池配置
    hikari:
      #连接池名
      pool-name: HikariCP
      #最小空闲连接数
      minimum-idle: 5
      # 空闲连接存活最大时间，默认10分钟
      idle-timeout: 600000
      # 连接池最大连接数，默认是10
      maximum-pool-size: 10
      # 此属性控制从池返回的连接的默认自动提交行为,默认值：true
      auto-commit: true
      # 此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认30分钟
      max-lifetime: 1800000
      # 数据库连接超时时间,默认30秒
      connection-timeout: 30000
      # 连接测试query
      connection-test-query: SELECT 1
mybatis-plus:
    # 搜索指定包别名
    typeAliasesPackage: com.bitejiuyeke.**.domain
    # 配置mapper的扫描，找到所有的mapper.xml映射文件
    mapperLocations: classpath*:mapper/**.xml
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl','0517d425d111e174fe3f2b799acbd3b1',now(),now(),'nacos','172.18.0.1','通用mysql公共配置','frameworkjava-test','','','','yaml','',''),
                                                                                                                                                                             ('bite-gateway-test.yaml','DEFAULT_GROUP','spring:
  cloud:
    gateway:
      discovery:
        locator:
          lowerCaseServiceId: true
          enabled: true
      routes:
        # 用户端服务
        - id: bite-mstemplate
          uri: lb://mstemplate
          predicates:
            - Path=/mstemplate/**
          filters:
            - StripPrefix=1
        # 门户服务
        - id: bite-portal
          uri: lb://bite-portal
          predicates:
            - Path=/portal/**
          filters:
            - StripPrefix=1
        # 鉴权模块
        - id: bite-admin
          uri: lb://bite-admin
          predicates:
            - Path=/admin/**
          filters:
            - StripPrefix=1
        # 文件
        - id: bite-file
          uri: lb://bite-file
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=1
          metadata:
            response-timeout: 300000
            connect-timeout: 300000

# 安全配置
security:
  # 不校验白名单
  ignore:
    whites:
      - /admin/logout
      - /admin/register
      - /admin/codeLogin
      - /**/login/**
      - /**/send_code/**
      - /**/nologin/**
      - /**/test/**','043148345f3f04a6d79a10daff04c0c3',now(),now(),'nacos','112.46.64.96','网关','frameworkjava-test','','','','yaml','',''),
                                                                                                                                                                             ('share-rabbitmq-test.yaml','DEFAULT_GROUP','spring:
  rabbitmq:
    port: 5672
    host: 192.168.160.131
    virtual-host: /
    username: admin
    password: bite@123','12b937d4c3b995bd0fa22f1eb03bc51f',now(),now(),'nacos','112.46.64.96','通用rabbitmq公共配置','frameworkjava-test','','','','yaml','','');

INSERT INTO config_info (data_id,group_id,content,md5,gmt_create,gmt_modified,src_user,src_ip,app_name,tenant_id,c_desc,c_use,effect,`type`,c_schema,encrypted_data_key) VALUES
    ('share-caffeine-test.yaml','DEFAULT_GROUP','caffeine:
  build:
    initial-capacity: 128
    maximum-size: 1024
    expire: 60','2257485195db60455f57aa575950a2ff',now(),now(),'nacos','112.46.64.96','本地缓存公共配置','frameworkjava-test',NULL,NULL,NULL,'yaml',NULL,'');



INSERT INTO tenant_info (kp,tenant_id,tenant_name,tenant_desc,create_source,gmt_create,gmt_modified) VALUES
    ('1','frameworkjava-test','frameworkjava-test','测试环境','nacos',unix_timestamp()*1000,unix_timestamp()*1000);