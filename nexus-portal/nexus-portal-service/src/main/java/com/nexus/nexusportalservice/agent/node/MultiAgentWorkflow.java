package com.nexus.nexusportalservice.agent.node;

import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.github.dockerjava.api.DockerClient;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IGiteeService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.HashMap;
import java.util.Map;

public class MultiAgentWorkflow {
    private AppMapper appMapper;
    private ChatClient chatClient;
    private VectorStore vectorStore;
    private DockerClient dockerClient;
    private String containerName;
    private String previewHost;
    private IGiteeService giteeService;

    //节点名称
    private static final String NODE_APP_GENERATION = "appGeneration"; //负责应用生成
    private static final String NODE_BUILD_PREVIEW = "buildPreview";   // 负责构建预览
    private static final String NODE_ERROR_FIX = "errorFix";           //负责构建出错修复
    private static final String NODE_APP_SCREENSHOT = "appScreenshot"; //负责制作镜像截图
    private static final String NODE_CODE_COMMIT = "codeCommit";       //负责代码提交 gitee

    public MultiAgentWorkflow(
            AppMapper appMapper, ChatClient chatClient,
            VectorStore vectorStore, DockerClient dockerClient,
            String containerName, String previewHost,
            IGiteeService giteeService
    ){
        this.appMapper = appMapper;
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.dockerClient = dockerClient;
        this.containerName = containerName;
        this.previewHost = previewHost;
        this.giteeService = giteeService;
    }

    private static Map<String, KeyStrategy> createKeyStrategies() {
        Map<String, KeyStrategy> keyStrategies = new HashMap<>();

        // 输入参数
        keyStrategies.put("appId", new ReplaceStrategy());
        keyStrategies.put("appDoc", new ReplaceStrategy());

        // AppGenerationAgent 输出
        keyStrategies.put("appGenerated", new ReplaceStrategy()); //true/false，是否生成成功
        keyStrategies.put("appPath", new ReplaceStrategy());
        keyStrategies.put("appType", new ReplaceStrategy());
        keyStrategies.put("files", new ReplaceStrategy());        //源文件，提供给 Gitee Agent 使用

        // BuildPreviewAgent 输出
        keyStrategies.put("buildPreview", new ReplaceStrategy()); // 是否成功生成预览
        keyStrategies.put("previewUrl", new ReplaceStrategy());
        keyStrategies.put("errorType", new ReplaceStrategy());    //错误类型

        //ErrorFixAgent 输出
        keyStrategies.put("fixSuccess", new ReplaceStrategy());   //是否修复成功

        // AppScreenshotAgent 输出
        keyStrategies.put("appScreenshot", new ReplaceStrategy());

        // CodeCommitAgent 输出
        keyStrategies.put("codeCommit", new ReplaceStrategy());

        // 通用状态键
        keyStrategies.put("status", new ReplaceStrategy()); //生成状态（不断更新）
        keyStrategies.put("error", new AppendStrategy()); //错误内容（不断更新）

        return keyStrategies;
    }
}
