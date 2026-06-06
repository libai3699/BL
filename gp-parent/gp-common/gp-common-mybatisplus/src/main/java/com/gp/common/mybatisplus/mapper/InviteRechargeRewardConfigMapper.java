package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.InviteRechargeRewardConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 邀请新人充值奖励配置Mapper接口
 *
 * @author axing
 * @date 2026-03-25
 */
public interface InviteRechargeRewardConfigMapper extends BaseMapper<InviteRechargeRewardConfig>
{
    /**
     * 查询邀请新人充值奖励配置
     *
     * @param id 邀请新人充值奖励配置ID
     * @return 邀请新人充值奖励配置
     */
    public InviteRechargeRewardConfig selectInviteRechargeRewardConfigById(Long id);

    /**
     * 查询邀请新人充值奖励配置列表
     *
     * @param inviteRechargeRewardConfig 邀请新人充值奖励配置
     * @return 邀请新人充值奖励配置集合
     */
    public List<InviteRechargeRewardConfig> selectInviteRechargeRewardConfigList(InviteRechargeRewardConfig inviteRechargeRewardConfig);

    /**
     * 新增邀请新人充值奖励配置
     *
     * @param inviteRechargeRewardConfig 邀请新人充值奖励配置
     * @return 结果
     */
    public int insertInviteRechargeRewardConfig(InviteRechargeRewardConfig inviteRechargeRewardConfig);

    /**
     * 修改邀请新人充值奖励配置
     *
     * @param inviteRechargeRewardConfig 邀请新人充值奖励配置
     * @return 结果
     */
    public int updateInviteRechargeRewardConfig(InviteRechargeRewardConfig inviteRechargeRewardConfig);

    /**
     * 删除邀请新人充值奖励配置
     *
     * @param id 邀请新人充值奖励配置ID
     * @return 结果
     */
    public int deleteInviteRechargeRewardConfigById(Long id);

    /**
     * 批量删除邀请新人充值奖励配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteInviteRechargeRewardConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    /**
     * 查询所有开启的配置，按最低充值金额降序排列（用于匹配最高档位）
     *
     * @return 开启状态的配置集合（按min_recharge_amount DESC）
     */
    List<InviteRechargeRewardConfig> selectEnabledConfigsOrderByAmountDesc();

    /**
     * 每日充值奖励：开启的配置，按最低充值金额降序
     */
    List<InviteRechargeRewardConfig> selectEnabledDailyConfigsOrderByAmountDesc();

}
