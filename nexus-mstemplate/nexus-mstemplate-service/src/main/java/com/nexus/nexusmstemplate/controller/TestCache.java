package com.nexus.nexusmstemplate.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Cache;
import com.nexus.nexuscommoncache.util.CacheUtil;
import com.nexus.nexuscommonredis.service.RedisService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test/cache")
@Slf4j
public class TestCache {
    @Autowired
    Cache<String, Object> cache;
    @Autowired
    CacheUtil util;
    @Autowired
    RedisService redisService;

    @RequestMapping("/put")
    public void put(String key, String val){
        util.setL2Cache(key, val, cache, redisService, 1000, TimeUnit.SECONDS);
    }

    @RequestMapping("get")
    public void get(String key){
        Integer ret = util.getCache(key, new TypeReference<Integer>() {}, cache, redisService);
        log.info(ret + "");
        if(ret != null){
            return;
        }
        log.info("从数据库中获取数据，并插入两级缓存");
        util.setL2Cache(key, 10, cache, redisService, 1000, TimeUnit.SECONDS);
    }
}
