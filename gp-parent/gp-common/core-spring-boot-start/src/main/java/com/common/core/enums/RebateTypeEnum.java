package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RebateTypeEnum {
    //    帐变类型(1 人工上分, 2 人工下分, 3 游戏下单, 4 游戏输赢, 5 用户提款, 6 提款退回, 7 提款成功
    search_level1_rebate(1, "bot.rebateType.YJXYYHSSFL"),
    search_level2_rebate(2, "bot.rebateType.EJXYYHSSFL"),
    group_search_rebate(3, "bot.rebateType.QZSSFL"),

    invite_level1_rebate(4, "bot.rebateType.YJXYYHYQFL"),
    invite_level2_rebate(5,"bot.rebateType.EJXYYHYQFL"),

    buy_ad_play_num_rebate(6, "bot.rebateType.YXFLBFL"),
    buy_ad_key_rebate(7, "bot.rebateType.YXFLGJC"),
    buy_ad_brand_rebate(8, "bot.rebateType.YXFLPPRZ"),

    ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        RebateTypeEnum[] values = values();
        for (RebateTypeEnum amountChangeEnum : values) {
            if (amountChangeEnum.getType().equals(type)) {
                return MessagesUtils.get(amountChangeEnum.getTypeName());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getTypeName(7));
    }

}
