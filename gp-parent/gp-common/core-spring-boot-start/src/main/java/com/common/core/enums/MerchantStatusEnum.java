package com.common.core.enums;

public enum MerchantStatusEnum {
    PENDING_REVIEW(-2), // 待审核
    REVIEW_REJECTED(-1), // 审核拒绝
    NORMAL(0), // 正常
    FROZEN(1); // 冻结

    private final int value;

    MerchantStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
