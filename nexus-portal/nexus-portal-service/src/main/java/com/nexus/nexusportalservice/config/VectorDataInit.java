package com.nexus.nexusportalservice.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@Configuration
public class VectorDataInit implements ApplicationRunner {

    @Autowired
    private MilvusVectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Document> documents = List.of(
                new Document("""
一键应用生成平台：平台概述。本平台是一键应用生成平台，支持AI生成应用代码，通过Docker容器提供实时预览能力。一共支持三种应用类型：1.纯前端HTML应用；2.纯前端Vue应用；3.前后端结合SpringMVC+Vue应用。用户描述需求，AI生成对应项目文件结构，平台构建Docker实例，对外暴露端口实现预览访问。所有预览环境为隔离Docker容器，每个生成应用独立容器，用完可销毁。
"""),
                new Document("""
应用类型1：纯前端HTML应用。技术栈：原生HTML、CSS、JavaScript，无后端服务。项目结构：包含index.html，css目录，js目录，静态资源assets。构建预览方式：使用nginx docker镜像，将静态文件复制到nginx的html目录，对外暴露80端口。不需要Java后端，不需要编译步骤，直接部署静态资源。适合简单页面、活动页、展示页面、简单交互网页。
"""),
                new Document("""
应用类型2：纯前端Vue应用。技术栈 Vue3 + Vite。项目结构：标准vite‑vue项目，package.json，src目录，main.js，App.vue，components组件目录，router路由目录，public静态资源。构建预览流程：Docker内执行npm install、npm run build，产出dist静态产物；使用nginx承载dist静态文件对外提供预览访问。没有Java后端，全部逻辑在浏览器前端完成。适合中后台页面、SPA单页应用。
"""),
                new Document("""
应用类型3：前后端 SpringMVC + Vue 组合应用。后端：Spring Boot SpringMVC，Java，提供RESTful接口；前端：Vue3 Vite SPA。项目分为两个子模块：backend SpringBoot工程、frontend Vue工程。构建预览流程：Docker多阶段构建。后端：Maven打包SpringBoot jar包；前端：npm build产出dist静态资源；将dist静态资源放入SpringBoot resources/static目录，由SpringBoot同时提供前端静态页面与后端API接口；容器启动SpringBoot jar，对外暴露服务端口，完成预览。前端页面访问后端接口使用相对路径，避免跨域问题。适合完整业务系统，需要数据库、接口、业务逻辑的场景。
"""),
                new Document("""
Docker预览通用规则。每一次生成应用，创建独立Docker容器做预览；容器之间环境隔离。平台负责生成Dockerfile；容器启动后分配端口，用户通过http访问预览页面。预览容器生命周期可控，可以启动、停止、销毁。文件全部由AI生成输出，包含完整项目文件列表、Dockerfile文件。禁止用户手动修改宿主机文件，全部运行于容器内部。
""")
        );
        vectorStore.add(documents);
        log.info("vectorStore initialized，一共加载 {} 条平台知识库文档", documents.size());
    }
}
