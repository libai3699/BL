package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.mapper.InviteRechargeRewardConfigMapper;
import com.gp.common.mybatisplus.mapper.InviteRechargeRewardRecordMapper;
import com.gp.common.mybatisplus.vo.InviteeDayRechargeSumVO;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 邀请新人充值奖励记录Service业务层处理
 *
 * @author axing
 * @date 2026-03-25
 */
@Slf4j
@Service
public class InviteRechargeRewardRecordService extends ServiceImpl<InviteRechargeRewardRecordMapper, InviteRechargeRewardRecord>
{
    /** 邀请奖励：首充（实时触发） */
    private static final int REWARD_TYPE_FIRST = 1;
    /** 邀请奖励：每日充值达标（定时汇总 t_order_amount + t_order_person 上分） */
    private static final int REWARD_TYPE_DAILY = 2;

    @Autowired
    private InviteRechargeRewardRecordMapper inviteRechargeRewardRecordMapper;
    @Resource
    private InviteRechargeRewardConfigMapper configMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;
    @Resource
    private MqSendEntityService mqSendEntityService;

    // ======================== 记录表CRUD ========================

    /**
     * 查询邀请新人充值奖励记录
     *
     * @param id 邀请新人充值奖励记录ID
     * @return 邀请新人充值奖励记录
     */
    public InviteRechargeRewardRecord selectInviteRechargeRewardRecordById(Long id)
    {
        return inviteRechargeRewardRecordMapper.selectInviteRechargeRewardRecordById(id);
    }

    /**
     * 查询邀请新人充值奖励记录列表
     *
     * @param param 邀请新人充值奖励记录
     * @return 邀请新人充值奖励记录
     */
    public List<InviteRechargeRewardRecord> selectInviteRechargeRewardRecordList(InviteRechargeRewardRecord param)
    {
        return inviteRechargeRewardRecordMapper.selectInviteRechargeRewardRecordList(param);
    }

    /**
     * 查询邀请新人充值奖励记录列表数量
     *
     * @param param 查询条件
     * @return 记录数量
     */
    public int selectInviteRechargeRewardRecordListCount(InviteRechargeRewardRecord param)
    {
        return inviteRechargeRewardRecordMapper.selectInviteRechargeRewardRecordListCount(param);
    }

    /** XXL：用户维度邀请奖励汇总（已发放） */
    public Map<String, Object> statInviteRewardByInviter(Long userId, String startTime, String endTime) {
        return inviteRechargeRewardRecordMapper.statInviteRewardByInviter(userId, startTime, endTime);
    }

    /** XXL：渠道维度 */
    public Map<String, Object> statInviteRewardByChannel(Long channelId, String startTime, String endTime) {
        return inviteRechargeRewardRecordMapper.statInviteRewardByChannel(channelId, startTime, endTime);
    }

    /** XXL：全站 */
    public Map<String, Object> statInviteRewardAll(String startTime, String endTime) {
        return inviteRechargeRewardRecordMapper.statInviteRewardAll(startTime, endTime);
    }

    /**
     * 新增邀请新人充值奖励记录
     *
     * @param param 邀请新人充值奖励记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertInviteRechargeRewardRecord(InviteRechargeRewardRecord param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改邀请新人充值奖励记录
     *
     * @param param 邀请新人充值奖励记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateInviteRechargeRewardRecord(InviteRechargeRewardRecord param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除邀请新人充值奖励记录
     *
     * @param ids 需要删除的邀请新人充值奖励记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteInviteRechargeRewardRecordByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除邀请新人充值奖励记录信息
     *
     * @param id 邀请新人充值奖励记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteInviteRechargeRewardRecordById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    // ======================== 核心发奖逻辑 ========================

    /**
     * 新人首充时触发：匹配档位 → 插记录(status=0) → 尝试发奖
     *
     * @param newUser        新充值的用户
     * @param rechargeAmount 首充金额
     */
    public void grantInviteRechargeReward(TUser newUser, BigDecimal rechargeAmount) {
        try {
            Long inviterUserId = newUser.getSuperUserId();
            if (inviterUserId == null || inviterUserId == 0) {
                return;
            }

            LambdaQueryWrapper<InviteRechargeRewardRecord> q = new LambdaQueryWrapper<>();
            q.eq(InviteRechargeRewardRecord::getInviterUserId, inviterUserId);
            q.eq(InviteRechargeRewardRecord::getInviteeUserId, newUser.getUserId());
            q.eq(InviteRechargeRewardRecord::getRewardType, REWARD_TYPE_FIRST);
            q.last("limit 1");
            InviteRechargeRewardRecord exist = inviteRechargeRewardRecordMapper.selectOne(q);
            if (exist != null) {
                return;
            }

            List<InviteRechargeRewardConfig> configs = configMapper.selectEnabledConfigsOrderByAmountDesc();
            if (CollUtil.isEmpty(configs)) {
                return;
            }
            InviteRechargeRewardConfig matchedConfig = matchHighestTier(configs, rechargeAmount);
            if (matchedConfig == null) {
                return;
            }

            BigDecimal rewardAmount = matchedConfig.getRewardAmount();
            BigDecimal withdrawBetAmount = computeWithdrawBetService.computeRedPacketService(
                    rewardAmount, matchedConfig.getWithdrawBonusRatio());
            String orderNo = SnowIdUtil.getId(OrderConstant.inviteRechargeReward);

            Date bizDay = Date.from(LocalDate.now(ZoneId.systemDefault())
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());

            InviteRechargeRewardRecord record = new InviteRechargeRewardRecord();
            record.setInviterUserId(inviterUserId);
            record.setInviteeUserId(newUser.getUserId());
            record.setRewardType(REWARD_TYPE_FIRST);
            record.setBizDate(bizDay);
            record.setRechargeAmount(rechargeAmount);
            record.setRewardAmount(rewardAmount);
            record.setWithdrawBetAmount(withdrawBetAmount);
            record.setConfigId(matchedConfig.getId());
            record.setOrderNo(orderNo);
            record.setStatus(0);
            record.setRetryCount(0);
            record.setCreateTime(new Date());
            inviteRechargeRewardRecordMapper.insert(record);

            doGrant(record);

        } catch (Exception e) {
            log.error("邀请新人充值奖励异常, newUserId={}, amount={}", newUser.getUserId(), rechargeAmount, e);
        }
    }

