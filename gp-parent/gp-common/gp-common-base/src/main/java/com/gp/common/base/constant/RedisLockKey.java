package com.gp.common.base.constant;

/**
 * @author axing
 */
public interface RedisLockKey {

    String financeCheckLock = "{}:ft:lock:financeCheck:{}";
    String riskCheckLock = "{}:ft:lock:riskCheck:{}";


    /**
     * 平台编码, 订单号
     */
    String gameOrderLock = "wallet:lock:gameOrder:{}:{}";

    /***
     * 注单id
     */
    String termOrderId = "ft:lock:termOrderId:{}";
    /**
     * 平台编码, 订单号
     */
    String gameNotifyLock = "gameNotify:{}:{}";


    /**
     * 平台编码, 订单号
     */
    String DgRestSettleGameNotifyLock = "{}:restSettle:gameNotify:{}:{}";
    String notifyLock = "{}:wallet:lock:notify:{}";

    String withdrawNotifyLock = "{}:wallet:lock:withdrawNotify:{}";
    String messageLock = "{}:wallet:lock:message:{}";




    String turnWater = "{}:wallet:lock:turnWater:{}:{}";


    String taskAwardReceiveLock = "{}:ft:lock:task:award:receive:{}";
    String activityAwardReceiveLock = "{}:ft:lock:activity:award:receive:{}";
    /**
     * 取消订单缓存
     */
    String gameCancelRedis = "game:cancel:{}:{}";
    String gameFishRedis = "game:fish:{}:{}";

    String redPacket = "{}:ft:lock:redPacket:{}:{}";

    String redPacketRain = "{}:ft:lock:redPacketRain:{}:{}";

    String privateRedPacketRain = "{}:ft:lock:privateRedPacketRain:{}:{}";


    //每次获取充值地址之后过多久能再次充值
    String payInterval = "{}:pay:interval:{}";
}
