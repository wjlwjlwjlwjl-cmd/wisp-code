# 1. 初始化nacos配置数据
# 注意： 此前如果修改过nacos外接数据库名称，此处需确保名称一致

use `frameworkjava_nacos_test`;
INSERT INTO config_info (data_id,group_id,content,md5,gmt_create,gmt_modified,src_user,src_ip,app_name,tenant_id,c_desc,c_use,effect,`type`,c_schema,encrypted_data_key) VALUES

('share-wispcode-test.yaml','DEFAULT_GROUP','app:
  preview:
    container-name: wispcode-userapp-preview
  host: 192.168.160.131
docker:
  host: tcp://192.168.160.131:2376
  cert: /workspace/cert
chat:
  memory:
    maxLen: 5
    ttl: 24

spring:
  ai:
    dashscope:
      api-key: {fill your api_key here}
      chat:
        options:
          model: deepseek-v4-pro-0813
          temperature: 0.7
    # -------- TEI bge‑m3 embedding --------
    openai:
      base-url: http://192.168.160.131:8090/v1
      api-key: dummy-tei
      chat:
        enabled: false
    # -------- Milvus --------
    vectorstore:
      milvus:
        enabled: true
        client:
          host: 192.168.160.131
          port: 19530
        collection-name: RAG
        embedding-dimension: 1024
        initialize-schema: true
        index-type: HNSW
        metric-type: COSINE

management:
  health:
    redis:
      enabled: false','42080302708734148213090210300147',now(),now(),'nacos','112.46.64.96','wisp-code configuration','frameworkjava-test',NULL,NULL,NULL,'yaml',NULL,'');

('share-common-test.yaml','DEFAULT_GROUP','feign:
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
      enabled: true
','6572759c52633434823509b70b430c4e',now(),now(),'nacos','112.46.64.96','common configuration','frameworkjava-test','','','','yaml','',''),

('share-redis-test.yaml','DEFAULT_GROUP','spring:
  cache:
    type: redis
  data:
    redis:
      host: frameworkjava-redis
      port: 6379
      password: bite@123
  redis:
    host: frameworkjava-redis
    port: 6379
    password: bite@123
','15803074b36c38b09331395091643875',now(),now(),'nacos','172.19.0.1','Common Redis Configuration','frameworkjava-test','','','','yaml','',''),

('share-mysql-test.yaml','DEFAULT_GROUP','spring:
  datasource:
    url: jdbc:mysql://192.168.160.131:3306/wispcode?useSSL=false&autoReconnect=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: bitedev
    password: bite@123
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      pool-name: HikariCP
      minimum-idle: 5
      idle-timeout: 600000
      maximum-pool-size: 10
      auto-commit: true
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1
mybatis-plus:
    typeAliasesPackage: com.bitejiuyeke.**.domain
    mapperLocations: classpath*:mapper/**.xml
    configuration:
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl','2256b897837515b4253601b87468809e',now(),now(),'nacos','172.18.0.1','Common MySQL Configuration','frameworkjava-test','','','','yaml','',''),

('bite-gateway-test.yaml','DEFAULT_GROUP','spring:
  cloud:
    gateway:
      discovery:
        locator:
          lowerCaseServiceId: true
          enabled: true
      routes:
        - id: bite-mstemplate
          uri: lb://mstemplate
          predicates:
            - Path=/mstemplate/**
          filters:
            - StripPrefix=1
        - id: bite-portal
          uri: lb://bite-portal
          predicates:
            - Path=/portal/**
          filters:
            - StripPrefix=1
        - id: bite-admin
          uri: lb://bite-admin
          predicates:
            - Path=/admin/**
          filters:
            - StripPrefix=1
        - id: bite-file
          uri: lb://bite-file
          predicates:
            - Path=/file/**
          filters:
            - StripPrefix=1
          metadata:
            response-timeout: 300000
            connect-timeout: 300000

security:
  ignore:
    whites:
      - /admin/logout
      - /admin/register
      - /admin/codeLogin
      - /**/login/**
      - /**/send_code/**
      - /**/nologin/**
      - /**/test/**','a0519254280c43363872c546441b4655',now(),now(),'nacos','112.46.64.96','Gateway','frameworkjava-test','','','','yaml','',''),

('share-rabbitmq-test.yaml','DEFAULT_GROUP','spring:
  rabbitmq:
    port: 5672
    host: 192.168.160.131
    virtual-host: /
    username: admin
    password: bite@123','3b76b88c362b16b73256b7715248c78c',now(),now(),'nacos','112.46.64.96','Common RabbitMQ Configuration','frameworkjava-test','','','','yaml','','');


INSERT INTO config_info (data_id,group_id,content,md5,gmt_create,gmt_modified,src_user,src_ip,app_name,tenant_id,c_desc,c_use,effect,`type`,c_schema,encrypted_data_key) VALUES
    ('share-caffeine-test.yaml','DEFAULT_GROUP','caffeine:
  build:
    initial-capacity: 128
    maximum-size: 1024
    expire: 60','6304904002990117226212343872707c',now(),now(),'nacos','112.46.64.96','Local Cache Configuration','frameworkjava-test',NULL,NULL,NULL,'yaml',NULL,'');


INSERT INTO tenant_info (kp,tenant_id,tenant_name,tenant_desc,create_source,gmt_create,gmt_modified) VALUES
    ('1','frameworkjava-test','frameworkjava-test','Test Environment','nacos',unix_timestamp()*1000,unix_timestamp()*1000);