package com.nexus.nexusmstemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusmstemplate.service.TestThreadService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/test/thread")
@Slf4j
public class TestThread {
    @Autowired
    TestThreadService testThreadService;

    @RequestMapping("/controller")
    public void controller(){
        log.info("Controller's thread service: {}", Thread.currentThread().getName());
        testThreadService.test();
    }
}
