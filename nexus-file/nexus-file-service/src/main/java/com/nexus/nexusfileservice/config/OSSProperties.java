package com.nexus.nexusfileservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS 配置信息，从nacos读取，地域相关信息均为华东2（上海）
 */
@Slf4j
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "oss")
@ConditionalOnProperty(value = "storage.type", havingValue = "oss")
public class OSSProperties  {

    /**
     * oss是否内网上传
     */
    private Boolean internal; // 默认为 false

    /**
     * oss的endpoint，oss-cn-shanghai.aliyuncs.com
     */
    private String endpoint;

    /**
     * oss的endpoint的内部地址，oss-cn-shanghai-internal.aliyuncs.com
     */
    private String intEndpoint;

    /**
     * 地域的代码，cn-shanghai
     */
    private String region;

    /**
     * ak，环境变量
     */
    private String accessKeyId;

    /**
     * sk，环境变量
     */
    private String accessKeySecret;

    /**
     *存储桶，默认使用 nexus-stack-bucket
     */
    private String bucketName;

    /**
     * 路径前缀，加在 endPoint 之后
     */
    private String pathPrefix;

    private Integer expre;

    private Integer minLen;

    private Integer maxLen;


    /**
     * 获取访问URL
     *
     * @return url信息
     */
    public String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 获取内部访问URL
     *
     * @return 内部访问URL
     */
    public String getInternalBaseUrl() {
        return "http://" + bucketName + "." + intEndpoint + "/";
    }

}
