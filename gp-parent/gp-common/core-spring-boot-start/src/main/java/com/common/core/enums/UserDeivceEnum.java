package com.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局会员来源枚举
 */
@AllArgsConstructor
@Getter
public enum UserDeivceEnum {
    //1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5
    androidTg(1, "androidTg"),
    iosTg(2, "iosTg"),
    pcTg(3, "pcTg"),
    macTg(4, "macTg"),
    pc(5, "pc"),
    h5(6, "h5");


    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String typeName;


    public static String getTypeName(Integer type) {
        UserDeivceEnum[] values = values();
        for (UserDeivceEnum userSourceEnum : values) {
            if (userSourceEnum.getType().equals(type)) {
                return userSourceEnum.getTypeName();
            }
        }
        return null;
    }

}
