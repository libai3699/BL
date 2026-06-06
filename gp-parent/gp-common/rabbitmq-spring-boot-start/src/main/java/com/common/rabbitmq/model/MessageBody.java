package com.common.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * mq消息的封装类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBody<T> implements Serializable {

    private static final Long serialVersionUID = 1L;

    /**
     * 消息类型 1 正常数据
     */
    private int messageType = 1;

    /**
     * 数据
     */
    private T data;
    /**
     * 消息类型
     */
    private String type = "";
    /**
     * markupType 类型
     */
    private String markupType = "";

    /**
     * botUsername
     */
    private String botUsername = "";

    /**
     * productId 类型
     */
    private String productId = "";
    /**
     * 返回码
     */
    private int code = 200;

    /**
     * 消息时间戳
     */
    private long timestamp;

    /**
     * 消息id 数据源消息到下游一路透传跟踪
     */
    private String messageId;

    /**扩展字段*/
    private Object extend1;
    private Object extend2;
    private Object extend3;
    private Object extend4;
    private Object extend5;
}
