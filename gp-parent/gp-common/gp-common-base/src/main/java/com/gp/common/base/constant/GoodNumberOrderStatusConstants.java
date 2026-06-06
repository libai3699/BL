package com.gp.common.base.constant;

public final class GoodNumberOrderStatusConstants {

    // 订单状态：待支付
    public static final int PENDING_PAYMENT = 0;

    // 订单状态：已支付
    public static final int PAID = 1;

    // 订单状态：已取消
    public static final int CANCELLED = 2;
    // 私有构造函数，防止实例化
    private GoodNumberOrderStatusConstants() {
    }


}
