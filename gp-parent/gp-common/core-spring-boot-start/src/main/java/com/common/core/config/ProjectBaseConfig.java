package com.common.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.byteplus.service.sms.impl.SmsServiceImpl;
import com.common.core.filter.JsonXssFilter;
import com.common.core.mail.MailService;
import com.common.core.prop.SmsAlProp;
import com.common.core.prop.SmsByteProp;
import com.common.core.prop.SmsTxProp;
import com.common.core.prop.SwaggerProp;
import com.common.core.service.BigDecimalSerialize;
import com.common.core.sms.SmsService;
import com.common.core.util.CTransactionTemplate;
import com.common.core.util.RedisUtil;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Properties;

@EnableConfigurationProperties({MailProperties.class, SmsAlProp.class, SmsTxProp.class, SmsByteProp.class, SwaggerProp.class})
@Configuration
public class ProjectBaseConfig{
    // 当前跨域请求最大有效时长。这里默认1天
    private static final long MAX_AGE = 24 * 60 * 60;
	@Bean
    public EnvironmentAware environmentAware() {
        return environment -> {
            if(StrUtil.isBlank(System.getProperty("project.name"))){
                System.setProperty("project.name",SpringUtil.getApplicationName());
            }
        };
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/messages");
        return messageSource;
    }

    /**
     * 跨域
     * @return
     */
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.setMaxAge(MAX_AGE);
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return new CorsFilter(source);
//    }


    @Bean
    public WebServerFactoryCustomizer webServerFactoryCustomizer() {
        return (WebServerFactoryCustomizer<UndertowServletWebServerFactory>) factory -> factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });
    }

    @Bean
    public CTransactionTemplate cTransactionTemplate(TransactionTemplate transactionTemplate) {
        CTransactionTemplate cTransactionTemplate = new CTransactionTemplate();
        cTransactionTemplate.setTransactionTemplate(transactionTemplate);
        return cTransactionTemplate;
    }



    @Bean
    public MailService mailService(JavaMailSender mailSender, RedisUtil redisUtil, MailProperties mailProperties) {
        MailService mailService = new MailService();
        mailService.setJavaMailSender(mailSender);
        mailService.setRedisUtil(redisUtil);
        mailService.setSender(mailProperties.getUsername());
        return mailService;
    }



    @SneakyThrows
    @Bean
    public SmsService smsService(RedisUtil redisUtil, SmsAlProp smsAlProp, SmsTxProp smsTxProp, SmsByteProp smsByteProp) {
        SmsService smsService = new SmsService();
        smsService.setRedisUtil(redisUtil);
        smsService.setSmsAlProp(smsAlProp);
        smsService.setSmsTxProp(smsTxProp);
        smsService.setSmsByteProp(smsByteProp);
        com.byteplus.service.sms.SmsService smsByteService = SmsServiceImpl.getInstance("ap-singapore-1");
        smsByteService.setAccessKey(smsByteProp.getAccessKey());
        smsByteService.setSecretKey(smsByteProp.getSecretKey());
        smsService.setSmsByteService(smsByteService);
        return smsService;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, new BigDecimalSerialize());
        };
    }

    @Bean
    public FilterRegistrationBean<JsonXssFilter> xssFilter(SwaggerProp swaggerProp) {
        FilterRegistrationBean<JsonXssFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        JsonXssFilter jsonXssFilter = new JsonXssFilter();
        jsonXssFilter.setSwaggerProp(swaggerProp);
        filterRegistrationBean.setFilter(jsonXssFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setName("xssFilter");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}

