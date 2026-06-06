package com.gp.common.base.constant.mq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 存储队列桶里的消息推送到mq
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MqEnum {

    //x限流桶的消息
    ftBotSendMsgMq("ftBotSendMsg", "ftBotSendMsgExchange", "ftBotSendMsgKey"),
    //订单处理
    ftBotSendTermMq("ftBotSendTerm", "ftBotSendTermExchange", "ftBotSendTermKey"),
    //任务处理mq
//    ftTaskMq("ftTask", "ftTaskExchange", "ftTaskKey"),
    //订单处理mq
    orderTaskMq("orderTask", "orderTaskExchange", "orderTaskKey"),
    //订单处理mq
    helpMoneyMq("helpMoney", "helpMoneyExchange", "helpMoneyKey"),
    newRedPacketMq("newRedPacket", "newRedPacketExchange", "newRedPacketKey"),
    //商户扣除积分
    merchantScoreMq("merchantScore", "merchantScoreExchange", "merchantScoreKey"),

    otherDealMq("otherDealMq", "otherDealMqExchange", "otherDealMqKey"),

    rebateMq("rebateMq", "rebateMqExchange", "rebateMqKey"),

    superRebateMq("superRebateMq", "superRebateMqExchange", "superRebateMqKey"),
    channelRebateMq("channelRebateMq", "channelRebateMqExchange", "channelRebateMqKey"),
    codeAmountMq("codeAmountMq", "codeAmountMqExchange", "codeAmountMqKey"),

    //订单es同步
//    orderEsMq("orderEsMq", "orderEsMqExchange", "orderEsMqKey"),
//    orderListEsMq("ordersEsMq", "ordersEsMqExchange", "ordersEsMqKey"),
    //账变es同步
//    amountEsMq("amountEsMq", "amountEsMqExchange", "amountEsMqKey"),
//    amountListEsMq("amountsEsMq", "amountsEsMqExchange", "amountsEsMqKey"),
    //附加数据(额外属性)账变es同步
//    extAmountEsMq("extAmountEsMq", "extAmountEsMqExchange", "extAmountEsMqKey"),
//    extAmountListEsMq("extAmountListEsMq", "extAmountListEsMqExchange", "extAmountListEsMqKey"),
    //充值
    rechargeMq("rechargeMq", "rechargeMqExchange", "rechargeMqKey"),
    //统计校准（投注额/派彩额/输赢/打码量 差值修正）
    calibrationStatsMq("calibrationStatsMq", "calibrationStatsMqExchange", "calibrationStatsMqKey"),
    ;
    private String queue;
    private String exchange;
    private String key;

}
