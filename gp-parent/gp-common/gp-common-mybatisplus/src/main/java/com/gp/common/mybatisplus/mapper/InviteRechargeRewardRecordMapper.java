package com.gp.common.mybatisplus.mapper;

import java.util.List;
import java.util.Map;

import com.gp.common.mybatisplus.entity.InviteRechargeRewardRecord;
import com.gp.common.mybatisplus.vo.InviteeDayRechargeSumVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 邀请新人充值奖励记录Mapper接口
 *
 * @author axing
 * @date 2026-03-25
 */
public interface InviteRechargeRewardRecordMapper extends BaseMapper<InviteRechargeRewardRecord>
{
    /**
     * 查询邀请新人充值奖励记录
     *
     * @param id 邀请新人充值奖励记录ID
     * @return 邀请新人充值奖励记录
     */
    public InviteRechargeRewardRecord selectInviteRechargeRewardRecordById(Long id);

    /**
     * 查询邀请新人充值奖励记录列表
     *
     * @param inviteRechargeRewardRecord 邀请新人充值奖励记录
     * @return 邀请新人充值奖励记录集合
     */
    public List<InviteRechargeRewardRecord> selectInviteRechargeRewardRecordList(InviteRechargeRewardRecord inviteRechargeRewardRecord);

    /**
     * 新增邀请新人充值奖励记录
     *
     * @param inviteRechargeRewardRecord 邀请新人充值奖励记录
     * @return 结果
     */
    public int insertInviteRechargeRewardRecord(InviteRechargeRewardRecord inviteRechargeRewardRecord);

    /**
     * 修改邀请新人充值奖励记录
     *
     * @param inviteRechargeRewardRecord 邀请新人充值奖励记录
     * @return 结果
     */
    public int updateInviteRechargeRewardRecord(InviteRechargeRewardRecord inviteRechargeRewardRecord);

    /**
     * 删除邀请新人充值奖励记录
     *
     * @param id 邀请新人充值奖励记录ID
     * @return 结果
     */
    public int deleteInviteRechargeRewardRecordById(Long id);

    /**
     * 批量删除邀请新人充值奖励记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteInviteRechargeRewardRecordByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    /**
     * 查询邀请新人充值奖励记录列表数量
     *
     * @param param 查询条件
     * @return 记录数量
     */
    int selectInviteRechargeRewardRecordListCount(InviteRechargeRewardRecord param);

    /**
     * 查询待发放的记录（status=0 且 retry_count < maxRetry），用于定时补发
     *
     * @param maxRetry 最大重试次数
     * @return 待发放的记录集合
     */
    List<InviteRechargeRewardRecord> selectPendingRecords(@Param("maxRetry") int maxRetry);

    /**
     * XXL 统计：邀请人维度，已发放奖励金额与笔数（按发放时间）
     */
    Map<String, Object> statInviteRewardByInviter(@Param("userId") Long userId, @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * XXL 统计：渠道维度（邀请人所属渠道）
     */
    Map<String, Object> statInviteRewardByChannel(@Param("channelId") Long channelId, @Param("startTime") String startTime,
            @Param("endTime") String endTime);

    /**
     * XXL 统计：全站已发放邀请奖励
     */
    Map<String, Object> statInviteRewardAll(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 自然日内用户充值总额：t_order_amount(已支付) + t_order_person(上分)
     */
    List<InviteeDayRechargeSumVO> listUserRechargeSumByDay(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
