package com.nexus.nexusmstemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexusmstemplate.domain.MessageDTO;
import com.nexus.nexusmstemplate.rabbit.Producer;

@RestController
@RequestMapping("/test/rabbit")
public class TestRabbit {
    @Autowired
    Producer producer;

    @RequestMapping("/produce")
    public void produce(String type, String msg){
        MessageDTO messageDTO = new MessageDTO(type, msg);
        producer.produceMsg(messageDTO);
    }
}
