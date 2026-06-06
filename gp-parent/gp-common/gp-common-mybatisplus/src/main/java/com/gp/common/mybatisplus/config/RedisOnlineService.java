package com.gp.common.mybatisplus.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.common.datasource.util.CecuUtil;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisOnlineService {

    /**
     * 单 dbCode 一个 ZSet，score=过期时间戳(ms)，避免每次按前缀全库 SCAN
     */
    private static final String ONLINE_ZSET_KEY = "online:zset:{}";
    private static final String MAX_ONLINE_COUNT_TODAY_KEY = "online:{}:{}:{}";
    //活跃人数
    private static final String ONLINE_COUNT_KEY = "day:active:{}:{}";
    private static final long ONLINE_TTL_MS = 5 * 60_000L;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户上线，5分钟失效（ZSet 实现）
     * 旧实现是 String + SCAN，每次新用户上线都会触发全库 SCAN，对 Redis 主线程压力极大。
     * 现改成 ZSet：score=过期时间戳，ZADD/ZCOUNT/ZRANGEBYSCORE 全部 O(log N)
     */
    @SneakyThrows
    public void userOnline(String userId) {
        String key = StrUtil.format(ONLINE_ZSET_KEY, CecuUtil.getDbCode());
        long now = System.currentTimeMillis();
        long expireAt = now + ONLINE_TTL_MS;

        // 顺手清理已过期成员（O(M+log N)，正常 M 很小）
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, now);
        // ZADD 返回 true=新增成员，false=更新已有成员的 score
        Boolean isNew = redisTemplate.opsForZSet().add(key, userId, expireAt);

        if (Boolean.TRUE.equals(isNew)) {
            // 仅"用户首次活跃"分支才设过期与统计，省掉热路径上的 EXPIRE/GET/SET RPC
            // 1 天兜底过期，正常每天都有新用户活跃 → 不会真过期；防止 dbCode 长期无活跃时 key 残留
            redisTemplate.expire(key, 1, TimeUnit.DAYS);

            // 更新今日历史最高在线人数（清理过后 ZCARD 即等于"当前在线"）
            Long currentOnline = redisTemplate.opsForZSet().zCard(key);
            if (currentOnline == null) {
                return;
            }
            String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
            String maxCountKey = StrUtil.format(MAX_ONLINE_COUNT_TODAY_KEY, CecuUtil.getDbCode(), today);

            Object oldMax = redisTemplate.opsForValue().get(maxCountKey);
            long oldMaxVal = oldMax == null ? 0 : Long.parseLong(oldMax.toString());

            if (currentOnline > oldMaxVal) {
                redisTemplate.opsForValue().set(maxCountKey, String.valueOf(currentOnline), 1, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 记录当日活跃人数
     */
    public void recordTodayActivePeopleNum(String userId) {
        String key = StrUtil.format(ONLINE_COUNT_KEY, CecuUtil.getDbCode(), DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));
        // 判断 key 是否存在，不存在则设置过期时间 24 小时
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForSet().add(key, userId);
            redisTemplate.expire(key, 2, TimeUnit.DAYS); // 初始设置有效期为 2天
        } else {
            redisTemplate.opsForSet().add(key, userId); // key 存在时仅添加用户 ID
        }
    }

    /**
     * 获取当日活跃人数（Set 长度）
     */
    public Long getTodayActiveCount() {
        String key = StrUtil.format(ONLINE_COUNT_KEY, CecuUtil.getDbCode(), DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));
        return redisTemplate.opsForSet().size(key);
    }
    /**
     * 获取当日活跃人数（Set 长度）
     * @param day 日期(yyyy-MM-dd)
     */
    public Long getTodayActiveCount(String day) {
        String key = StrUtil.format(ONLINE_COUNT_KEY, CecuUtil.getDbCode(), day.replaceAll("-", ""));
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 当前在线人数（ZCOUNT，O(log N)，不再全库 SCAN）
     */
    public long getOnlineCount() throws IOException {
        String key = StrUtil.format(ONLINE_ZSET_KEY, CecuUtil.getDbCode());
        long now = System.currentTimeMillis();
        Long count = redisTemplate.opsForZSet().count(key, now, Long.MAX_VALUE);
        return count == null ? 0 : count;
    }

    public Long getTodayMaxOnline() {
        String today = DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        String maxCountKey = StrUtil.format(MAX_ONLINE_COUNT_TODAY_KEY, CecuUtil.getDbCode(), today);
        Object oldMax = redisTemplate.opsForValue().get(maxCountKey);
        return oldMax == null ? 0 : Long.parseLong(oldMax.toString());
    }

    /**
     * 当前在线用户 ID 列表（ZRANGEBYSCORE，O(log N + M)，不再全库 SCAN）
     */
    public List<String> getOnlineUserIds() throws IOException {
        String key = StrUtil.format(ONLINE_ZSET_KEY, CecuUtil.getDbCode());
        long now = System.currentTimeMillis();
        Set<Object> members = redisTemplate.opsForZSet().rangeByScore(key, now, Long.MAX_VALUE);
        if (members == null || members.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> userIds = new ArrayList<>(members.size());
        for (Object m : members) {
            userIds.add(m.toString());
        }
        return userIds;
    }



}
