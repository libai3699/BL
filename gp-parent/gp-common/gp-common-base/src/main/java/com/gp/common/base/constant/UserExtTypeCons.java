package com.gp.common.base.constant;

/**
 * t_user_ext_change 中的 ext_type 用户额外属性类型 字段
 * @author Administrator
 */
public interface UserExtTypeCons {
//1 转盘次数, 2 提现打码量, 3 未领取反水, 4 未领取返佣)
    /**
     * 转盘次数
     */
    Integer 转盘次数 = 1;
    /**
     * 打码量
     */
    Integer 打码量 = 2;
    /**
     * 提现打码量
     */
    Integer 提现打码量 = 3;
    /**
     * 未领取反水
     */
    Integer 未领取反水 = 4;
    /**
     * 未领取返佣
     */
    Integer 未领取返佣 = 5;
    /**
     * 彩金
     */
    Integer 彩金 = 6;
    /**
     * 累计充值
     */
    Integer 累计充值 = 7;

    /**
     * 充值彩金
     */
    Integer 充值彩金 = 8;
    /**
     * 转盘彩金
     */
    Integer 转盘彩金 = 9;
    /**
     * 活动彩金
     */
    Integer 活动彩金 = 10;
    /**
     * 未领取代理工资
     */
    Integer 未领取代理工资 = 11;
}
