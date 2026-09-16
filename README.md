## 一、功能介绍
## 二、部署
### 2.1 部署代码

`clone` 本仓库到 `/home/diinki/` 目录下，找到 `deploy/test/app/docker-compose-mid.yml`，通过下面的命令进行容器编排

```shell
$ docker compose -p nexus-stack -f docker-compose-mid.yml up -d
```

部署以下容器

* `Redis`，映射主机 5379 端口，默认认证密码：`bite@123`，容器名：`frameworkjava-redis`
* `MySQL`，映射主机 3306 端口，默认用户：`bitedev`，密码：`bite@123` （MYSQL_ROOT_PASSWORD=bite@123）