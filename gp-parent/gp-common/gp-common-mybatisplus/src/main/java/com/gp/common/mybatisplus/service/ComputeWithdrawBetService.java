package com.gp.common.mybatisplus.service;

import com.alibaba.fastjson.JSON;
import com.common.core.enums.ComputeWithdrawBetEnum;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.log.TelegramUtil;
import com.gp.common.base.constant.ActivityConstants;
import com.gp.common.base.constant.ActivityTypeConstants;
import com.gp.common.base.model.LOG;
import com.gp.common.mybatisplus.cache.RedisClientApiCache;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 领取返水返佣记录Service业务层处理
 *
 * @author axing
 * @date 2024-05-17
 */
@Service
@Slf4j
public class ComputeWithdrawBetService {
    @Resource
    private ConfigAmountService configAmountService;
    @Resource
    private RedisClientApiCache redisClientApiCache;
    @Resource
    private ErrorTelegramUtil errorTelegramUtil;
    public BigDecimal computeWithdrawBetService(BigDecimal amount, BigDecimal bonus, ComputeWithdrawBetEnum computeWithdrawBetEnum) {
        if (Objects.equals(computeWithdrawBetEnum, ComputeWithdrawBetEnum.userRecharge) || Objects.equals(computeWithdrawBetEnum, ComputeWithdrawBetEnum.personAmountUp)) {
            return configAmountService.upAmountRatio().multiply(amount).add((bonus.multiply(configAmountService.upBonusRatio()))).abs();
        }
        if (Objects.equals(computeWithdrawBetEnum, ComputeWithdrawBetEnum.wheelAward)) {
            return (bonus.multiply(configAmountService.wheelBonusRatio())).abs();
        }
        if (Objects.equals(computeWithdrawBetEnum, ComputeWithdrawBetEnum.taskAward)) {
            return (bonus.multiply(configAmountService.taskBonusRatio())).abs();
        }

        return BigDecimal.ZERO;
    }

    /**
     *
     * @param bonus
     * @param withdrawBonusRatio 提现比例
     * @param awardId  任务id
     * @return
     */
    public BigDecimal computeActivityService(BigDecimal bonus,BigDecimal withdrawBonusRatio,Long awardId) {
        if(withdrawBonusRatio.compareTo(BigDecimal.ZERO)<0){
            //errorTelegramUtil.dealErrorMsg("提现打码量比例配置错误，请检查配置奖励id="+awardId);
            return (bonus.multiply(configAmountService.taskBonusRatio())).abs();
        }else {
            return (bonus.multiply(withdrawBonusRatio)).abs();
        }

    }

    /**
     *
     * @param bonus
     * @param withdrawBonusRatio 提现比例
     * @return
     */
    public BigDecimal computeRedPacketService(BigDecimal bonus,BigDecimal withdrawBonusRatio) {
        if(withdrawBonusRatio.compareTo(BigDecimal.ZERO)<0){
            //errorTelegramUtil.dealErrorMsg("提现打码量比例配置错误，请检查配置奖励id="+awardId);
            return (bonus.multiply(configAmountService.taskBonusRatio())).abs();
        }else {
            return (bonus.multiply(withdrawBonusRatio)).abs();
        }

    }

    public BigDecimal computeMultipleService(BigDecimal bonus, BigDecimal multiple) {
        if (bonus == null || multiple == null) {
            return BigDecimal.ZERO;
        }
        return bonus.multiply(multiple);
    }

    public static void main(String[] args) {
        System.out.println(ActivityConstants.HELPMONEY);
    }
}
