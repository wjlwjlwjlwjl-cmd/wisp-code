package com.nexus.nexusportalservice.task;

import com.nexus.nexuscommonredis.service.RedisService;
import com.nexus.nexusportalservice.constants.ContainerConstants;
import com.nexus.nexusportalservice.utils.ContainerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CodeServerCleanupTask {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ContainerUtil containerUtil;

    @Scheduled(fixedDelay = 60_000)
    public void cleanup(){
        redisService.scan(ContainerConstants.CONTAINER_PREFIX + "*", 100, (key)->{
            String containerId = redisService.getCacheObject(key, String.class);
            if(!containerUtil.containerExists(containerId)){
                redisService.deleteObject(key);
            }
        });
    }
}
