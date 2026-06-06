package com.common.core.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * restTemplate参数配置
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@ConfigurationProperties(prefix = "rest")
@Data
public class RestTemplateProp {

    /**
     * 是否开启代理
     */
    private Boolean proxyEnable = false;

    /**
     * 代理地址
     */
    private String proxyUri;
    /**
     * 代理ip
     */
    private Integer proxyPort;

    /**
     * 请求超时时间
     */
    private int requestTimeout = 20000;

    /**
     * 连接超时时间
     */
    private int cTimeout = 20000;

    /**
     * 套接字读取超时时间
     */
    private int readTimeout = 20000;
}
