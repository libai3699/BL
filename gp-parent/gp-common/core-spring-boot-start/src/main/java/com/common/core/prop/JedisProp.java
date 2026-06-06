package com.common.core.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@ConfigurationProperties(prefix = "redis")
public class JedisProp {

    /**
     * 集群还是单例
     */
    private Boolean isCluster = false;

    /**
     * 是否开启ssl
     */
    private boolean ssl = false;

    /**
     * 集群节点
     */
    private List<Cluster> clusters;

    /**
     * 单机的ip
     */
    private String host = "127.0.0.1";

    /**
     * 单机的端口
     */
    private Integer port = 6379;
    /**
     * 密码
     */
    private String pass;
    /**
     * 指定的redis库
     */
    private Integer database = 0;

    /**
     * 最大线程数
     */
    private Integer maxTotal = 100;

    /**
     * 空闲线程数
     */
    private Integer maxIdle = 50;

    /**
     * 最大等待时间
     */
    private Integer maxWaitMillis = 3000;

    /**
     * 是否测试浏览
     */
    private Boolean testOnBorrow = true;

    /**
     * 是否测试返回
     */
    private Boolean testOnReturn = false;

    /**
     * 检查空闲链接
     */
    private Boolean testWhileIdle  = true;

    @Data
    public static class Cluster {
        /**
         * 节点ip
         */
        private String ip;
        /**
         * 节点端口
         */
        private Integer port;
    }
}
