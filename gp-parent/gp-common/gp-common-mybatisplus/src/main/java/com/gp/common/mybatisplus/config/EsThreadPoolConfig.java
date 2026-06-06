package com.gp.common.mybatisplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * 因spring自带的线程池不支持拒绝策略,创建新的线程池替换自带的.
 *
 * @author ruoyi
 **/
@Configuration
public class EsThreadPoolConfig {

    @Bean(name = "esThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);                 // ≥1
        ex.setMaxPoolSize(8);                  // >= core
        ex.setQueueCapacity(2000);             // 有界队列，形成背压
        ex.setKeepAliveSeconds(60);            // 空闲线程回收时间
        ex.setThreadNamePrefix("esTask-");
        // 某些 Spring 版本没有 setThreadGroupName，没必要可删掉
        ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 满了由调用方执行，防丢
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setAwaitTerminationSeconds(30);
        return ex; // Spring 会自动 initialize()
    }
}
