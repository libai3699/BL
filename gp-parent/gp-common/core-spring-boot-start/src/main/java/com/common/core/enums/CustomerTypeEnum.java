package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CustomerTypeEnum {

    /**
     * 财务客服
     */
    FinancialCustomer(1, "bot.customer.CWFH"),
    /**
     * 反馈客服
     */
    FeedbackCustomer(2, "bot.customer.FKKF"),
    /**
     * 代理客服
     */
    AgentCustomer(3, "bot.customer.DLKF"),
    ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        CustomerTypeEnum[] values = values();
        for (CustomerTypeEnum amountChangeEnum : values) {
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
