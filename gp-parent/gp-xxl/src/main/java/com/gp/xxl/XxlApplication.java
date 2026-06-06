package com.gp.xxl;

import com.gp.common.base.utils.SystemUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

//import org.springframework.cloud.netflix.hystrix.EnableHystrix;


@EnableFeignClients(basePackages = {"com.gp.feign.api"})
@ComponentScan(basePackages = {"com.gp"})
@SpringBootApplication
@SpringCloudApplication
//@EnableHystrix
//@EnableAsync
public class XxlApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlApplication.class, args);
        System.setProperty(SystemUtil.appStartupTime, String.valueOf(System.currentTimeMillis()));
        System.out.println("启动时间: " + SystemUtil.getAppStartupTime());
    }

}
