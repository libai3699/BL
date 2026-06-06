package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RebateFromTypeEnum {


    /**
     * 搜索返利
     */
    search_rebate(1, "bot.rebateFrom.SSFL"),
    /**
     * 邀请返利
     */
    invite_rebate(2,  "bot.rebateFrom.YQFL"),
    /**
     * 营销返利
     */
    buy_rebate(3,  "bot.rebateFrom.YXFL"),
    ;
    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        RebateFromTypeEnum[] values = values();
        for (RebateFromTypeEnum amountChangeEnum : values) {
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
