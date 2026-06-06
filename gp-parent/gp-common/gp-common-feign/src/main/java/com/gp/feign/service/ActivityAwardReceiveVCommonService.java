package com.gp.feign.service;

import cn.hutool.core.collection.CollUtil;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.mq.HelpMoneyEntity;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.service.*;
import com.gp.common.mybatisplus.until.HelpMoneySecretSign;
import com.gp.feign.util.MessageSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ActivityAwardReceiveVCommonService {

    @Resource
    private ActivityTaskService activityTaskService;
    @Resource
    private ActivityAwardService activityAwardService;

    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;
    @Resource
    private MessageSend messageSend;

    @Resource
    private UserExtChangeManage userExtChangeManage;
    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;

    @Resource
    private MqSendEntityService mqSendEntityService;


    @Resource
    public MybatisBotService mybatisBotService;
    @Resource
    public OrderHelpMoneyService orderHelpMoneyService;
    @Resource
    public HelpMoneySecretSign helpMoneySecretSign;
    @Resource
    public UserService userService;


    public void doAward(TUser user, List<ActivityAward> activityAwards, Integer type, Long activityId, BigDecimal amount) {
        activityAwards.stream().forEach(a -> {
            //看看今天是否已经领取过这个奖励了 //大于0 才有意义
            if (a.getIsFixed() == 1 && amount.compareTo(BigDecimal.ZERO) > 0) {
                //这里就是固定金额
                a.setAward((a.getRatio() == null ? BigDecimal.ZERO : a.getRatio()).multiply(amount).setScale(2, BigDecimal.ROUND_DOWN));
                //看下这个金额是否大于 封顶金额
                BigDecimal amountCap = a.getAmountCap();
                if(amountCap!= null && amountCap.compareTo(BigDecimal.ZERO) > 0 && a.getAward().compareTo(amountCap) > 0){
                    a.setAward(amountCap);
                }

            }
            Integer count = activityAwardReceiveService.isReceive(user.getUserId(), a.getActivityTaskId(), activityId, type);
            //等等于0 的时候才去给他加奖励
            if (count == 0) {
                //开始分发奖励
                Integer awardType = a.getType();
                BigDecimal award = a.getAward();

                ActivityAwardReceive activityAwardReceive = new ActivityAwardReceive();
                String orderNo = SnowIdUtil.getId(OrderConstant.orderActive);
                ExtConsumer extConsumer = () -> {
                    ActivityTask activityTask = activityTaskService.getById(a.getActivityTaskId());
                    activityAwardReceive.setActivityId(activityTask.getActivityId());
                    activityAwardReceive.setActivityTaskId(a.getActivityTaskId());
                    activityAwardReceive.setType(a.getType());
                    activityAwardReceive.setUserId(user.getUserId());
                    activityAwardReceive.setUserTgId(user.getUserTgId());
                    activityAwardReceive.setAward(award);
                    activityAwardReceive.setAmount(amount);
                    activityAwardReceive.setRatio(a.getIsFixed() == 1 ? a.getRatio() : BigDecimal.ZERO);
                    activityAwardReceive.setOrderNo(orderNo);
                    activityAwardReceive.setIsFixed(a.getIsFixed());
                    activityAwardReceive.setCreateBy(String.valueOf(user.getUserId()));
                    activityAwardReceiveService.save(activityAwardReceive);
                };
                //看看是转盘还是 彩金 彩金的话
                if (Objects.equals(awardType, ActivityAwardConstants.BONUS)) {
                    //给他减去积分
                    String dbCode = CecuUtil.getDbCode();
                    userService.judgeUserScore(award, dbCode);
                    //增加  彩金
                    //生成领取记录
                    List<ChangeExtValueVo> arr = CollUtil.newArrayList();
                    ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
                    changeExtValueVoBonus.setUserId(user.getUserId());
                    changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
                    changeExtValueVoBonus.setUpdateValue(a.getAward());
                    changeExtValueVoBonus.setOrderNo(orderNo);
                    changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
                    changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
                    changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
                    changeExtValueVoBonus.setOperator(user.getUserTgName());
                    arr.add(changeExtValueVoBonus);
                    //彩金细分
                    ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
                    changeExtValueVoBonusSmall.setUserId(user.getUserId());
                    changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.活动彩金);
                    changeExtValueVoBonusSmall.setUpdateValue(a.getAward());
                    changeExtValueVoBonusSmall.setOrderNo(orderNo);
                    changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
                    changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
                    changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
                    changeExtValueVoBonusSmall.setOperator(user.getUserTgName());
                    arr.add(changeExtValueVoBonusSmall);
                    ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
                    changeExtValueVoStatement.setUserId(user.getUserId());
                    changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
                    changeExtValueVoStatement.setUpdateValue(computeWithdrawBetService.computeActivityService(a.getAward(), a.getWithdrawBonusRatio(), a.getId()));
                    changeExtValueVoStatement.setOrderNo(orderNo);
                    changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
                    changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
                    changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
                    changeExtValueVoStatement.setOperator(user.getUserTgName());
                    arr.add(changeExtValueVoStatement);
                    //发送商户积分扣减
                    userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.taskAward, true, 1, a.getAward());
                    userService.sendJudgeUserScoreMq(award, dbCode,MerchantChangeTypeConstants.ACTIVITY_REWARD,MerchantOrderTypeConstant.ACTIVITY_REWARD,"活动奖励");
                    //发消息
                    MessagesUtils.setLang(user.getLanKey());
                    user.setUserAvatar(mybatisBotService.getMybatisBotConfig().getActivityPic());
//                    String msg = new StringBuilder()
//                            .append(MessagesUtils.get("bot.activity.HDCJN")).append(" ").append(BigDecimalUtils.trim(a.getAward()))
//                            .toString();
//                    messageSend.sendMsgAsy(user, MessagesUtils.get("bot.activity.HDJLFF"), msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
                    String msg = new StringBuilder()
                            .append("<bot.activity.HDCJN>").append(" ").append(BigDecimalUtils.trim(a.getAward()))
                            .append(" <bot.money.unit>")
                            .toString();
                    messageSend.sendMsgAsy(user, "<bot.activity.HDJLFF>", msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
                    mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(user.getUserId()).channelId(user.getChannelId())
                            .type(MqEventTypeConstants.USER_EVENT_REWARD).bonusAmount(activityAwardReceive.getAward()).orderNo(activityAwardReceive.getOrderNo())
                            .productId(dbCode)
                            .build());

                    //发送救济金mq计算赠送金额
                    mqSendEntityService.sendHelpMoneyEntity(HelpMoneyEntity.builder().userId(user.getUserId()).tgUserId(user.getUserTgId())
                            .betMoney(BigDecimal.ZERO).winMoney(BigDecimal.ZERO).bonusMoney(activityAwardReceive.getAward()).gameTypeCode("0").dayStr(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()))
                            .productId(dbCode)
                            .build());
                }
                if (Objects.equals(awardType, ActivityAwardConstants.WHEELNUM)) {
                    List<ChangeExtValueVo> arr = CollUtil.newArrayList();
                    ChangeExtValueVo changeExtValueVo = new ChangeExtValueVo();
                    changeExtValueVo.setUserId(user.getUserId());
                    changeExtValueVo.setExtType(UserExtTypeCons.转盘次数);
                    changeExtValueVo.setUpdateValue(a.getAward());
                    changeExtValueVo.setOrderNo(orderNo);
                    changeExtValueVo.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
                    changeExtValueVo.setAccountType(AccountChangeTypeConstants.INCOME);
                    changeExtValueVo.setChangeType(BaseGameInfoCons.UserExtChangeType.转盘增加);
                    changeExtValueVo.setOperator(user.getUserTgName());
                    arr.add(changeExtValueVo);
                    userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.taskAward, false, 1, BigDecimal.ZERO);
                    //发消息 hyh
                    MessagesUtils.setLang(user.getLanKey());
                    user.setUserAvatar(mybatisBotService.getMybatisBotConfig().getActivityPic());
                    String dbCode = CecuUtil.getDbCode();
//                    String msg = new StringBuilder()
//                            .append(MessagesUtils.get("bot.activity.HDZPCSN")).append(" ").append(BigDecimalUtils.trim(a.getAward())).toString();
//                    messageSend.sendMsgAsy(user, MessagesUtils.get("bot.activity.HDJLFF"), msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
                    String msg = new StringBuilder()
                            .append("<bot.activity.HDZPCSN>").append(" ").append(BigDecimalUtils.trim(a.getAward()))
                            .append(" <bot.wheel.unit>")
                            .toString();

                    messageSend.sendMsgAsy(user, "<bot.activity.HDJLFF>", msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
                    mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(user.getUserId()).channelId(user.getChannelId())
                            .type(MqEventTypeConstants.USER_EVENT_REWARD).wheelNum(activityAwardReceive.getAward().intValue()).orderNo(activityAwardReceive.getOrderNo())
                            .productId(dbCode)
                            .build());
                    //发送mq

                }
            }
        });
    }

    public void doActivityAward(TUser user, Activity activity) {
        String gameTypeCode = activity.getGameTypeCode();
        if(gameTypeCode.equals("0")){
            activity.setGameTypeCode("-1");
        }
        String yesterday = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDateDays(new Date(), -1));
        OrderHelpMoney orderHelpMoney = orderHelpMoneyService.queryOrderHelpMoney(user.getUserId(), activity.getGameTypeCode(), yesterday);
        if (orderHelpMoney == null || orderHelpMoney.getReceiveMoney().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        helpMoneySecretSign.checkSign(orderHelpMoney);
        Long awardId = orderHelpMoney.getAwardId();
        if (awardId == null) {
            return;
        }
        ActivityAward activityAward = activityAwardService.getById(awardId);
        ActivityAwardReceive activityAwardReceive = new ActivityAwardReceive();
        String orderNo = SnowIdUtil.getId(OrderConstant.orderActive);
        ExtConsumer extConsumer = () -> {
            activityAwardReceive.setActivityId(activity.getId());
            activityAwardReceive.setActivityTaskId(activityAward.getActivityTaskId());
            activityAwardReceive.setUserId(user.getUserId());
            activityAwardReceive.setUserTgId(user.getUserTgId());
            activityAwardReceive.setType(activityAward.getType());
            activityAwardReceive.setAward(orderHelpMoney.getReceiveMoney());
            activityAwardReceive.setOrderNo(orderNo);
            activityAwardReceive.setAmount(orderHelpMoney.getCalMoney());
            activityAwardReceive.setRatio(activityAward.getRatio());
            activityAwardReceive.setIsFixed(activityAward.getIsFixed());
            activityAwardReceive.setCreateBy(String.valueOf(user.getUserId()));
            activityAwardReceiveService.save(activityAwardReceive);
            orderHelpMoney.setIsReceive(1);
            helpMoneySecretSign.dealSign(orderHelpMoney);
            orderHelpMoneyService.updateById(orderHelpMoney);
        };
        BigDecimal award = orderHelpMoney.getReceiveMoney();
        if (Objects.equals(activityAward.getType(), ActivityAwardConstants.BONUS)) {
            //给他减去积分
            String dbCode = CecuUtil.getDbCode();
            userService.judgeUserScore(award, dbCode);
            //增加  彩金
            //生成领取记录
            List<ChangeExtValueVo> arr = CollUtil.newArrayList();
            ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
            changeExtValueVoBonus.setUserId(user.getUserId());
            changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
            changeExtValueVoBonus.setUpdateValue(orderHelpMoney.getReceiveMoney());
            changeExtValueVoBonus.setOrderNo(orderNo);
            changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonus.setOperator(user.getUserTgName());
            arr.add(changeExtValueVoBonus);
            //彩金细分
            ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
            changeExtValueVoBonusSmall.setUserId(user.getUserId());
            changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.活动彩金);
            changeExtValueVoBonusSmall.setUpdateValue(orderHelpMoney.getReceiveMoney());
            changeExtValueVoBonusSmall.setOrderNo(orderNo);
            changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonusSmall.setOperator(user.getUserTgName());
            arr.add(changeExtValueVoBonusSmall);
            ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
            changeExtValueVoStatement.setUserId(user.getUserId());
            changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
            changeExtValueVoStatement.setUpdateValue(computeWithdrawBetService.computeActivityService(orderHelpMoney.getReceiveMoney(), activityAward.getWithdrawBonusRatio(), awardId));
            changeExtValueVoStatement.setOrderNo(orderNo);
            changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
            changeExtValueVoStatement.setOperator(user.getUserTgName());
            arr.add(changeExtValueVoStatement);
            userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.taskAward, true, 1, orderHelpMoney.getReceiveMoney());
            //发送商户积分扣减
            userService.sendJudgeUserScoreMq(award, dbCode,MerchantChangeTypeConstants.ACTIVITY_REWARD,MerchantOrderTypeConstant.ACTIVITY_REWARD,"活动奖励");
            //发消息
            MessagesUtils.setLang(user.getLanKey());
            user.setUserAvatar(mybatisBotService.getMybatisBotConfig().getActivityPic());
