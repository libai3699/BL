package com.gp.common.base.constant;

public class OrderPhoneStatusConstants {
        public static final int PENDING_REVIEW = 0; // 待审核状态常量
        public static final int SUBMITTED = 1; // 已提交状态常量
        public static final int CHECK_APPROVED = 2; // 审核通过状态常量
        public static final int REJECTED = 3; // 已拒绝状态常量


        // 私有构造函数，防止实例化
        private OrderPhoneStatusConstants() {
        }

}
