package com.common.core.constant;

/**
 * 返回状态码
 *
 * @author ruoyi
 */
public interface OrderConstant
{
    /**
     * 充值
     */
    public static final String OrderAmount = "OA";

    /**
     * 提现
     */
    public static final String OrderWithdraw = "OW";

    /**
     * 注单
     */
    public static final String OrderTerm = "OT";

    /**
     * 反水领取注单
     */
    public static final String OrderRebateReceive = "ORR";

    /**
     * 转盘领取
     */
    public static final String orderWheel = "OWH";
    /**
     * 活动奖励
     */
    public static final String orderActive = "OAE";

    /**
     * 法币提现
     */
    public static final String LawOrderWithdraw = "LOW";
    /**
     * 人工上下分
     */
    public static final String  OrderPerson= "W";
    /**
     * 商户上下分
     */
    public static final String  OrderMerchantScore= "MS";

    /**
     * 红包接收
     */
    public static final String OrderRedEnvelopeReceive = "RER";
    /**
     * 红包发放
     */

    public static final String OrderRedEnvelopeSend = "ORS";

    /**
     * 佣金订单
     */
    public static final String OrderCommission = "OC";
    /**
     * 转账
     */
    public static final String OrderTransferSend= "OTS";
    /**
     * 转账接受
     */
    public static final String OrderTransferReceive= "OTR";

    /**
     * 转账接受
     */
    public static final String levelBonus= "LB";
    /**
     * 红包雨
     */
    public static final String redPacketRain= "RPR";
    public static final String privateRed= "PRD";

    /**
     * 返佣 渠道id 用户id 日期
     */
    public static final String receiveRebateOrder = "RRO-{}-{}-{}";
    /**
     * 彩票奖励订单 userLotteryAward
     */
    public static final String lotteryAward = "ULA";

    /**
     * 邀请新人充值奖励
     */
    public static final String inviteRechargeReward = "IRR";

}
