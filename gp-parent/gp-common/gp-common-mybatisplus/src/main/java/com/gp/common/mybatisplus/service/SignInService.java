package com.gp.common.mybatisplus.service;


import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Date: Created in 13:55 2020/2/26
 * @Description: 基于Redis位图的用户签到功能实现类
 * * <p>
 * * 实现功能：
 * * 1. 用户签到
 * * 2. 检查用户是否签到
 * * 3. 获取当月签到次数
 * * 4. 获取当月连续签到次数
 * * 5. 获取当月首次签到日期
 * * 6. 获取当月签到情况
 */
@Service
public class SignInService  {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户签到
     *
     * @param userId  用户ID
     * @return 之前的签到状态
     */

    public boolean doSign(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String dbCode = CecuUtil.getDbCode();
        // 拼接 key
        String key = dbCode+ RedisKey.USER_SIGN_KEY + userId + keySuffix;
        // 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        return stringRedisTemplate.opsForValue().setBit(key,dayOfMonth - 1, true);

    }

    /**
     * 检查用户是否签到
     *
     * @return 当前的签到状态
     */

    public boolean checkSign(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String dbCode = CecuUtil.getDbCode();
        // 拼接 key
        String key = dbCode+RedisKey.USER_SIGN_KEY + userId + keySuffix;
        int dayOfMonth = now.getDayOfMonth();
        return stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1);
    }


    public long signCount(Long userId) {
        // 获取当前登录用户
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String dbCode = CecuUtil.getDbCode();
        // 拼接 key
        String key =dbCode+ RedisKey.USER_SIGN_KEY + userId + keySuffix;
        // 获取今天是本月的第几天
        // 当前日期为 2022年5月12号，故而 dayOfMonth = 12
        int dayOfMonth = now.getDayOfMonth();
        // 获取从 0 到 dayOfMonth 的签到结果
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key,
                BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        if(result == null || result.isEmpty()){
            // 没有签到结果
            return 0;
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return  0;
        }

        int count = 0;
        // 循环遍历
        while(true){
            // 让这个数字与 1 做与运算，得到数字的最后一位 bit 位  判断这个 bit 位是否为0
            // num & 1 做与运算，其中 1 的左边以 0 补齐
            if ((num & 1) == 0) {
                // 如果为 0，说明未签到，结束
                break;
            } else {
                // 如果不为 0，说明已签到，计数器 +1
                count++;
            }
            // 把数字右移一位，抛弃最后一个 bit 位，继续下一个 bit 位
            // >> :右移 最高位是0，左边补齐0;最高为是1，左边补齐1
            // << :左移 左边最高位丢弃，右边补齐0
            // >>>:无符号右移 无论最高位是0还是1，左边补齐0
            // num >>>= 1 ————> num = num >>> 1
            num >>>= 1;
        }
        return count;
    }




}
