package com.gp.feign.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TtlRunnable;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.core.util.RedisUtil;
import com.common.datasource.function.FrontConsumer;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.nacos.BotConfig;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.service.*;
import com.gp.feign.util.MessageSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class UserBootService {
    @Resource
    private MessageSend messageSend;
    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;
    @Resource
    private ChannelService channelService;
    @Resource
    private ActivityService activityService;
    @Resource
    private ActivityTaskService activityTaskService;
    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private Executor threadPoolTaskExecutor;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private BotItemService botItemService;
    @Resource
    public MybatisBotService mybatisBotService;
    @Resource
    public CecuUtil cecuUtil;
    public void giveBonusAndSendUpMsg(TUser upUser, TUser tUser) {
        BotConfig botConfig = botItemService.getNowBot();
        String dbCode = CecuUtil.getDbCode();
        log.info("执行彩金前 - 调用线程池中{}", dbCode);
        //赠送信息看看这个渠道是否有赠送彩金 赠送彩金了给他赠送
        threadPoolTaskExecutor.execute(TtlRunnable.get(() ->  {
            cecuUtil.cutDbByCode(dbCode);
            log.info("赠送彩金执行 -前- 调用线程池中{}", dbCode);
            this.giveBonus(tUser,botConfig);
            log.info("赠送彩金执行 -后- 调用线程池中{}", dbCode);
        }));
        log.info("执行消息发送前 - 调用线程池中{}", dbCode);
        //异步发消息给 上级告诉他邀请使用hyh
        if(upUser!=null){
            threadPoolTaskExecutor.execute(TtlRunnable.get(() ->  {
                cecuUtil.cutDbByCode(dbCode);
                sendUpMsg(upUser, tUser);
            }));
        }
    }

    private void sendUpMsg(TUser upUser, TUser user) {
        MessagesUtils.setLang(upUser.getLanKey());
        String title = MessagesUtils.get("bot.invite.YQXR");
        String text = MessagesUtils.get("bot.invite.GXXX");
        text = StrUtil.format(text, BotUrlFormat.getCustomerUserName(user.getUserTgUsername(), user.getUserTgName()));
        messageSend.sendMsgAsyNoMessage(upUser, title, text, user.getUserAvatar());
    }

    public void giveBonus(TUser user,BotConfig botConfig) {
        Long channelId = user.getChannelId();
        if(channelId==null){
            return;
        }
        Channel channel = channelService.getById(channelId);
        //看看活动是否开启如果开启了 话即可以
        Activity activity = activityService.queryChannelActivity();
        if (channel==null||activity == null ||channel.getGiveawayAmount()==null||channel.getGiveawayAmount().compareTo(BigDecimal.ZERO)<=0) {
            return;
        }
        BigDecimal codingVolumeMultiple = channel.getCodingVolumeMultiple();
        if(codingVolumeMultiple==null){
            channel.setCodingVolumeMultiple(BigDecimal.TEN);
        }
        MessagesUtils.setLang(channel.getLanKey());
        //然后开始去赠送 查下任务
        ActivityTask activityTask = null;
        BigDecimal activityAward = channel.getGiveawayAmount();
        try {
            activityTask = activityTaskService.querySpecialActivityTask(activity.getId());
        } catch (Exception e) {
            log.info("活动任务未配置");
        }
        ActivityAwardReceive activityAwardReceive = new ActivityAwardReceive();
        String orderNo = SnowIdUtil.getId(OrderConstant.orderActive);
        ActivityTask finalActivityTask = activityTask;

        ExtConsumer extConsumer = () -> {
            activityAwardReceive.setActivityId(finalActivityTask.getActivityId());
            activityAwardReceive.setActivityTaskId(finalActivityTask.getId());
            activityAwardReceive.setType(ActivityAwardConstants.BONUS);
            activityAwardReceive.setUserId(user.getUserId());
            activityAwardReceive.setUserTgId(user.getUserTgId());
            activityAwardReceive.setAward(activityAward);
            activityAwardReceive.setOrderNo(orderNo);
            activityAwardReceive.setIsFixed(0);
            activityAwardReceive.setRatio(BigDecimal.ZERO);
            activityAwardReceive.setAmount(BigDecimal.ZERO);
            activityAwardReceive.setCreateBy(String.valueOf(user.getUserId()));
            activityAwardReceiveService.save(activityAwardReceive);
        };
        //增加  彩金
        //生成领取记录
        List<ChangeExtValueVo> arr = CollUtil.newArrayList();
        ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
        changeExtValueVoBonus.setUserId(user.getUserId());
        changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
        changeExtValueVoBonus.setUpdateValue(activityAward);
        changeExtValueVoBonus.setOrderNo(orderNo);
        changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
        changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
        changeExtValueVoBonus.setOperator(user.getUserTgName());
        //彩金细分
        ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
        changeExtValueVoBonusSmall.setUserId(user.getUserId());
        changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.活动彩金);
        changeExtValueVoBonusSmall.setUpdateValue(activityAward);
        changeExtValueVoBonusSmall.setOrderNo(orderNo);
        changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
        changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
        changeExtValueVoBonusSmall.setOperator(user.getUserTgName());
        arr.add(changeExtValueVoBonusSmall);

        arr.add(changeExtValueVoBonus);
        ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
        changeExtValueVoStatement.setUserId(user.getUserId());
        changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
        changeExtValueVoStatement.setUpdateValue(computeWithdrawBetService.computeMultipleService(channel.getGiveawayAmount(), channel.getCodingVolumeMultiple()));
        changeExtValueVoStatement.setOrderNo(orderNo);
        changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
        changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
        changeExtValueVoStatement.setOperator(user.getUserTgName());
        arr.add(changeExtValueVoStatement);
        String dbCode = CecuUtil.getDbCode();

        userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.taskAward, true, 1, activityAward);
        mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(user.getUserId()).channelId(user.getChannelId())
                .type(MqEventTypeConstants.USER_EVENT_REWARD).bonusAmount(activityAwardReceive.getAward()).orderNo(activityAwardReceive.getOrderNo())
                .productId(dbCode)
                .build());
        //发送一个消息吧

        user.setUserAvatar( mybatisBotService.getMybatisBotConfig().getActivityPic());
