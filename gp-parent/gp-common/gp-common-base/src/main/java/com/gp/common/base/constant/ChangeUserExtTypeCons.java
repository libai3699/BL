package com.gp.common.base.constant;

/**
 * @author Administrator
 */
public interface ChangeUserExtTypeCons {
//1 转盘次数, 2 提现打码量, 3 未领取反水, 4 未领取返佣)
    /**
     * 转盘次数
     */
    Integer 用户返水 = 1;
    /**
     * 打码量
     */
    Integer 上级返佣 = 2;
    /**
     * 提现打码量
     */
    Integer 返水领取 = 3;
    /**
     * 未领取反水
     */
    Integer 返佣领取 = 4;

}
