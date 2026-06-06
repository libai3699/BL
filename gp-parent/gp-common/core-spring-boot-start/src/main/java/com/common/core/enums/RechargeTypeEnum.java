package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RechargeTypeEnum {
    official_wallet(1, "bot.index.GFQB"),
    direct_topUp(2, "bot.index.ZC"),
 ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        RechargeTypeEnum[] values = values();
        for (RechargeTypeEnum amountChangeEnum : values) {
            if (amountChangeEnum.getType().equals(type)) {
                return MessagesUtils.get(amountChangeEnum.getTypeName());
            }
        }
        return null;
    }


}
