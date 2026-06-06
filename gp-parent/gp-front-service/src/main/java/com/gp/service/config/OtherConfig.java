package com.gp.service.config;

import com.gp.common.mybatisplus.service.UserBlackIpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2的接口配置
 *
 * @author ruoyi
 */
@Configuration
@EnableSwagger2
public class OtherConfig
{
   @Resource
   private UserBlackIpService userBlackIpService;
    @PostConstruct
    public void init() {
//        userBlackIpService.refreshUserBlackIp();
    }
}
