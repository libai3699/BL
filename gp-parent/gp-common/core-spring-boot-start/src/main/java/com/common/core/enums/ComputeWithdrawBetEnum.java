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
public enum ComputeWithdrawBetEnum {

    personAmountUp(1, "人工上分"),//人工上分
    userRecharge(2, "用户充值"),//用户充值

    wheelAward(3, "转盘奖励"),//转盘奖励
    taskAward(4, "任务奖励"),//任务奖励

    ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        ComputeWithdrawBetEnum[] values = values();
        for (ComputeWithdrawBetEnum amountChangeTypeEnum : values) {
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
