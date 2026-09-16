## 一、功能介绍

### 1.1 根据自然语言描述生成应用

1. 用户不需要考虑到需求文档的层次，只需要给出最基本的需求，比如：做一个贪吃蛇、写一个个人博客页面，LLM 会自动根据用户需求生成软件需求
2. 支持三种类型应用，HTML、VUE、Spring_VUE（前后端结合）
3. 应用构建，分为两种方式：
	* 单 Agent 完成代码编写、编译构建、构建预览、代码上传
	* 多 Agent 构建 Graph，在单 Agent 分别负责上述任务之外，增加编译报错修复节点
## 1.2 基于 Gitee API  进行云端代码存储

最初设计实现为 MCP，后续发现直接封装为方法可实现相同效果，并且不会受模型输出导致 ToolCalling 异常。提供下面三个方法

* commit，模拟 `git commit`，不存在新增，存在则更新
* pull，将云端代码拉取到本地
* delete，删除云端代码

### 1.3 RAG

* 向量数据库，使用 Docker 部署 Milvus，

## 二、部署
### 2.1 部署代码

在本地目录中批量替换 IP，之后通过 `scp` 或者其他方式上传到服务器

`clone` 本仓库到 `/home/diinki/` 目录下，找到 `deploy/test/app/docker-compose-mid.yml`，通过下面的命令进行容器编排

```shell
$ docker compose -p nexus-stack -f docker-compose-mid.yml up -d
```

部署了以下容器

* `Redis`，映射主机 5379 端口，默认认证密码：`bite@123`，容器名：`frameworkjava-redis`
* `MySQL`，映射主机 3306 端口，默认用户：`bitedev`，密码：`bite@123` （MYSQL_ROOT_PASSWORD=bite@123）
* `Nacos`，映射主机 8848、9848 端口，默认用户 `nacos`，密码 `bite@123`
* `Milvus`，向量数据库。默认用户 `root`，默认密码 `minioadmin`，WebUI 访问 `http://localhost:9091/webui`，或者通过 Attu 客户端。创建数据库 `wispcode_db`，创建 Collection `RAG`（对应表）
* `bge-m3-embedding`，因为网络环境原因，直接从 HuggingFace 获取嵌入模型不稳定，所以通过 model_downlaod.py 下载，容器编排自动完成挂载，容器位置位于 `/data`
* `wispcode-userapp-preview`，预览容器，包含 nginx + JDK

### 2.2 准备后续所需镜像

1. `codercom/code-server:4.137.0`，用于后续为用户提供网页端 Code 代码修改
2. 制作容器，主要后端服务容器需要提供 jdk、npm、mvn，没有现成的镜像，手动制作（虽然也上传了 DockerHub，但是考虑网络环境，选取自行制作。直接仓库拉取：`docker pull wangjialelele/jdk21-mvn-npm:v1.0`

### 2.3 配置 Nacos 配置

打开 Nacos 浏览器管理界面，更改如下配置：

* `share-wispcode-test.yaml`，更改 API-KEY，以及嵌入模型服务器、向量数据库、预览容器、docker 服务器、code-server 容器的 IP 地址，Gitee Access-Token
* `share-mysql-test.yaml`，更改 MySQL 服务器 IP，数据库名称不需更改

### 2.4 后端服务配置更改

#### 2.4.2 Docker Server IP

后端服务使用 Docker Maven Plugin 进行服务打包部署，在根 pom.xml 中，修改 Docker Server IP

#### 2.4.3 Nacos Server

同样在根 pom.xml 中更新 Nacos Server IP

