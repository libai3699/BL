package com.gp.common.base.utils;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/29 22:11
 */
@Slf4j
public class CaffeineUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    @SneakyThrows
    public static LoadingCache<String, Object> buildCaffeine(String url) {
        LoadingCache<String, Object> loadingCache = Caffeine.newBuilder()
                .initialCapacity(100) //初始个数
                .maximumSize(1000000) //最大个数
                .recordStats() //添加统计
                .weakKeys() //GC的时候会清除
                .weakValues() //GC的时候会清除
                .expireAfterAccess(Duration.ofSeconds(2))
                .expireAfterWrite(Duration.ofSeconds(2))//缓存失效返回心智
//                .refreshAfterWrite(Duration.ofSeconds(2)) //缓存失效会返回旧值
                .removalListener((o, o2, removalCause) -> {
                }) //过期后处理
                .build(s -> "我换成新的了 !"); //更新处理
//        loadingCache.put("key", "xxxx");
//        System.out.println("key的值是："+loadingCache.get("key"));
//        log.info("caffien指标: {}", loadingCache.stats());
//        Thread.sleep(5000);

        //对key执行显示刷新
//        loadingCache.refresh("key");
        //全部清除
//        loadingCache.invalidateAll();
//        System.out.println("key的值是："+loadingCache.get("key"));
//        System.out.println("key的值是："+loadingCache.get("key"));
//        log.info("caffien指标: {}", loadingCache.stats());
        return loadingCache;
    }
}