    /**
     * 实际发奖：给邀请人加彩金 + 加提现打码量
     *
     * @param record 待发放的记录
     */
    public void doGrant(InviteRechargeRewardRecord record) {
        try {
            TUser inviter = userService.getById(record.getInviterUserId());
            if (inviter == null) {
                markFailed(record, "邀请人不存在");
                return;
            }

            String dbCode = CecuUtil.getDbCode();
            BigDecimal rewardAmount = record.getRewardAmount();
            BigDecimal withdrawBetAmount = record.getWithdrawBetAmount();
            String orderNo = record.getOrderNo();

            ExtConsumer extConsumer = () -> {};

            List<ChangeExtValueVo> arr = CollUtil.newArrayList();
            ChangeExtValueVo bonusVo = new ChangeExtValueVo();
            bonusVo.setUserId(inviter.getUserId());
            bonusVo.setExtType(UserExtTypeCons.彩金);
            bonusVo.setUpdateValue(rewardAmount);
            bonusVo.setOrderNo(orderNo);
            bonusVo.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            bonusVo.setAccountType(AccountChangeTypeConstants.INCOME);
            bonusVo.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            bonusVo.setOperator("system");
            arr.add(bonusVo);

            ChangeExtValueVo bonusSmallVo = new ChangeExtValueVo();
            bonusSmallVo.setUserId(inviter.getUserId());
            bonusSmallVo.setExtType(UserExtTypeCons.活动彩金);
            bonusSmallVo.setUpdateValue(rewardAmount);
            bonusSmallVo.setOrderNo(orderNo);
            bonusSmallVo.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            bonusSmallVo.setAccountType(AccountChangeTypeConstants.INCOME);
            bonusSmallVo.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            bonusSmallVo.setOperator("system");
            arr.add(bonusSmallVo);

            ChangeExtValueVo withdrawBetVo = new ChangeExtValueVo();
            withdrawBetVo.setUserId(inviter.getUserId());
            withdrawBetVo.setExtType(UserExtTypeCons.提现打码量);
            withdrawBetVo.setUpdateValue(withdrawBetAmount);
            withdrawBetVo.setOrderNo(orderNo);
            withdrawBetVo.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            withdrawBetVo.setAccountType(AccountChangeTypeConstants.INCOME);
            withdrawBetVo.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
            withdrawBetVo.setOperator("system");
            arr.add(withdrawBetVo);

            userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.bonusAward,
                    OrderTypeEnum.bonusAward, true, 1, rewardAmount);

            mqSendEntityService.sendOrderEntity(
                    OrderEntity.builder()
                            .userId(inviter.getUserId())
                            .channelId(inviter.getChannelId())
                            .type(MqEventTypeConstants.INVITE_RECHARGE_REWARD)
                            .bonusAmount(rewardAmount)
                            .orderNo(orderNo)
                            .productId(dbCode)
                            .build());

            userService.sendJudgeUserScoreMq(rewardAmount, dbCode,
                    MerchantChangeTypeConstants.ACTIVITY_REWARD,
                    MerchantOrderTypeConstant.ACTIVITY_REWARD, "邀请新人充值奖励");

            record.setStatus(1);
            record.setGrantTime(new Date());
            record.setFailReason(null);
            inviteRechargeRewardRecordMapper.updateById(record);

            log.info("邀请新人充值奖励发放成功, inviterUserId={}, inviteeUserId={}, reward={}",
                    record.getInviterUserId(), record.getInviteeUserId(), rewardAmount);

        } catch (Exception e) {
            log.error("邀请新人充值奖励发放失败, recordId={}", record.getId(), e);
            markRetry(record, e.getMessage());
        }
    }

    /**
     * 定时补发：查询待发放记录，逐条重试
     */
    public void retryPendingRewards() {
        List<InviteRechargeRewardRecord> pendingRecords = inviteRechargeRewardRecordMapper.selectPendingRecords(5);
        if (CollUtil.isEmpty(pendingRecords)) {
            return;
        }
        log.info("邀请新人充值奖励补发，待处理记录数={}", pendingRecords.size());
        for (InviteRechargeRewardRecord record : pendingRecords) {
            doGrant(record);
        }
    }

    /**
     * 每日邀请充值奖励：按自然日汇总 t_order_amount（已支付）+ t_order_person（上分）充值额，达标则给邀请人发奖（与首充档位规则相同，配置 reward_type=2）。
     */
    public void processDailyInviteRechargeRewards(LocalDate statDate) {
        if (statDate == null) {
            return;
        }
        List<InviteRechargeRewardConfig> configs = configMapper.selectEnabledDailyConfigsOrderByAmountDesc();
        if (CollUtil.isEmpty(configs)) {
            log.debug("每日邀请充值奖励：无开启的每日配置, statDate={}", statDate);
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dayStr = statDate.format(dtf);
        String startTime = dayStr + " 00:00:00";
        String endTime = dayStr + " 23:59:59";

        List<InviteeDayRechargeSumVO> sums = inviteRechargeRewardRecordMapper.listUserRechargeSumByDay(startTime, endTime);
        if (CollUtil.isEmpty(sums)) {
            return;
        }
        List<Long> userIds = sums.stream().map(InviteeDayRechargeSumVO::getUserId).collect(Collectors.toList());
        List<TUser> users = userService.listByIds(userIds);
        Map<Long, TUser> userMap = users.stream().collect(Collectors.toMap(TUser::getUserId, u -> u, (a, b) -> a));

        Date bizDay = Date.from(statDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        for (InviteeDayRechargeSumVO row : sums) {
            try {
                TUser invitee = userMap.get(row.getUserId());
                if (invitee == null) {
                    continue;
                }
                Long inviterUserId = invitee.getSuperUserId();
                if (inviterUserId == null || inviterUserId == 0) {
                    continue;
                }
                InviteRechargeRewardConfig matched = matchHighestTier(configs, row.getTotalAmount());
                if (matched == null) {
                    continue;
                }

                LambdaQueryWrapper<InviteRechargeRewardRecord> dup = new LambdaQueryWrapper<>();
                dup.eq(InviteRechargeRewardRecord::getInviterUserId, inviterUserId);
                dup.eq(InviteRechargeRewardRecord::getInviteeUserId, invitee.getUserId());
                dup.eq(InviteRechargeRewardRecord::getRewardType, REWARD_TYPE_DAILY);
                dup.eq(InviteRechargeRewardRecord::getBizDate, bizDay);
                dup.last("limit 1");
                if (inviteRechargeRewardRecordMapper.selectCount(dup) > 0) {
                    continue;
                }

                BigDecimal rewardAmount = matched.getRewardAmount();
                BigDecimal withdrawBetAmount = computeWithdrawBetService.computeRedPacketService(
                        rewardAmount, matched.getWithdrawBonusRatio());
                String orderNo = SnowIdUtil.getId(OrderConstant.inviteRechargeReward);

                InviteRechargeRewardRecord record = new InviteRechargeRewardRecord();
                record.setInviterUserId(inviterUserId);
                record.setInviteeUserId(invitee.getUserId());
                record.setRewardType(REWARD_TYPE_DAILY);
                record.setBizDate(bizDay);
                record.setRechargeAmount(row.getTotalAmount());
                record.setRewardAmount(rewardAmount);
                record.setWithdrawBetAmount(withdrawBetAmount);
                record.setConfigId(matched.getId());
                record.setOrderNo(orderNo);
                record.setStatus(0);
                record.setRetryCount(0);
                record.setCreateTime(new Date());
                inviteRechargeRewardRecordMapper.insert(record);

                doGrant(record);
            } catch (Exception e) {
                log.error("每日邀请充值奖励处理异常, statDate={}, userId={}", statDate, row.getUserId(), e);
            }
        }
    }

    private static InviteRechargeRewardConfig matchHighestTier(List<InviteRechargeRewardConfig> configs, BigDecimal amount) {
        if (amount == null || CollUtil.isEmpty(configs)) {
            return null;
        }
        for (InviteRechargeRewardConfig config : configs) {
            if (amount.compareTo(config.getMinRechargeAmount()) >= 0) {
                return config;
            }
        }
        return null;
    }

    private void markRetry(InviteRechargeRewardRecord record, String reason) {
        record.setRetryCount(record.getRetryCount() + 1);
        record.setFailReason(reason != null && reason.length() > 490 ? reason.substring(0, 490) : reason);
        if (record.getRetryCount() >= 5) {
            record.setStatus(2);
            log.error("邀请新人充值奖励发放失败超过最大重试次数, recordId={}", record.getId());
        }
        inviteRechargeRewardRecordMapper.updateById(record);
    }

    private void markFailed(InviteRechargeRewardRecord record, String reason) {
        record.setStatus(2);
        record.setFailReason(reason);
        inviteRechargeRewardRecordMapper.updateById(record);
    }
}
