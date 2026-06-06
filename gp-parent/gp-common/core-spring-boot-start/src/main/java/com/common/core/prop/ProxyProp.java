package com.common.core.prop;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * TxSmsConfig config
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RefreshScope
@ConfigurationProperties(prefix = "proxy")
public class ProxyProp {

    private String host;
    private Integer port;

    private String host2;
    private Integer port2;


}
