package com.gp.common.mybatisplus.config;

import com.common.core.config.JedisBeanConfig;
import com.gp.common.mybatisplus.pay.service.PayService;
import com.common.core.prop.ProxyProp;
import com.common.core.util.RedisUtil;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 */
@AutoConfigureAfter({JedisBeanConfig.class})
@EnableConfigurationProperties({ProxyProp.class})
@Configuration
public class PayConfig {
    @Bean
    public PayService payService(RedisUtil redisUtil, ProxyProp proxyProp) {
        PayService payService = new PayService();
        payService.setRedisUtil(redisUtil);
        PayService.proxyProp = proxyProp;
        return payService;
    }

}
