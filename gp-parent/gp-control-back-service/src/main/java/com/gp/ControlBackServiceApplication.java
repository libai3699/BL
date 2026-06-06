package com.gp;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@EnableFeignClients(basePackages = {"com.gp.feign.api"})
@ComponentScan(basePackages = {"com.gp"})
@SpringCloudApplication
public class ControlBackServiceApplication {

    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ControlBackServiceApplication.class, args);
    }
}
