<p align="center">
  <img src="./pictures/index.jpeg" alt="首页" width="85%">
</p>

## 一、技术栈

1. Spring Boot、Spring AI、Spring Cloud、MyBatis-Plus 等

2. 中间件或小组件：Git、Docker、Redis、MySQL、Nacos、Nginx、Milvus 等

## 二、功能介绍

### 2.1 根据自然语言描述生成应用

1. 用户不需要考虑需求文档的层次，只需要给出最基本的需求，比如：做一个贪吃蛇、写一个个人博客页面，LLM 会自动根据用户需求生成软件需求

2. 支持三种类型应用：HTML、Vue、Spring_Vue（前后端结合）

3. 应用构建分为两种方式：

   * 单 Agent 完成代码编写、编译构建、构建预览、代码上传

   * 多 Agent 构建 Graph，在单 Agent 分别负责上述任务的基础上，增加编译报错修复节点

<p align="center">
  <img src="./pictures/RequirementGenerate.jpeg" alt="应用文档生成" width="50%">
</p>
<p align="center">
  <img src="./pictures/AppGenerate.jpeg" alt="应用代码生成" width="50%">
</p>

### 2.2 LLM 生成应用实时预览

后端会自动完成代码的编译构建并返回预览 URL，前端点击跳转即可直接浏览页面效果，并与后端（如果存在）进行交互

<p align="center">
  <img src="./pictures/preview.jpeg" alt="应用预览" width="50%">
</p>

### 2.3 对 LLM 生成的应用进行修改

1. 对于普通用户，提供直接通过自然语言重新要求 LLM 对代码进行修改的选项

2. 对于高级开发者，通过 Code Server 容器，提供类似桌面端 VSCode 的代码编辑体验（Code OSS，无 Microsoft 插件市场）

<p align="center">
  <img src="./pictures/CodeEdit.jpeg" alt="代码编辑" width="50%">
</p>

> 目前出于权限限制考虑，Code Server 容器采用动态创建，并且只挂载宿主机上用来预览的代码目录。

### 2.4 基于 Gitee API 进行云端代码存储

最初设计实现为 MCP，后续发现直接封装为方法可实现相同效果，并且不会受模型输出导致 ToolCalling 异常的影响。提供以下三个方法：

* commit：模拟 `git commit`，不存在则新增，存在则更新

* pull：将云端代码拉取到本地

* delete：删除云端代码

### 2.5 RAG

* 向量数据库：使用 Docker 部署 Milvus，默认使用 BAAI/bge-m3 作为嵌入模型

* 在开发过程中发现，相关内容通过 LLM 的系统提示词传递更加适合，故目前只搭建了 RAG 系统，并未直接提供作为检索资料的语料。如果需要，直接添加文本内容即可

### 2.6 应用论坛

用户可以将自己构建的应用进行部署，供所有用户查看

### 2.7 登录系统

通过邮件验证码进行用户注册、登录

### 2.8 其他功能

1. 前端页面主题切换

2. 使用 JWT Token + Redis 完成登录态处理

## 三、部署

### 3.1 部署代码

Nginx 配置：将 `nginx/conf/nginx.conf` 中的 IP 更新

`git clone` 本仓库到 `/home/diinki/` 目录下，找到 `deploy/test/app/docker-compose-mid.yml`，通过下面的命令进行容器编排：

```shell
docker compose -p nexus-stack -f docker-compose-mid.yml up -d
```

部署了以下容器：

* Redis：映射主机 5379 端口，默认认证密码 `bite@123`，容器名 `frameworkjava-redis`

* MySQL：映射主机 3306 端口，默认用户 `bitedev`，密码 `bite@123`（MYSQL_ROOT_PASSWORD=bite@123）

* Nacos：映射主机 8848、9848 端口，默认用户 `nacos`，密码 `bite@123`

* Milvus：向量数据库。默认用户 `root`，默认密码 `minioadmin`，WebUI 访问 `http://localhost:9091/webui`，或者通过 Attu 客户端。创建数据库 `wispcode_db`；创建 Collection `RAG`（对应表），设置下面的字段：

  * doc_id：主键，VarChar(36)

  * content：VarChar(65535)

  * metadata：JSON

  * embedding：1024 维的向量，对应到 BAAI/bge-m3 输出向量

* bge-m3-embedding：因为网络环境原因，直接从 HuggingFace 获取嵌入模型不稳定，所以通过 `model_download.py` 下载，容器编排自动完成挂载，容器位置位于 `/data`

* wispcode-userapp-preview：预览容器，包含 Nginx + JDK

### 3.2 准备后续所需镜像

1. `codercom/code-server:4.137.0`：用于后续为用户提供网页端代码编辑

2. 制作镜像：主要后端服务容器需要提供 JDK、npm、mvn，没有现成的镜像，需手动制作（虽然也上传了 Docker Hub，但考虑网络环境，选取自行制作；也可直接从仓库拉取：`docker pull wangjialelele/jdk21-mvn-npm:v1.0`）

### 3.3 配置 Nacos 配置

打开 Nacos 浏览器管理界面，更改如下配置：

* `share-wispcode-test.yaml`：更改 API-KEY，以及嵌入模型服务器、向量数据库、预览容器、Docker 服务器、code-server 容器的 IP 地址和 Gitee Access-Token

* `share-mysql-test.yaml`：更改 MySQL 服务器 IP，数据库名称不需更改

* `share-email-test.yaml`：更改邮箱、SMTP 密码

### 3.4 后端服务配置更改

#### 3.4.1 Docker Server IP

后端服务使用 Docker Maven Plugin 进行服务打包部署，在根 `pom.xml` 中修改 Docker Server IP

#### 3.4.2 Nacos Server

同样在根 `pom.xml` 中更新 Nacos Server IP

### 3.5 Docker 远程访问证书

1. 运行 `deploy/app/config/cert` 下的 `cert.sh`（需要 root 权限，并且修改 SERVER 地址；同时建议使用 `file` 命令查看是否含有 Windows 换行符，如果有，使用 `dos2unix` 修改）

   生成如下文件：

   * `ca-key.pem`：CA 密钥

   * `ca.pem`：CA 证书

   * `cert.pem`：客户端证书

   * `extfile.cnf`：客户端证书扩展配置文件

   * `key.pem`：客户端密钥

   * `server-cert.pem`：服务端证书

   * `server-key.pem`：服务端密钥

2. 将 `ca.pem`、`server-cert.pem`、`server-key.pem` 放到 `/etc/docker`；将 `ca.pem`、`cert.pem`、`key.pem` 放到 `wisp-code/deploy/test/app/config/cert` 目录下（`cert.sh` 同级目录）

   将 `/lib/systemd/system/docker.service` 中的相应部分替换为：

   ```
   ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2376 --tlsverify=true --tlscacert=/etc/docker/ca.pem --tlscert=/etc/docker/server-cert.pem --tlskey=/etc/docker/server-key.pem -H fd:// --containerd=/run/containerd/containerd.sock
   ```

   随后重启 Docker 服务（`daemon-reload`、`restart`）
