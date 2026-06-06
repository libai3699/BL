package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TtlRunnable;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.common.rabbitmq.service.RabbitMqTemplate;
import com.gp.common.base.constant.AccountChangeTypeConstants;
import com.gp.common.base.constant.BaseGameInfoCons;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.constant.UserExtTypeCons;
import com.gp.common.base.constant.mq.MqEnum;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserExt;
import com.gp.common.mybatisplus.entity.UserExtChange;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.until.UserExtChangeSign;
import com.gp.common.mybatisplus.until.UserExtSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class PayUntil {
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    public UserExtService userExtService;
    @Resource
    public UserService userService;
    @Resource
    public RedisUtil redisUtil;
    @Resource
    public CecuUtil cecuUtil;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private UserWalletService userWalletService;
    @Resource
    private UserExtSign userExtSign;
    @Resource
    private UserExtChangeSign userExtChangeSign;

    @Resource
    private UserExtChangeService userExtChangeService;
    @Resource
    private InviteRechargeRewardRecordService inviteRechargeRewardRecordService;

    @Resource
    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;
    @Resource
    private RabbitMqTemplate rabbitMqTemplate;

    public Boolean getIsNewUser(Long userId) {
        Boolean flag = false;
        //查询用户
        //需要看看用户之前有没有充值
        BigDecimal userCzAmount = userExtService.queryUSerExt(userId, UserExtTypeCons.累计充值).getAmount();
        if (userCzAmount.compareTo(BigDecimal.ZERO) == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 不是异步的
     *
     * @param tUser
     * @param amount
     */
    public void dealMoney(TUser tUser, BigDecimal amount, boolean isNewUser) {
        String dbCode = CecuUtil.getDbCode();
        //发送mq发送充值事件
        mqSendEntityService.sendRechargeConsume(com.gp.common.mybatisplus.mq.RechargeEntity.builder().fromUserId(tUser.getUserId())
                .type(0).fromAmount(amount)
                .productId(dbCode)
                .build(), MqEnum.rechargeMq);

        //需要两个值一个是 用户当天的充值量 一个是用户充值总量
        //1 每日, 2 永久 3 周 4
        String today = StrUtil.format(RedisKey.USER_RECHARGE_TODAY, dbCode, tUser.getUserId());
        HashMap<String, String> todayMap = MapUtil.newHashMap();
        todayMap.put("type", "1");
        todayMap.put("key", today);
        String all = StrUtil.format(RedisKey.USER_RECHARGE, dbCode, tUser.getUserId());
        HashMap<String, String> allMap = MapUtil.newHashMap();
        allMap.put("type", "2");
        allMap.put("key", all);
        String week = StrUtil.format(RedisKey.USER_RECHARGE_WEEK, dbCode, tUser.getUserId());
        HashMap<String, String> weekMap = MapUtil.newHashMap();
        weekMap.put("type", "3");
        weekMap.put("key", week);
        String month = StrUtil.format(RedisKey.USER_RECHARGE_MONTH, dbCode, tUser.getUserId());
        HashMap<String, String> monthMap = MapUtil.newHashMap();
        monthMap.put("type", "4");
        monthMap.put("key", month);
        String first = StrUtil.format(RedisKey.USER_RECHARGE_FIRST, dbCode, tUser.getUserId());
        String first24 = StrUtil.format(RedisKey.USER_RECHARGE_TODAY_24, dbCode, tUser.getUserId());

        List<HashMap<String, String>> hashMaps = CollUtil.newArrayList(todayMap, allMap, weekMap, monthMap);
        if (isNewUser) {
            HashMap<String, String> firstMap = MapUtil.newHashMap();
            firstMap.put("type", "5");
            firstMap.put("key", first);
            List<HashMap<String, String>> firstHashMaps = CollUtil.newArrayList(firstMap);
            redisUtil.updateRedisValues(firstHashMaps, amount);

            HashMap<String, String> first24Map = MapUtil.newHashMap();
            first24Map.put("type", "8");
            first24Map.put("key", first24);
            List<HashMap<String, String>> first24HashMaps = CollUtil.newArrayList(first24Map);
            redisUtil.updateRedisValues(first24HashMaps, amount);

            // 邀请新人首充奖励（异步，给邀请人发奖）
            threadPoolTaskExecutor.execute(TtlRunnable.get(() -> {
                try {
                    cecuUtil.cutDbByCode(dbCode);
                    inviteRechargeRewardRecordService.grantInviteRechargeReward(tUser, amount);
                } catch (Exception e) {
                    log.error("邀请新人充值奖励触发异常, userId={}, amount={}", tUser.getUserId(), amount, e);
                }
            }));

            return;
        }
        //这里走异步吧
        //异步任务
        threadPoolTaskExecutor.execute(TtlRunnable.get(() -> {
            cecuUtil.cutDbByCode(dbCode);
            //判断用户二次充值
            Integer i = userService.judgeUserSecondRecharge(tUser.getUserId());
            //次数为两次时采取存redis
            if (i == 2) {
                List<HashMap<String, String>> secondHashMaps = CollUtil.newArrayList();
                String second = StrUtil.format(RedisKey.USER_RECHARGE_SECOND, dbCode, tUser.getUserId());
                HashMap<String, String> secondMap = MapUtil.newHashMap();
                secondMap.put("type", "6");
                secondMap.put("key", second);
                secondHashMaps.add(secondMap);
                redisUtil.updateRedisValues(secondHashMaps, amount);
            }

            //次数为三次时采取存redis
            if (i == 3) {
                List<HashMap<String, String>> threeHashMaps = CollUtil.newArrayList();
                String three = StrUtil.format(RedisKey.USER_RECHARGE_THREE, dbCode, tUser.getUserId());
                HashMap<String, String> threeMap = MapUtil.newHashMap();
                threeMap.put("type", "7");
                threeMap.put("key", three);
                threeHashMaps.add(threeMap);
                redisUtil.updateRedisValues(threeHashMaps, amount);
            }
        }));

        //查询是否是二次充值



        redisUtil.updateRedisValues(hashMaps, amount);
//        BotConfig botConfig = botItemService.getNowBot();
//        //开始触发任务 建议发消息给mq 一会在改吧 这又加了个需求就是 用户充值赠送用户次数
//        SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.ftTaskMq.getExchange(), MqEnum.ftTaskMq.getKey(), MessageBody.builder()
//                .data(tUser).botUsername(botConfig.getGameBotUsername()).productId(dbCode).build());
    }

    /**
     * 这里去补一下用户提现打码量
     *
     * @param tUser
     */
    public void dealUserWithdrawBet(TUser tUser) {
        try {
            BigDecimal userAmount = userWalletService.getUserAmount(tUser.getUserId());
            if (userAmount.compareTo(BigDecimal.ONE) >= 0) {
                return;
            }
            //去处理用户打码量
            UserExt codeUserExt = userExtService.queryUSerExt(tUser.getUserId(), UserExtTypeCons.打码量);
            //获取提现打码量
            UserExt txCodeUserExt = userExtService.queryUSerExt(tUser.getUserId(), UserExtTypeCons.提现打码量);
            if (null != txCodeUserExt) {
                BigDecimal oldAmount = txCodeUserExt.getAmount();
                BigDecimal newAmount = BigDecimal.ZERO;
                if (null != codeUserExt) {
                    newAmount = codeUserExt.getAmount();
                }
                BigDecimal subtract = oldAmount.subtract(newAmount);
                //判断正还是负
                int accountType;
                int type;
                if (subtract.compareTo(BigDecimal.ZERO) > 0) {
                    //正-提现打码量大于累计打码量
                    accountType = AccountChangeTypeConstants.INCOME;
                    type = BaseGameInfoCons.UserExtChangeType.人工增加提现打码量;
                } else if (subtract.compareTo(BigDecimal.ZERO) < 0) {
                    //负-提现打码量小于累计打码量
                    accountType = AccountChangeTypeConstants.EXPENSE;
                    type = BaseGameInfoCons.UserExtChangeType.人工减少提现打码量;
                } else {
                    //相等-不产生账变记录,不改变提现打码量
                    return;
                }
                txCodeUserExt.setAmount(newAmount);
                userExtSign.dealSign(txCodeUserExt);
                userExtService.updateById(txCodeUserExt);
                // 添加账变记录
                UserExtChange userExtChange = new UserExtChange();
                userExtChange.setExtType(txCodeUserExt.getType());
                userExtChange.setUserId(txCodeUserExt.getUserId());
                userExtChange.setTgUserId(txCodeUserExt.getUserTgId());
                TUser user = userService.getById(txCodeUserExt.getUserId());
                userExtChange.setSuperUserId(user.getSuperUserId());
                userExtChange.setChannelId(user.getChannelId());
                userExtChange.setOrderNo("");
                userExtChange.setAccountType(accountType);
                userExtChange.setType(type);
                userExtChange.setOrderType(BaseGameInfoCons.UserExtOrderType.人工操作提现打码量订单);
                userExtChange.setAmount(subtract.abs());
                userExtChange.setOldAmount(oldAmount);
                userExtChange.setNewAmount(newAmount);
                userExtChange.setRemark("清空未完成打码量");
                userExtChangeSign.dealSign(userExtChange);
                userExtChangeService.save(userExtChange);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

