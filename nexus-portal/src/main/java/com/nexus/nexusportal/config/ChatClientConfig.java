package com.nexus.nexusportal.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

@SuppressWarnings("null")
@Configuration
public class ChatClientConfig {
    private final ChatClient.Builder builder;

    public ChatClientConfig(ChatClient.Builder builder){
        this.builder = builder;
    }

    @Bean("chatClient")
    public ChatClient getChatClient(){
        return builder
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .defaultOptions(DashScopeChatOptions
                .builder()
                .topP(0.7)
                .build()) //topP是将所有可能token从高到底排序后，可能性从高到低加到topP后只在这些范围里随机选，相比 temperature 直接过滤掉冷门词
            .build();
    }
}
