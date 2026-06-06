package com.common.core.util;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * redis工具类
 *
 * @author axing
 * @date 2021/08/03
 */
@Slf4j
@Data
public class RedisLockUtil {

    private RedisLock redisLock;

    public static final long SLEEP_TIME = 100;

    /**
     *
     * @param lockName 锁的名字
     * @param time     时间
     * @param timeUnit 时间单位
     * @return {@link Boolean}
     */
    @SneakyThrows
    public <T> T tryLock(String lockName, Integer time, TimeUnit timeUnit, Supplier<T> supplier) {
        if (redisLock.tryLock(lockName, time, timeUnit)) {
            try {
                return supplier.get();
            } finally {
                redisLock.unlock(lockName);
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }
    }

}
