package com.nexus.nexusmstemplate.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nexus.nexusmstemplate.domain.MessageDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = "testQueue")
    public void listenerQueue(MessageDTO messageDTO){
        log.info("收到消息：" + messageDTO + "\n" + MessageDTO.class);
    }
}
