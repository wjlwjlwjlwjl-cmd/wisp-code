package com.nexus.nexuscommonredis.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings({"rawtypes", "null"})
public class RedissonService {
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * Redis 操作客户端
     */
    private RedissonClient redissonClient;

    /**
     * 获取分布式锁
     * 
     * @param lockKey       锁的唯一标识，建议：模块名 + 服务名 + 唯一键
     * @param expire        过期时间，单位是毫秒
     * @return              分布式锁
     */
    public RLock aquire(String lockKey, long expire){
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(expire, TimeUnit.MILLISECONDS);
        return lock;
    }

    /**
     * 释放锁
     * 
     * @param lock          锁实例
     * @return              解锁是否成功（是否线程对应的释放锁）
     */
    public boolean releaseLock(RLock lock){
        if(lock.isHeldByCurrentThread()){
            lock.unlock();
            return true;
        }
        return false;
    }
}
