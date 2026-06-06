package com.common.core.constant;


/**
 * 定义用户操作和系统事件的类型常量类。
 * 所有常量值应保持唯一且不可变，用于标识系统中不同的事件类型。
 *
 * TODO 事件类型
 */
public final class MqEventTypeConstants {

    /** 用户自动充值事件 */
    public static final String USER_AUTO_RECHARGE = "1";

    /** 后台手动上分事件 */
    public static final String BACKEND_MANUAL_TOP_UP = "2";

    /** 用户自动提现事件 */
    public static final String USER_AUTO_WITHDRAWAL = "3";

    /** 后台手动下分（人工下分）事件 */
    public static final String BACKEND_MANUAL_DEBIT = "4";

    /** 用户下注事件 */
    public static final String USER_BET = "5";

    /** 用户下注结算事件 */
    public static final String USER_BET_SETTLEMENT = "6";

    /** 用户注册事件 */
    public static final String USER_REGISTRATION = "7";

    /** 用户领取返水事件 */
    public static final String USER_RECEIVE_REBATE = "8";

    /** 用户领取返佣事件 */
    public static final String USER_RECEIVE_COMMISSION = "9";

    /** 用户活动奖励相关事件 */
    public static final String USER_EVENT_REWARD = "10";

    /** 转盘活动事件 */
    public static final String WHEEL_OF_FORTUNE_ACTIVITY = "11";

    /** 法币提现 */
    public static final String USER_AUTO_LAW_WITHDRAWAL = "12";


    /** 红包领取 */
    public static final String USER_RECEIVE_RED_PACKET = "13";


    /** 代理工资领取 */
    public static final String USER_RECEIVE_AGENT_BONUS = "14";
    /** 转账发送 */
    public static final String USER_TRANSFER_SEND = "15";

    /** 转账接受 */
    public static final String USER_TRANSFER_RECEIVE= "16";


    /** 红包雨 */
    public static final String USER_RED_PACKET_RAIN= "17";
    /** vip等级 */
    public static final String USER_VIP_BONUS= "18";


    /** 彩票奖励 */
    public static final String LOTTERY_AWARD = "19";

    /** 统计校准（投注额/派彩额/输赢/打码量 差值修正） */
    public static final String STATS_CALIBRATION = "20";


    /** 邀请新人充值奖励 */
    public static final String INVITE_RECHARGE_REWARD = "21";

    // 私有构造器防止实例化
    private MqEventTypeConstants() {
        throw new AssertionError("Cannot instantiate constant utility class");
    }



}
