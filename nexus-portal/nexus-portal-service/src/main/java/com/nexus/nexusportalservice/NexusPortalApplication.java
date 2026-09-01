package com.nexus.nexusportalservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NexusPortalApplication {
    public static void main(String[] args){
        SpringApplication.run(NexusPortalApplication.class, args);
        log.info("wisp-code start successfully");
    }
}