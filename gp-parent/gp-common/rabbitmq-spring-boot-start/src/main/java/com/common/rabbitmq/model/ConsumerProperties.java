package com.common.rabbitmq.model;

import lombok.Data;


/**
 * 消费者日志打印模板
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class ConsumerProperties {

    /**
     *  BROADCASTING
     *
     *  CLUSTERING 默认
     *
     */
    private String messageModel;

    /**
     *  0 CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，即跳过历史消息
     *  4 CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
     *  5 CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
     */
    private String consumeFromWhere;

    /**
     * 设置消费最小线程数 大小取值范围都是 [1, 1000]。
     */
    private Integer consumeThreadMin  ;

    /**
     * 设置消费最大线程数 大小取值范围都是 [1, 1000]。
     */
    private Integer consumeThreadMax ;

    /**
     * 单队列并行消费允许的最大跨度取值范围都是 [1, 65535]，默认是2000。
     * 这个参数只在并行消费的时候才起作用
     */
    private Integer consumeConcurrentlyMaxSpan  ;

    /**
     * 批量消费最大消息条数，取值范围: [1, 1024]。默认是1
     */
    private Integer consumeMessageBatchMaxSize;

    /**
     * 消费者去broker拉取消息时，一次拉取多少条。取值范围: [1, 1024]。默认是32 。可选配置
     */
    private Integer pullBatchSize ;


    /**
     * 检查拉取消息的间隔时间，由于是长轮询，所以为 0
     * 但是如果应用为了流控，也可以设置大于 0 的值，单位毫秒，取值范围: [0, 65535]
     */
    private Integer pullInterval;

}
