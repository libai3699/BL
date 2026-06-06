package com.common.datasource.config;

import com.common.datasource.interceptor.CecuDecryptHandlerInterceptor;
import com.common.datasource.param.CecuProp;
import com.common.datasource.util.CecuUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 11:19
 */
@Configuration
@EnableConfigurationProperties(CecuProp.class)
public class CecuConfig{

    @Bean
    @ConditionalOnProperty(prefix = "dynamic",name = "cecu-switch",havingValue = "true")
    public CecuUtil cecuUtil(CecuProp cecuProp) {
        CecuUtil cecuUtil = new CecuUtil();
        cecuUtil.setCecuProp(cecuProp);
        return cecuUtil;

    }

    @Bean
    @ConditionalOnProperty(prefix = "dynamic",name = "cecu-switch",havingValue = "true")
    public CecuDecryptHandlerInterceptor cecuDecryptHandlerInterceptor(CecuProp cecuProp, CecuUtil cecuUtil) {
        CecuDecryptHandlerInterceptor handlerInterceptor = new CecuDecryptHandlerInterceptor();
        handlerInterceptor.setCecuProp(cecuProp);
        handlerInterceptor.setCecuUtil(cecuUtil);
        return handlerInterceptor;
    }


}
