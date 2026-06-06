package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.OrderBetHeard;
import com.gp.common.mybatisplus.entity.RedPacketRainConfig;
import com.gp.common.mybatisplus.mapper.RedPacketRainConfigMapper;
import com.gp.common.mybatisplus.until.RedJudgeUntil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;


/**
 * 红包雨活动配置Service业务层处理
 *
 * @author axing
 * @date 2025-07-24
 */
@Service
public class RedPacketRainConfigService extends ServiceImpl<RedPacketRainConfigMapper, RedPacketRainConfig>
{
    @Autowired
    private RedPacketRainConfigMapper redPacketRainConfigMapper;
    @Resource
    private RedJudgeUntil redJudgeUntil;
    @Resource
    private OrderTermService orderTermService;
    /**
     * 查询红包雨活动配置
     *
     * @param id 红包雨活动配置ID
     * @return 红包雨活动配置
     */

    public RedPacketRainConfig selectRedPacketRainConfigById(Long id)
    {
        return redPacketRainConfigMapper.selectRedPacketRainConfigById(id);
    }

    /**
     * 查询红包雨活动配置列表
     *
     * @param param 红包雨活动配置
     * @return 红包雨活动配置
     */

    public List<RedPacketRainConfig> selectRedPacketRainConfigList(RedPacketRainConfig param)
    {
        return redPacketRainConfigMapper.selectRedPacketRainConfigList(param);
    }

    /**
     * 新增红包雨活动配置
     *
     * @param param 红包雨活动配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertRedPacketRainConfig(RedPacketRainConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包雨活动配置
     *
     * @param param 红包雨活动配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRedPacketRainConfig(RedPacketRainConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包雨活动配置
     *
     * @param ids 需要删除的红包雨活动配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRedPacketRainConfigByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包雨活动配置信息
     *
     * @param id 红包雨活动配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRedPacketRainConfigById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
    public RedPacketRainConfig queryRedPacketRainToday(Integer userLevel, BigDecimal codeAmount, BigDecimal rechargeAmount) {
        Date startTimeByDate = DateUtils.getStartTimeByDate(new Date());
        return redPacketRainConfigMapper.queryRedPacketRainToday(userLevel, codeAmount, rechargeAmount, startTimeByDate);
    }
    public RedPacketRainConfig queryRedPacketRainConfig(Integer userLevel, BigDecimal codeAmount, BigDecimal rechargeAmount) {
        return redPacketRainConfigMapper.queryRedPacketRainConfig(userLevel, codeAmount, rechargeAmount);
    }

    /**
     * 根据充值类型和游戏类型打码量动态过滤红包雨配置
     *
     * @param userLevel        用户等级
     * @param userId           用户ID（用于根据recharge_type获取对应的充值金额）
     * @return 符合条件的红包雨配置
     */
    public RedPacketRainConfig queryRedPacketRainTodayByRechargeType(Integer userLevel, Long userId) {
        // 先查询符合等级条件的红包雨列表（不过滤充值和打码量）
        List<RedPacketRainConfig> list = redPacketRainConfigMapper.queryRedPacketRainTodayList(userLevel,
                BigDecimal.ZERO);
        if (list == null || list.isEmpty()) {
            return null;
        }

        // 遍历列表，根据每个配置的recharge_type和type_code_bet进行验证
        for (RedPacketRainConfig config : list) {
            // 1. 验证游戏类型打码量 (type_code_bet)
            BigDecimal dayBetAmount = config.getDayBetAmount();
            if (dayBetAmount != null && dayBetAmount.compareTo(BigDecimal.ZERO) > 0) {
                String typeCodeBet = config.getTypeCodeBet();
                OrderBetHeard betHeard = orderTermService.queryTodayBetAmount(userId,
                        typeCodeBet);
                BigDecimal userCodeAmount = betHeard.getCodeAmount();
                if (userCodeAmount.compareTo(dayBetAmount) < 0) {
                    // 打码量不足，跳过此配置
                    continue;
                }
            }

            // 2. 验证充值金额 (根据recharge_type动态获取)
            BigDecimal minRechargeAmount = config.getMinRechargeAmount();
            if (minRechargeAmount != null && minRechargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal userRechargeAmount = redJudgeUntil.getRechargeAmount(config.getRechargeType(), userId);
                if (userRechargeAmount.compareTo(minRechargeAmount) < 0) {
                    // 充值金额不足，跳过此配置
                    continue;
                }
            }

            // 满足所有条件，返回此配置
            return config;
        }

        return null;
    }
}
