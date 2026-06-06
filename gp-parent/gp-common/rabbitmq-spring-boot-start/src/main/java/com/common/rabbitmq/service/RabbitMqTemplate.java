package com.common.rabbitmq.service;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.common.rabbitmq.model.MessageBody;
import com.gp.common.base.utils.LogUtil;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageBuilderSupport;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import javax.annotation.PostConstruct;
import java.util.Map;


/**
 * @author axing
 */
@Data
public class RabbitMqTemplate {

    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void dispatcherInit() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 当前消息的唯一关联数据（这个消息的唯一id）
             * @param b 消息是否成功收到
             * @param s  失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("confirm...correlationData："+correlationData+"ack:==="+b+"s====>"+s);
            }
        });

        //设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("message====>"+message+"replyCode====>"+replyCode+"replyText=====>"
                    +replyText+"exchange=====>"+exchange+ "routingKey====>"+routingKey);
        });
    }


    public void sendMq(String mqExchange, String mqKey, MessageBody messageBody, Map<String, Object> headers) {
        try {
            messageBody.setMessageId(IdUtil.fastSimpleUUID());
            messageBody.setTimestamp(System.currentTimeMillis());
            LogUtil.info(String.format("推送MQ消息=> mqExchange: {%s}, mqKey: {%s}", mqExchange, mqKey));
            MessageBuilderSupport<Message> messageBuilderSupport = MessageBuilder
                    .withBody(JSON.toJSONBytes(messageBody));
//                    .setContentEncoding(MediaType.APPLICATION_JSON_VALUE);
            if (MapUtil.isNotEmpty(headers)) {
                headers.forEach(messageBuilderSupport::setHeader);
            }
            rabbitTemplate.send(mqExchange, mqKey, messageBuilderSupport.build());

        } catch (Exception ex) {
            LogUtil.error(ex, "send MQ message is fail!");
        }
    }
    public void sendMessageWithExpiration(String mqExchange, String mqKey, MessageBody messageBody, long expiration) {
        messageBody.setMessageId(IdUtil.fastSimpleUUID());
        messageBody.setTimestamp(System.currentTimeMillis());
        LogUtil.info(String.format("推送MQ消息=> mqExchange: {%s}, mqKey: {%s}", mqExchange, mqKey));
        MessageBuilderSupport<Message> messageBuilderSupport = MessageBuilder
                .withBody(JSON.toJSONBytes(messageBody));
        rabbitTemplate.convertAndSend(mqExchange, mqKey, messageBuilderSupport.build(), message -> {
            message.getMessageProperties().setHeader("x-delay",expiration);
            return message;
        });
    }

    public void sendMq(String mqExchange, String mqKey, MessageBody messageBody) {
        sendMq(mqExchange,mqKey,messageBody,null);
    }
}
