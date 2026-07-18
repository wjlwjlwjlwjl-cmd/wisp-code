package com.nexus.nexusmstemplate.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nexus.nexusmstemplate.domain.MessageDTO;

@Component
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void produceMsg(MessageDTO MessageDTO){
        rabbitTemplate.convertAndSend("testQueue", MessageDTO);
    }
}
