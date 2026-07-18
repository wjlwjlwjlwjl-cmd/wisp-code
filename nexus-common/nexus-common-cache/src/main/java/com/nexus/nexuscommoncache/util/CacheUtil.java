package com.nexus.nexuscommoncache.util;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.nexus.nexuscommonredis.service.RedisService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * CacheUtil 本地缓存，使用 Caffine 作为一级缓存，Redis 作为二级缓存（可选），最后是 Mysql 作为持久化数据库
 */
@Component
@SuppressWarnings({"unchecked"})
@Slf4j
public class CacheUtil {
    public static<T> T getCache(String key, TypeReference<T> reference, Cache<String, Object> caffeineCache, RedisService redisService){
        T ret = (T)caffeineCache.getIfPresent(key);
        if(ret != null){
            log.info("从一级缓存中获取到数据");
            return ret;     //从一级缓存中获取到数据
        }
        log.info("一级缓存中没有数据");

        if(reference.getType() == String.class){
            ret = (T)redisService.getCacheObject(key, String.class);    
            caffeineCache.put(key, ret);
            log.info("从二级缓存中获取到数据");
            return ret;
        }
        ret = redisService.getCacheObject(key, reference); //从二级缓存中获取数据
        if(ret != null){
            caffeineCache.put(key, ret);
            log.info("从二级缓存中获取到数据");
            return ret;
        }
        log.info("二级缓存中没有数据");
        return null;        //未获取到数据，返回空
    }

    /**
     * 将数据同时放进一级缓存、二级缓存
     * 
     * @param <T>               缓存数据类型
     * @param key               缓存数据对应键
     * @param value             缓存数据
     * @param caffeineCache     缓存操作对象
     * @param expire            数据过期时间（Redis）
     * @param unit              时间单位
     */
    public static<T> void setL2Cache(String key, T value, Cache<String, Object> caffeineCache, RedisService redisService, long expire, TimeUnit unit){
        caffeineCache.put(key, value);
        log.info("更新一级、二级缓存数据");
        redisService.setCacheObject(key, value, expire, unit);
    }
}
