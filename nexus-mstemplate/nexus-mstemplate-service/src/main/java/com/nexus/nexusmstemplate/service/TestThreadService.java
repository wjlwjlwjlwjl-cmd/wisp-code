package com.nexus.nexusmstemplate.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestThreadService {
    @Async
    public void test(){
        log.info("Service's thread name: {}", Thread.currentThread().getName());
    }
}
