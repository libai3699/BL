package com.common.core.enums;


import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RebateBuyTypeEnum {

    /**
     * 播放量
     */
    play_num_rebate(1, "bot.rebateBuy.GMBFL"),
    /**
     * 关键词
     */
    key_rebate(2, "bot.rebateBuy.GMGJC"),
    /**
     * 品牌认证
     */
    brand_rebate(3, "bot.rebateBuy.GMPPRZ"),
    ;

    private Integer type;
    private String typeName;


    public static String getTypeName(Integer type) {
        RebateBuyTypeEnum[] values = values();
        for (RebateBuyTypeEnum amountChangeEnum : values) {
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
