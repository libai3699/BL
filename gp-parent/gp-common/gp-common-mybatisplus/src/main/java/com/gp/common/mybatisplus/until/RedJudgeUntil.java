package com.gp.common.mybatisplus.until;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.ActivityConstants;
import com.gp.common.base.constant.ActivityTypeConstants;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.constant.UserExtTypeCons;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.GameType;
import com.gp.common.mybatisplus.entity.OrderBetHeard;
import com.gp.common.mybatisplus.service.ActivityAwardReceiveService;
import com.gp.common.mybatisplus.service.GameTypeService;
import com.gp.common.mybatisplus.service.OrderTermService;
import com.gp.common.mybatisplus.service.UserExtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Component
public class RedJudgeUntil {
    @Resource
    private UserExtService userExtService;
    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;
    @Resource
    private OrderTermService orderTermService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private GameTypeService gameTypeService;
    /**
     * 验证用户充值金额是否满足红包领取条件
     *
     * @param minRechargeAmount 最小充值金额要求
     * @param rechargeType      充值类型 1-每日 2-永久 3-周 4-月
     * @param userId            用户ID
     */
    public void checkRechargeAmount(BigDecimal minRechargeAmount, Integer rechargeType, Long userId) {
        if (minRechargeAmount == null || minRechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal rechargeAmount;
        BigDecimal firstAmount;

        if (ActivityTypeConstants.FOREVER.equals(rechargeType)) {
            // 永久累计充值
            rechargeAmount = userExtService.queryUSerExt(userId, UserExtTypeCons.累计充值).getAmount();
        } else if (ActivityTypeConstants.DAY.equals(rechargeType)
                || ActivityTypeConstants.WEEK.equals(rechargeType)
                || ActivityTypeConstants.MONTH.equals(rechargeType)) {
            // 每日/周/月充值
            String redisKey = activityAwardReceiveService.getRedisKey(null, ActivityConstants.RECHARGE, rechargeType,
                    userId);
            String first = StrUtil.format(RedisKey.USER_RECHARGE_TODAY_24, CecuUtil.getDbCode(), userId);
            // 从 Redis 获取实际的值  24小时内充值判断 新用户 和首冲判断一样
            String redisValue = getRedisValue(redisKey);
            String firstValue = getRedisValue(first);
            rechargeAmount = parseBigDecimalSafely(redisValue);
            firstAmount = parseBigDecimalSafely(firstValue);
            rechargeAmount = rechargeAmount.add(firstAmount);
        } else {
            rechargeAmount = BigDecimal.ZERO;
        }

        if (rechargeAmount.compareTo(minRechargeAmount) < 0) {
            // 根据充值类型获取类型名称
            String typeName = getRechargeTypeName(rechargeType);
            String msg = StrUtil.format(MessagesUtils.get("bot.red.rechargeLimit"), typeName,
                    BigDecimalUtils.trim(minRechargeAmount));
            Assert.isFalse(true, msg);
        }
    }

    /**
     * 根据充值类型获取用户的充值金额
     *
     * @param rechargeType 充值类型 1-每日 2-永久 3-周 4-月
     * @param userId       用户ID
     * @return 对应类型的充值金额
     */
    public BigDecimal getRechargeAmount(Integer rechargeType, Long userId) {
        if (rechargeType == null) {
            return BigDecimal.ZERO;
        }

        if (ActivityTypeConstants.FOREVER.equals(rechargeType)) {
            // 永久累计充值
            return userExtService.queryUSerExt(userId, UserExtTypeCons.累计充值).getAmount();
        } else if (ActivityTypeConstants.DAY.equals(rechargeType)
                || ActivityTypeConstants.WEEK.equals(rechargeType)
                || ActivityTypeConstants.MONTH.equals(rechargeType)) {
            // 每日/周/月充值
            String redisKey = activityAwardReceiveService.getRedisKey(null, ActivityConstants.RECHARGE, rechargeType,
                    userId);
            // 从 Redis 获取实际的值
            String redisValue = getRedisValue(redisKey);
            return parseBigDecimalSafely(redisValue);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 验证用户是否满足红包/活动的完整条件（充值金额 + 打码量）
     *
     * @param minRechargeAmount 最小充值金额要求
     * @param rechargeType      充值类型 1-每日 2-永久 3-周 4-月
     * @param dayBetAmount      打码量要求
     * @param typeCodeBet       游戏类型code（null或0表示全部游戏）
     * @param userId            用户ID
     */
    public void checkRedCondition(BigDecimal minRechargeAmount, Integer rechargeType,
            BigDecimal dayBetAmount, String typeCodeBet, Long userId) {
        // 1. 验证打码量
        if (dayBetAmount != null && dayBetAmount.compareTo(BigDecimal.ZERO) > 0) {
            OrderBetHeard betHeard = orderTermService.queryTodayBetAmount(userId, typeCodeBet);
            String gameTypeStr = "";
            if (!ActivityAwardReceiveService.isAllGameTypes(typeCodeBet)) {
                gameTypeStr = gameTypeService.listByTypeCodesWithoutOpen(typeCodeBet).stream()
                        .map(GameType::getTypeName).filter(StrUtil::isNotEmpty)
                        .collect(java.util.stream.Collectors.joining("/"));
            }
            BigDecimal userCodeAmount = betHeard.getCodeAmount();
            if (userCodeAmount.compareTo(dayBetAmount) < 0) {
                String msg = StrUtil.format(MessagesUtils.get("bot.red.dayLimit"), gameTypeStr,
                        BigDecimalUtils.trim(dayBetAmount));
                Assert.isFalse(true, msg);
            }
        }

        // 2. 验证充值金额
        checkRechargeAmount(minRechargeAmount, rechargeType, userId);
    }

    /**
     * 根据充值类型获取类型名称
     *
     * @param rechargeType 充值类型 1-每日 2-永久 3-周 4-月
     * @return 类型名称（每日/累计/本周/本月）
     */
    private String getRechargeTypeName(Integer rechargeType) {
        if (ActivityTypeConstants.DAY.equals(rechargeType)) {
            return MessagesUtils.get("bot.recharge.type.day");
        } else if (ActivityTypeConstants.FOREVER.equals(rechargeType)) {
            return MessagesUtils.get("bot.recharge.type.forever");
        } else if (ActivityTypeConstants.WEEK.equals(rechargeType)) {
            return MessagesUtils.get("bot.recharge.type.week");
        } else if (ActivityTypeConstants.MONTH.equals(rechargeType)) {
            return MessagesUtils.get("bot.recharge.type.month");
        }
        return MessagesUtils.get("bot.recharge.type.forever");
    }

    /**
     * 从 Redis 获取值
     *
     * @param redisKey Redis key
     * @return Redis 中的值，如果不存在则返回 null
     */
    private String getRedisValue(String redisKey) {
        if (StrUtil.isBlank(redisKey)) {
            return null;
        }
        try {
            Object value = redisUtil.get(redisKey);
            if (value != null) {
                return value.toString();
            }
            return null;
        } catch (Exception e) {
            log.error("从 Redis 获取值失败, key: {}", redisKey, e);
            return null;
        }
    }

    /**
     * 安全解析字符串为 BigDecimal
     * 当字符串为空或无效时返回 ZERO，不会抛出异常
     *
     * @param value 要解析的字符串
     * @return 解析后的 BigDecimal，无效时返回 ZERO
     */
    private BigDecimal parseBigDecimalSafely(String value) {
        log.info("value: {}", value);
        if (StrUtil.isBlank(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            log.warn("无法解析 BigDecimal 值: {}", value);
            return BigDecimal.ZERO;
        }
    }
}
