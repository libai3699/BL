package com.common.rabbitmq.config;

import com.common.rabbitmq.service.RabbitMqTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 19:33
 */
@Configuration
public class RabbitConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Bean
    public RabbitMqTemplate rabbitMqTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMqTemplate rabbitMqTemplate = new RabbitMqTemplate();
        rabbitMqTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMqTemplate;
    }
}



