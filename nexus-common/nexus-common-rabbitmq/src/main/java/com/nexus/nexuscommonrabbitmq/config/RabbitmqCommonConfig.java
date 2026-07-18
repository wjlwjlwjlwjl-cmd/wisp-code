package com.nexus.nexuscommonrabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqCommonConfig {
    @Bean("testQueue")
    public Queue workQueue(){
        return QueueBuilder.durable("testQueue").build();
    }

    /**
     * RabbitMQ 序列化配置
     * 
     * @return
     */
    @Bean
    public MessageConverter jsonToMapMessageConverter(){
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        return jackson2JsonMessageConverter;
    }
}
