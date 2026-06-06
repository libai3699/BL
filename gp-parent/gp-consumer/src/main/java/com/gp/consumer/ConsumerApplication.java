package com.gp.consumer;

//import de.codecentric.boot.admin.server.config.EnableAdminServer;
import io.netty.channel.DefaultChannelId;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
//@EnableAdminServer
//@ComponentScan(basePackages = {"com.gp"}, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {
//        com.gp.common.rabbitmq.config.RabbitConfig.class
//}
//))
//@EnableAsync
@EnableFeignClients(basePackages = {"com.gp.feign.api"})
@ComponentScan(basePackages = {"com.gp"})
@SpringCloudApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
