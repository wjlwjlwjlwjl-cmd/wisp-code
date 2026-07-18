package com.nexus.nexusadminservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.nexus")
public class AdminService {
    public static void main(String[] args){
        SpringApplication.run(AdminService.class, args);
        log.info("管理服务启动成功");
    }
}
