package com.nexus.nexusportalservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="gitee")
public class GiteeConfig {
    private String apiBaseUrl = "https://gitee.com/api/v5/"; //前置路径
    private String accessToken = "f3044cf9989512905d2e14d7b061be4d"; //验证令牌
}
