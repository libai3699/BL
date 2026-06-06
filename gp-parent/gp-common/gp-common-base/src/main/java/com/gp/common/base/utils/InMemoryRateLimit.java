package com.gp.common.base.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;


/**
 * 内存限流
 *
 * @author Administrator
 */
public class InMemoryRateLimit {

    private TimedCache<String, Node> cache;

    public InMemoryRateLimit() {
        cache = CacheUtil.newTimedCache(0);
        cache.schedulePrune(60 * 1000);
        // 销毁时调用cache.cancelPruneSchedule();
    }


    /**
     * 尝试获取令牌
     *
     * @param id        令牌桶id
     * @param rate      填充速率 个/秒
     * @param capacity  令牌桶容量
     * @param requested 当次请求消耗令牌个数
     * @return true 允许访问 ； false 拒绝访问
     */
    public boolean tryAllowed(String id, int rate, int capacity, int requested) {
        String intern = id.intern();
        int ttl = BigDecimal.valueOf(capacity).divide(BigDecimal.valueOf(rate), 0, RoundingMode.CEILING).multiply(BigDecimal.valueOf(2)).intValue();
        long now = Instant.now().getEpochSecond();
        Long lastToken;
        Long timestamp;
        boolean allowed = false;
        synchronized (intern) {
            Node node = cache.get(id);
            if (node != null) {
                lastToken = node.getLastTokens();
                timestamp = node.getTimestamp();
            } else {
                node = new Node();
                lastToken = new Long(capacity);
                timestamp = 0L;
            }
            long timeDiff = now - timestamp;
            long filledTokens = Math.min(capacity, lastToken + (timeDiff * rate));
            if (filledTokens >= requested) {
                filledTokens = filledTokens - requested;
                allowed = true;

            }
            node.setLastTokens(filledTokens);
            node.setTimestamp(now);
            cache.put(intern, node, ttl * 1000);
        }
        return allowed;
    }


    @Data
    @NoArgsConstructor
    private static class Node {
        /**
         * 时间戳
         */
        private volatile Long timestamp;
        /**
         * 剩余令牌
         */
        private volatile Long lastTokens;
    }

}
