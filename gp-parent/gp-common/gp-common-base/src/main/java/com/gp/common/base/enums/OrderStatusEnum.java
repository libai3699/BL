package com.gp.common.base.enums;

import lombok.Getter;

/**
 * @author axing
 */
@Getter
public enum OrderStatusEnum {
    WAITING_FOR_PAYMENT(0), // 订单等待支付状态，对应值为 0
    PAID(1), // 订单已支付状态，对应值为 1
    CANCELED(2); // 订单已取消状态，对应值为 2

    private  int value; // 声明一个私有成员变量 value，用于存储枚举值对应的整数值

    // 构造函数，用于初始化枚举值和对应的整数值
    OrderStatusEnum(int value) {
        this.value = value;
    }

    // 获取枚举值对应的整数值
    public int getValue() {
        return value;
    }
}
