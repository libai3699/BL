package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RebateConfigEnum {


    /**
     * 搜索返利一级配置
     */
    search_level1_rebate("search_level1_rebate", "bot.rebateConfig.SSYJYH"),
    /**
     * 搜索返利二级配置
     */
    search_level2_rebate("search_level2_rebate", "bot.rebateConfig.SSEJYH"),
    /**
     * 搜索群主返利配置
     */
    group_search_rebate("group_search_rebate", "bot.rebateConfig.QZSSFL"),
    /**
     * 邀请返利一级配置
     */
    invite_level1_rebate("invite_level1_rebate", "bot.rebateConfig.YQYJYHFL"),
    /**
     * 邀请返利二级配置
     */
    invite_level2_rebate("invite_level2_rebate", "bot.rebateConfig.YQYEYHFL"),
    /**
     * 购买配置
     */
    buy_ad_level1_rate("buy_ad_level1_rate", "bot.rebateConfig.GGGMFL"),
    ;
    private String key;
    private String typeName;


    public static String getTypeName(String key) {
        RebateConfigEnum[] values = values();
        for (RebateConfigEnum amountChangeEnum : values) {
            if (amountChangeEnum.getKey().equals(key)) {
                return MessagesUtils.get(amountChangeEnum.getTypeName());
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getTypeName("buy_ad_level1_rate"));
    }

}
