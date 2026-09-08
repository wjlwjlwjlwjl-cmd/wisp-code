package com.wjl.config;

import com.wjl.service.GiteeService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 注册 Mcp Server
@Configuration
public class GiteeMcpServerConfig {
    @Bean
    ToolCallbackProvider mcpConfig(GiteeService giteeService){
        return MethodToolCallbackProvider.builder().toolObjects(giteeService).build();
    }
}