package com.nexus.nexusportalservice.agent.config;

import com.github.dockerjava.api.DockerClient;
import com.nexus.nexusportalservice.agent.node.MultiAgentWorkflow;
import com.nexus.nexusportalservice.mapper.AppMapper;
import com.nexus.nexusportalservice.service.IGiteeService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiAgentWorkFlowConfig {
    @Value("${app.preview.container-name}")
    private String containerName;

    @Value("${app.preview.host")
    private String previewHost;

    @Bean
    public MultiAgentWorkflow multiAgentWorkflow(AppMapper appMapper, ChatClient chatClient,
                                                 VectorStore vectorStore, DockerClient dockerClient,
                                                 IGiteeService giteeService) {
        return new MultiAgentWorkflow(
                appMapper,
                chatClient,
                vectorStore,
                dockerClient,
                containerName,
                previewHost,
                giteeService);
    }
}
