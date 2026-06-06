package com.gp.common.base.constant;

public class OrderWithdrawOrderStatusConstants {
        public static final int PENDING_REVIEW = 0; // 待审核状态常量
        public static final int RISK_APPROVED = 1; // 风险审核通过状态常量
        public static final int FINANCE_APPROVED = 2; // 财务通过状态常量
        public static final int REJECTED = 3; // 已拒绝状态常量
        public static final int WITHDRAWAL_SUCCESS = 4; // 提现成功状态常量
        public static final int WITHDRAWAL_FAILURE = 5; // 提现失败状态常量
        public static final int ORDER_PLACED_SUCCESS = 6; // 上游下单成功状态常量
        public static final int ORDER_PLACED_FAILURE = 7; // 上游下单失败状态常量

        // 私有构造函数，防止实例化
        private OrderWithdrawOrderStatusConstants() {
        }

}
