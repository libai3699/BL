package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.mapper.InviteRechargeRewardConfigMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import com.gp.common.mybatisplus.entity.InviteRechargeRewardConfig;

/**
 * 邀请新人充值奖励配置Service业务层处理
 *
 * @author axing
 * @date 2026-03-25
 */
@Service
public class InviteRechargeRewardConfigService extends ServiceImpl<InviteRechargeRewardConfigMapper, InviteRechargeRewardConfig>
{
    @Autowired
    private InviteRechargeRewardConfigMapper inviteRechargeRewardConfigMapper;

    /**
     * 查询邀请新人充值奖励配置
     *
     * @param id 邀请新人充值奖励配置ID
     * @return 邀请新人充值奖励配置
     */

    public InviteRechargeRewardConfig selectInviteRechargeRewardConfigById(Long id)
    {
        return inviteRechargeRewardConfigMapper.selectInviteRechargeRewardConfigById(id);
    }

    /**
     * 查询邀请新人充值奖励配置列表
     *
     * @param param 邀请新人充值奖励配置
     * @return 邀请新人充值奖励配置
     */

    public List<InviteRechargeRewardConfig> selectInviteRechargeRewardConfigList(InviteRechargeRewardConfig param)
    {
        return inviteRechargeRewardConfigMapper.selectInviteRechargeRewardConfigList(param);
    }

    /**
     * 新增邀请新人充值奖励配置
     *
     * @param param 邀请新人充值奖励配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertInviteRechargeRewardConfig(InviteRechargeRewardConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改邀请新人充值奖励配置
     *
     * @param param 邀请新人充值奖励配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateInviteRechargeRewardConfig(InviteRechargeRewardConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除邀请新人充值奖励配置
     *
     * @param ids 需要删除的邀请新人充值奖励配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteInviteRechargeRewardConfigByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除邀请新人充值奖励配置信息
     *
     * @param id 邀请新人充值奖励配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteInviteRechargeRewardConfigById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
