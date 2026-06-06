package com.common.datasource.config;

import com.common.datasource.interceptor.CecuDecryptHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 对web请求进行拦截处理
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 11:19
 */
@Configuration
@ConditionalOnBean(CecuDecryptHandlerInterceptor.class)
public class CecuDecryptWebConfig implements WebMvcConfigurer {

    @Resource
    private CecuDecryptHandlerInterceptor cecuDecryptHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cecuDecryptHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/**/*.*");
    }
}
