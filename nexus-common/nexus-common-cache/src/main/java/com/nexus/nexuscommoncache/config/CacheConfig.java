package com.nexus.nexuscommoncache.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, Object> getCacheObject(){
        return Caffeine.newBuilder()
        .initialCapacity(5)
        .maximumSize(10)
        .expireAfterWrite(90L, TimeUnit.SECONDS)
        .build();
    }
}
