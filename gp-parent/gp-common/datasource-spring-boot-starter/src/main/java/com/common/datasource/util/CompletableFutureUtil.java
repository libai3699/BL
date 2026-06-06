//package com.common.datasource.util;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.Executor;
//
///**
// * 异步线程-支持切库
// *
// * @author axing
// * @version 1.0
// * @date 2023/10/31/031 14:42
// */
//@Slf4j
//@Component
//public class CompletableFutureUtil {
//
//    @Resource
//    private Executor threadPoolTaskExecutor;
//
//    public CompletableFuture<Void> runAsync(BusinessFunction business) {
//        Map<String, String> map = CecuThreadLocal.getMap();
//        return CompletableFuture.runAsync(() -> {
//            CecuThreadLocal.setMap(map);
//            business.execution();
//            CecuThreadLocal.clear();
//        }, threadPoolTaskExecutor);
//    }
//}
