package com.common.core.enums;

import cn.hutool.core.util.ArrayUtil;
import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局会员来源枚举
 */
@AllArgsConstructor
@Getter
public enum UserSourceEnum  {

    TELEGRAM(0, "飞机"),
    H5(1, "H5用户");


    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String typeName;


    public static String getTypeName(Integer type) {
        UserSourceEnum[] values = values();
        for (UserSourceEnum userSourceEnum : values) {
            if (userSourceEnum.getType().equals(type)) {
                return userSourceEnum.getTypeName();
            }
        }
        return null;
    }

}
