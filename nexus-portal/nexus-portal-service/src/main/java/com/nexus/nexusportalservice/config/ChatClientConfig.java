package com.nexus.nexusportalservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("null")
@Configuration
public class ChatClientConfig {
    @Autowired
    private ChatMemoryConfig chatMemoryConfig;

    @Autowired
    private MilvusVectorStore vectorStore;

    private final ChatClient.Builder builder;
    private List<ToolCallbackProvider> toolCallbackProviders;

    public ChatClientConfig(ChatClient.Builder builder, List<ToolCallbackProvider> toolCallbackProviders){
        this.toolCallbackProviders = toolCallbackProviders;
        this.builder = builder;
    }

    @Bean("chatClient")
    public ChatClient getChatClient(){
        //Memory
        PromptChatMemoryAdvisor messageChatMemoryAdvisor = PromptChatMemoryAdvisor.builder(chatMemoryConfig).build();

        //RAG
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor
                .builder(vectorStore)
                .searchRequest(SearchRequest.builder().build())
                .build();

        builder.defaultAdvisors(
                new SimpleLoggerAdvisor(), messageChatMemoryAdvisor, questionAnswerAdvisor
                )
                .defaultOptions(DashScopeChatOptions.builder().topP(0.7).build()); //topP是将所有可能token从高到底排序后，可能性从高到低加到topP后只在这些范围里随机选，相比 temperature 直接过滤掉冷门词

        //Tool Adding
        for(ToolCallbackProvider toolCallbackProvider: toolCallbackProviders){
            builder.defaultToolCallbacks(toolCallbackProvider);
        }

        return builder.build();
    }
}
