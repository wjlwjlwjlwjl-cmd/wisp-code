package com.nexus.nexusgateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 白名单配置
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")    //批量将配置文件中的属性映射到实体类
public class IgnoreWhiteProperties {

    /**
     * 放行白名单
     */
    private List<String> whites;
}
