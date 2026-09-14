package com.nexus.nexusportalservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Gitee 相关配置（前缀 gitee）。使用 @ConfigurationProperties 走宽松绑定，
 * 因此 gitee.access-token / gitee.accessToken / gitee.accesstoken 均可映射到 accessToken，
 * 避免 @Value 无法做 kebab/camel 宽松匹配而读不到值的问题；同时支持 Nacos 配置热刷新。
 */
@Data
@Component
@ConfigurationProperties(prefix = "gitee")
public class GiteeProperties {

    private String apiBaseUrl = "https://gitee.com/api/v5/";

    private String accessToken;

    private UserCode userCode = new UserCode();

    @Data
    public static class UserCode {
        private String owner;
        private String repo;
        private String branch;
    }
}