//        String msg = new StringBuilder()
//                .append(MessagesUtils.get("bot.activity.HDCJN")).append(" ").append(BigDecimalUtils.trim(activityAward))
//                .toString();
//        messageSend.sendMsgAsy(user, MessagesUtils.get("bot.activity.QDHDFF"), msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
        String msg = new StringBuilder()
                .append("<bot.activity.HDCJN>").append(" ").append(BigDecimalUtils.trim(activityAward))
                .append(" <bot.money.unit>")
                .toString();
        messageSend.sendMsgAsy(user, "<bot.activity.QDHDFF>", msg, UserMessageConstant.ACTIVITY_AWARD, orderNo);
    }

    /***
     * 检查是否在黑名单里
     * @param realIpAddr
     */

    public void dealUserBlackIp(String realIpAddr) {
        if(StrUtil.isEmpty(realIpAddr)){
            return;
        }
        realIpAddr = realIpAddr.replaceAll("\\.","|");
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.userBlackIp,dbCode);
        List<String> arr = redisUtil.getList(key);
        log.info("realIpAddr{}",realIpAddr);
        log.info("arr{}",arr);
        if(CollUtil.isNotEmpty(arr)){
            boolean containsItem = arr.contains(realIpAddr);
            if (containsItem) {
                Assert.isFalse(true,MessagesUtils.get("bot.user.black"));
            }
        }


    }

    public static void main(String[] args) {
        ArrayList<String> arr = CollUtil.newArrayList();
        arr.add("11");
        System.out.println(arr);
    }

    public void dealUserBlackIpOut(String realIpAddr, FrontConsumer frontConsumer) {
        if(StrUtil.isEmpty(realIpAddr)){
            return;
        }
        realIpAddr = realIpAddr.replaceAll("\\.","|");
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.userBlackIp,dbCode);
        List<String> arr = redisUtil.getList(key);

        if(CollUtil.isNotEmpty(arr)){
            boolean containsItem = arr.contains(realIpAddr);
            if (containsItem) {
                Assert.isFalse(true,MessagesUtils.get("bot.user.black"));
                frontConsumer.accept();
            }
        }


    }
}
