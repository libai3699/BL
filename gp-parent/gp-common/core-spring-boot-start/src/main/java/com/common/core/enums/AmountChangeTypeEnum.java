package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 *  t_amount_change 中的 type(帐变类型)字段
 * @author axing
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AmountChangeTypeEnum {

    personAmountUp(1, "bot.amountChange.RGSF"),//人工上分
    personAmountDown(2, "bot.amountChange.RGXF"),//人工下分
    userRecharge(3, "bot.amountChange.YHCZ"),//用户充值
    userWithdraw(4, "bot.amountChange.YHTX"),//用户提现
    withdrawReject(5, "bot.amountChange.TXJJ"),//提现拒绝 -> 我方出纳,财务拒绝, 金额退回
    withdrawFail(6, "bot.amountChange.TXSB"),//提现失败  ->  上游提现失败, 金额退回
    checkFail(7, "bot.amountChange.SHJJ"),//审核拒绝  -> 我方出纳,财务拒绝, 金额退回
    gameBet(8, "bot.amountChange.XXTZ"),//游戏投注
    gameAward(9, "bot.amountChange.XXFJ"),//游戏返奖
    gameCancel(10, "bot.amountChange.QXTZ"),//游戏取消,投注退回
    rebateAward(11, "bot.amountChange.FSJL"),//反水奖励
    bonusAward(12, "bot.amountChange.CJJL"),//彩金奖励
    wheelAward(13, "bot.amountChange.ZPJL"),//转盘奖励
    taskAward(14, "bot.amountChange.RWJL"),//任务奖励
    commissionAward(15, "bot.amountChange.YJJL"),//佣金奖励
    newPeopleAward(16, "bot.amountChange.XRJL"),//新人奖励
    redEnvelopeReceive(17, "bot.amountChange.HBLQ"),//红包领取
    walletTransfer(18, "bot.amountChange.QBZR"),//钱包转入
    walletOut(19, "bot.amountChange.QBZC"),//钱包转出
    //代理工资领取

    agentAmountReceive(20, "bot.amountChange.DLGZLQ"),//代理工资领取
    //彩金赠与
    bonusGive(21, "bot.amountChange.CJZY"),//赠与彩金
    //彩金接收
    bonusReceive(22, "bot.amountChange.CJJS"),//收到彩金

    ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        AmountChangeTypeEnum[] values = values();
        for (AmountChangeTypeEnum amountChangeTypeEnum : values) {
            if (amountChangeTypeEnum.getType().equals(type)) {
                return MessagesUtils.get(amountChangeTypeEnum.getTypeName());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getTypeName(13));
    }

}
