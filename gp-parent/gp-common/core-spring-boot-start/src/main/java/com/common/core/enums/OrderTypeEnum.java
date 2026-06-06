package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * t_amount_change 中的 order_type(订单类型)字段
 * @author axing
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderTypeEnum {

    personAmount(1, "bot.orderType.ZGSXFDD"),//人工上下分订单
    userRecharge(2, "bot.orderType.CZDD"),//充值订单
    userWithdraw(3, "bot.orderType.TXDD"),//提现订单
    gameOrder(4, "bot.orderType.YXDD"),//游戏订单
    rebateAward(5, "bot.orderType.FSDD"),//反水订单
    bonusAward(6, "bot.orderType.CJDD"),//彩金订单
    wheelAward(7, "bot.orderType.ZPJL"),//转盘奖励
    taskAward(8, "bot.orderType.RWJL"),//任务奖励
    commissionAward(9, "bot.orderType.YJJL"),//佣金奖励
    newPeopleAward(10, "bot.orderType.XRJL"),//新人奖励
    redAward(11, "bot.orderType.HBJL"),//红包奖励

    transfer(12, "bot.orderType.ZZDD"),//红包奖励
    upgradeAward(13, "bot.orderType.SJJL"),//升级奖励
    weeklyAward(14, "bot.orderType.ZJL"),//周奖励
    monthlyAward(15, "bot.orderType.YJL"),//月奖励
    rechargeAward(16, "bot.orderType.CZJL"),//充值奖励
    lossAward(17, "bot.orderType.KSJL"),//亏损奖励
    ;
    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        OrderTypeEnum[] values = values();
        for (OrderTypeEnum amountChangeTypeEnum : values) {
            if (amountChangeTypeEnum.getType().equals(type)) {
                return MessagesUtils.get(amountChangeTypeEnum.getTypeName());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getTypeName(7));
    }

}
