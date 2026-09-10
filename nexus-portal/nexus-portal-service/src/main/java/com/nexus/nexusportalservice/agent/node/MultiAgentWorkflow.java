package com.nexus.nexusportalservice.agent.node;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.github.dockerjava.api.DockerClient;
import com.nexus.nexuscommondomain.exception.ServiceException;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IGiteeService;
import com.nexus.nexusportalservice.service.impl.GiteeServiceImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MultiAgentWorkflow {
    private StateGraph stateGraph;
    private AppMapper appMapper;
    private ChatClient chatClient;
    private DockerClient dockerClient;
    private String containerName;
    private String previewHost;
    private GiteeServiceImpl giteeService;

    //节点名称
    private static final String NODE_APP_GENERATION = "appGeneration"; //负责应用生成
    private static final String NODE_BUILD_PREVIEW = "buildPreview";   // 负责构建预览
    private static final String NODE_ERROR_FIXING = "errorFixing";           //负责构建出错修复
    private static final String NODE_GITEE_COMMIT = "giteeCommit";       //负责代码提交 gitee

    public MultiAgentWorkflow(
            AppMapper appMapper, ChatClient chatClient,
            DockerClient dockerClient, String containerName,
            String previewHost, GiteeServiceImpl giteeService
    ){
        stateGraph = new StateGraph(createKeyStrategyFactory());
        this.appMapper = appMapper;
        this.chatClient = chatClient;
        this.dockerClient = dockerClient;
        this.containerName = containerName;
        this.previewHost = previewHost;
        this.giteeService = giteeService;

        addNodes();
        addEdges();
    }

    private void addNodes(){
        try{
            stateGraph.addNode(NODE_APP_GENERATION, AsyncNodeAction.node_async(new AppGenerationAgent(appMapper, chatClient)));
            stateGraph.addNode(NODE_BUILD_PREVIEW, AsyncNodeAction.node_async(new BuildPreviewAgent(appMapper, dockerClient, containerName, previewHost)));
            stateGraph.addNode(NODE_ERROR_FIXING, AsyncNodeAction.node_async(new ErrorFixingAgent(chatClient)));
            stateGraph.addNode(NODE_GITEE_COMMIT, AsyncNodeAction.node_async(new GiteeCommitAgent(giteeService)));
        }
        catch(GraphStateException e){
            System.out.println(e.getMessage());
        }
    }

    private void addEdges(){
        try{
            stateGraph.addEdge(StateGraph.START, NODE_APP_GENERATION); //先生成应用代码
            stateGraph.addEdge(NODE_APP_GENERATION, NODE_BUILD_PREVIEW); //先生成应用代码
            stateGraph.addConditionalEdges(NODE_BUILD_PREVIEW, state -> {
                Boolean buildPreview = state.value("buildPreview", Boolean.class).orElse(null);
                if(buildPreview){
                    return CompletableFuture.completedFuture("SUCCESS");
                }
                return CompletableFuture.completedFuture("ERROR");
            }, Map.of("SUCCESS", NODE_GITEE_COMMIT, "ERROR", NODE_ERROR_FIXING));

            stateGraph.addConditionalEdges(NODE_ERROR_FIXING, state -> {
                Boolean fixSuccess = state.value("fixSuccess", Boolean.class).orElse(null);
                if(fixSuccess){
                    return CompletableFuture.completedFuture("SUCCESS");
                }
                return CompletableFuture.completedFuture("ERROR");

            }, Map.of("SUCCESS", NODE_BUILD_PREVIEW, "ERROR", StateGraph.END));

            stateGraph.addEdge(NODE_GITEE_COMMIT, StateGraph.END);
        }
        catch(GraphStateException e){
            System.out.println(e.getMessage());
        }
    }

    public OverAllState execute(OverAllState overAllState) throws Exception{
        CompiledGraph compiledGraph = stateGraph.compile();
        RunnableConfig runnableConfig = RunnableConfig.builder().build();
        overAllState.registerKeyAndStrategy(createKeyStrategies());
        return compiledGraph.invoke(overAllState, runnableConfig).orElseThrow(() -> new ServiceException("StateGraph Running failed"));
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
        keyStrategies.put("codeCommit", new ReplaceStrategy()); //推送 gitee 是否成功

        // 通用状态键
        keyStrategies.put("status", new ReplaceStrategy()); //生成状态（不断更新）
        keyStrategies.put("error", new AppendStrategy()); //错误内容，叠加输出（不断更新）

        return keyStrategies;
    }

    private KeyStrategyFactory createKeyStrategyFactory(){
        return KeyStrategy.builder().addStrategies(createKeyStrategies()).build();
    }
}