//            String msg = new StringBuilder()
//                    .append(MessagesUtils.get("bot.activity.HDCJN")).append(" ").append(BigDecimalUtils.trim(orderHelpMoney.getReceiveMoney()))
//                    .toString();
//            messageSend.sendMsgAsy(user, MessagesUtils.get("bot.activity.HDJLFF"), msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);

            String msg = new StringBuilder()
                    .append("<bot.activity.HDCJN>").append(" ").append(BigDecimalUtils.trim(orderHelpMoney.getReceiveMoney()))
                    .append(" <bot.money.unit>")
                    .toString();
            messageSend.sendMsgAsy(user, "<bot.activity.HDJLFF>", msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
            mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(user.getUserId()).channelId(user.getChannelId())
                    .type(MqEventTypeConstants.USER_EVENT_REWARD).bonusAmount(activityAwardReceive.getAward()).orderNo(activityAwardReceive.getOrderNo())
                    .productId(dbCode)
                    .build());

            //发送救济金mq计算赠送金额
            mqSendEntityService.sendHelpMoneyEntity(HelpMoneyEntity.builder().userId(user.getUserId()).tgUserId(user.getUserTgId())
                    .betMoney(BigDecimal.ZERO).winMoney(BigDecimal.ZERO).bonusMoney(activityAwardReceive.getAward()).gameTypeCode("0").dayStr(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()))
                    .productId(dbCode)
                    .build());
        }
        if (Objects.equals(activityAward.getType(), ActivityAwardConstants.WHEELNUM)) {
            List<ChangeExtValueVo> arr = CollUtil.newArrayList();
            ChangeExtValueVo changeExtValueVo = new ChangeExtValueVo();
            changeExtValueVo.setUserId(user.getUserId());
            changeExtValueVo.setExtType(UserExtTypeCons.转盘次数);
            changeExtValueVo.setUpdateValue(orderHelpMoney.getReceiveMoney());
            changeExtValueVo.setOrderNo(orderNo);
            changeExtValueVo.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVo.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVo.setChangeType(BaseGameInfoCons.UserExtChangeType.转盘增加);
            changeExtValueVo.setOperator(user.getUserTgName());
            arr.add(changeExtValueVo);
            userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.taskAward, false, 1, BigDecimal.ZERO);
            //发消息 hyh
            MessagesUtils.setLang(user.getLanKey());
            user.setUserAvatar(mybatisBotService.getMybatisBotConfig().getActivityPic());
            String dbCode = CecuUtil.getDbCode();
//            String msg = new StringBuilder()
//                    .append(MessagesUtils.get("bot.activity.HDZPCSN")).append(" ").append(BigDecimalUtils.trim(orderHelpMoney.getReceiveMoney())).toString();
//            messageSend.sendMsgAsy(user, MessagesUtils.get("bot.activity.HDJLFF"), msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
            String msg = new StringBuilder()
                    .append("<bot.activity.HDZPCSN>").append(" ").append(BigDecimalUtils.trim(orderHelpMoney.getReceiveMoney()))
                    .append(" <bot.wheel.unit>")
                    .toString();
            messageSend.sendMsgAsy(user, "<bot.activity.HDJLFF>", msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
            mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(user.getUserId()).channelId(user.getChannelId())
                    .type(MqEventTypeConstants.USER_EVENT_REWARD).wheelNum(activityAwardReceive.getAward().intValue()).orderNo(activityAwardReceive.getOrderNo())
                    .productId(dbCode)
                    .build());
        }
    }
}
