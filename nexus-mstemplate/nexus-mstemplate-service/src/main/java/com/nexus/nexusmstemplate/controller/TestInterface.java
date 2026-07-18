package com.nexus.nexusmstemplate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test/interface")
@Slf4j
public class TestInterface {
    @GetMapping("/info")
    public void info(){
        log.info("接口调用成功");
    }

    @RequestMapping("/type")
    public void type(String str){
        log.info("接口调用成功");
    }
}
