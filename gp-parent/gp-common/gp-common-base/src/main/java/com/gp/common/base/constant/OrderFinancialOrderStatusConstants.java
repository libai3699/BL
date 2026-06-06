package com.gp.common.base.constant;

import cn.hutool.core.collection.CollUtil;

import java.util.List;

public final class OrderFinancialOrderStatusConstants {
    // 定义状态常量值
    public static final Integer TO_BE_PAID = 0;  // 待支付
    public static final Integer PAID_PENDING_REVIEW = 1;  // 已支付(待审核)
    public static final Integer EFFECTIVE_INCOME_GENERATED = 2;  // 已生效,产生收益(审核成功)
    public static final Integer REVIEW_FAILED = 3;  // 审核失败
    public static final Integer REDEMPTION_SUBMITTED = 4;  // 提交赎回
    public static final Integer REDEMPTION_REVIEWED_SUCCESS = 5;  // 赎回审核成功
    public static final Integer REDEMPTION_REVIEWED_FAILED = 6;  // 赎回审核失败
    public static final Integer AUTO_REDEMPTION = 7;  // 自动赎回
    public static final List<Integer> myFinancial = CollUtil.newArrayList(
            PAID_PENDING_REVIEW,
            EFFECTIVE_INCOME_GENERATED,
            REDEMPTION_SUBMITTED,
            REDEMPTION_REVIEWED_FAILED
    );
}
