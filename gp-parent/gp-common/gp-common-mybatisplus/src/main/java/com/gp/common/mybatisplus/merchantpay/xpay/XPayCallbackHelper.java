package com.gp.common.mybatisplus.merchantpay.xpay;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;

/**
 * XPay 异步回调 JSON 的公共解析（避免多处以不同方式读 order_type、业务单号）。
 */
public final class XPayCallbackHelper {

    private static final String[] PAYMENT_STATUS_KEYS = {"orderNo", "request_no", "requestNo", "order_id"};

    private XPayCallbackHelper() {
    }

    /**
     * order_type 可能是数字或字符串（如 "1"）。
     */
    public static Integer parseOrderType(JSONObject json) {
        if (json == null) {
            return null;
        }
        Integer t = json.getInteger("order_type");
        if (t != null) {
            return t;
        }
        String s = json.getString("order_type");
        if (StrUtil.isBlank(s)) {
            return null;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 链上充值(1)、内部转账充值(5)：用 order_id 调 /recharge/status。
     */
    public static boolean isRechargeStatusByOrderId(Integer orderType, String orderId) {
        return orderType != null && (orderType == 1 || orderType == 5) && StrUtil.isNotBlank(orderId);
    }

    /**
     * 收款请求等：用于 /payment/status 的单号（按常见字段顺序尝试）。
     */
    public static String resolvePaymentStatusBusinessKey(JSONObject notify) {
        if (notify == null) {
            return "";
        }
        for (String key : PAYMENT_STATUS_KEYS) {
            String v = notify.getString(key);
            if (StrUtil.isNotBlank(v)) {
                return v;
            }
        }
        return "";
    }
}
