package com.nexus.nexusmstemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexuscommonredis.service.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/test/redis")
public class TestRedis {
    @Autowired
    private RedisService redisService;

    @PostMapping("/kv/expire1")
    public void expire1(){
        redisService.expire("key1", 5);
    }

    @PostMapping("/kv/set")
    public void set(String key, String val){
    }

    @GetMapping("/kv/get")
    public String get(String key){
        String val = redisService.getCacheObject(key, String.class);
        if(val == null){
            log.warn("val == null");
        }
        return val;
    }
}
