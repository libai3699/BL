package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.OrderBetHeard;
import com.gp.common.mybatisplus.until.RedJudgeUntil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PrivateRedPacketConfigMapper;
import com.gp.common.mybatisplus.entity.PrivateRedPacketConfig;

import javax.annotation.Resource;

/**
 * 私人红包配置Service业务层处理
 *
 * @author axing
 * @date 2025-07-25
 */
@Service
public class PrivateRedPacketConfigService extends ServiceImpl<PrivateRedPacketConfigMapper, PrivateRedPacketConfig>
{
    @Autowired
    private PrivateRedPacketConfigMapper privateRedPacketConfigMapper;
    @Resource
    private RedJudgeUntil redJudgeUntil;
    @Resource
    private OrderTermService orderTermService;
    /**
     * 查询私人红包配置
     *
     * @param id 私人红包配置ID
     * @return 私人红包配置
     */

    public PrivateRedPacketConfig selectPrivateRedPacketConfigById(Long id)
    {
        return privateRedPacketConfigMapper.selectPrivateRedPacketConfigById(id);
    }

    /**
     * 查询私人红包配置列表
     *
     * @param param 私人红包配置
     * @return 私人红包配置
     */

    public List<PrivateRedPacketConfig> selectPrivateRedPacketConfigList(PrivateRedPacketConfig param)
    {
        return privateRedPacketConfigMapper.selectPrivateRedPacketConfigList(param);
    }

    /**
     * 新增私人红包配置
     *
     * @param param 私人红包配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPrivateRedPacketConfig(PrivateRedPacketConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改私人红包配置
     *
     * @param param 私人红包配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePrivateRedPacketConfig(PrivateRedPacketConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除私人红包配置
     *
     * @param ids 需要删除的私人红包配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePrivateRedPacketConfigByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除私人红包配置信息
     *
     * @param id 私人红包配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePrivateRedPacketConfigById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public PrivateRedPacketConfig queryPrivateRedPacketConfig(Long userId, Long privateRedId,Integer userLevel, BigDecimal codeAmount, BigDecimal rechargeAmount) {
        return privateRedPacketConfigMapper.queryPrivateRedPacketConfig(userId,privateRedId,userLevel,codeAmount,rechargeAmount);
    }

    public List<PrivateRedPacketConfig> queryPrivateRedPacketConfigs(Long userId, Integer userLevel, BigDecimal codeAmount, BigDecimal rechargeAmount) {
        return privateRedPacketConfigMapper.queryPrivateRedPacketConfigs(userId,userLevel,codeAmount,rechargeAmount);
    }

    /**
     * 根据充值类型和游戏类型打码量动态过滤私人红包配置
     *
     * @param userId           用户ID
     * @param userLevel        用户等级
     * @return 符合条件的私人红包配置列表
     */
    public List<PrivateRedPacketConfig> queryPrivateRedPacketConfigsByRechargeType(Long userId, Integer userLevel) {
        // 先查询符合等级条件的私人红包列表（不过滤充值和打码量）
        List<PrivateRedPacketConfig> list = privateRedPacketConfigMapper.queryPrivateRedPacketConfigList(userId,
                userLevel);
        if (list == null || list.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<PrivateRedPacketConfig> result = new java.util.ArrayList<>();

        // 遍历列表，根据每个配置的recharge_type和type_code_bet进行验证
        for (PrivateRedPacketConfig config : list) {
            boolean passed = true;

            // 1. 验证游戏类型打码量 (type_code_bet)
            BigDecimal dayBetAmount = config.getDayBetAmount();
            if (dayBetAmount != null && dayBetAmount.compareTo(BigDecimal.ZERO) > 0) {
                String typeCodeBet = config.getTypeCodeBet();
                OrderBetHeard betHeard = orderTermService.queryTodayBetAmount(userId,
                        typeCodeBet);
                BigDecimal userCodeAmount = betHeard.getCodeAmount();
                if (userCodeAmount.compareTo(dayBetAmount) < 0) {
                    passed = false;
                }
            }

            // 2. 验证充值金额 (根据recharge_type动态获取)
            if (passed) {
                BigDecimal minRechargeAmount = config.getMinRechargeAmount();
                if (minRechargeAmount != null && minRechargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal userRechargeAmount = redJudgeUntil.getRechargeAmount(config.getRechargeType(), userId);
                    if (userRechargeAmount.compareTo(minRechargeAmount) < 0) {
                        passed = false;
                    }
                }
            }

            if (passed) {
                result.add(config);
            }
        }

        return result;
    }
}
