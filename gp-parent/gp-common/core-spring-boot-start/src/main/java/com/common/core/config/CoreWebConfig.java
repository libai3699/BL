package com.common.core.config;

import com.common.core.handler.ExtendedRequestMappingHandlerAdapter;
import com.common.core.handler.MvcInterceptor;
import com.gp.common.base.utils.MessagesUtils;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 对web请求进行拦截处理
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 11:19
 */
@Configuration
public class CoreWebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MvcInterceptor())
                .addPathPatterns("/**");
    }
    @Bean
    public MessagesUtils messagesUtils() {
        return new MessagesUtils();
    }

    @Bean
    public WebMvcRegistrations mvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
                return new ExtendedRequestMappingHandlerAdapter();
            }
        };
    }
}
