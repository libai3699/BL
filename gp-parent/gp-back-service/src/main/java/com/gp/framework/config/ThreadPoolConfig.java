package com.gp.framework.config;

import java.util.concurrent.*;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.gp.common.utils.Threads;

/**
 * 线程池配置
 * 因spring自带的线程池不支持拒绝策略,创建新的线程池替换自带的.
 * @author ruoyi
 **/
@Configuration
public class ThreadPoolConfig
{
    // 核心线程池大小
    public static int corePoolSize = 10;

    // 最大可创建的线程数
    public static int maxPoolSize = 100;

    // 队列最大长度
    public static int queueCapacity = 1000;

    // 线程池维护线程所允许的空闲时间
    public static int keepAliveSeconds = 300;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setCorePoolSize(corePoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("backTaskGroup-");
        executor.setThreadGroupName("backTaskGroup");
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService()
    {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build())
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t)
            {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }

//    /**
//     * 执行周期性或定时任务
//     */
//    @Bean(name = "serviceThreadPoolExecutor")
//    protected ThreadPoolExecutor serviceThreadPoolExecutor()
//    {
//        return new ThreadPoolExecutor(11, 100, 1, TimeUnit.MINUTES, //
//                new ArrayBlockingQueue<Runnable>(10000),//
//                new BasicThreadFactory.Builder().namingPattern("service-pool-%d").daemon(true).build()) {
//            @Override
//            protected void afterExecute(Runnable r, Throwable t) {
//                super.afterExecute(r, t);
//            }
//        };
//    }
}
