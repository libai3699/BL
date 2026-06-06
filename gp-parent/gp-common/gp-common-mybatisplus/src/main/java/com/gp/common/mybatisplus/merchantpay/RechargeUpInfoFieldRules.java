package com.gp.common.mybatisplus.merchantpay;

import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.impl.WalletPayService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值响应 upInfo 字段注入规则注册表 —— 命中 (merchantCode, payMethod) 组合时,
 * 从创单结果 upInfo JSON 中提取指定字段, 注入到下单响应 JSON 中.
 *
 * key 维度：(merchantCode, payMethod)。后续可平滑迁移到 Nacos 或 t_pay_channel 字段。
 *
 * 当前规则：
 *   WALLET_PAY + USDT_BLACK -> upInfo.addr  -> 响应 addr
 */
public final class RechargeUpInfoFieldRules {

    /**
     * 字段映射规则: upInfo 中的字段名 -> 响应 JSON 中的字段名
     */
    public static final class Rule {
        private final String upInfoField;
        private final String targetField;

        public Rule(String upInfoField, String targetField) {
            this.upInfoField = upInfoField;
            this.targetField = targetField;
        }

        public String getUpInfoField() {
            return upInfoField;
        }

        public String getTargetField() {
            return targetField;
        }
    }

    private static final Map<String, Rule> REGISTRY = new HashMap<>();

    static {
        register(PayMerchantCons.WALLET_PAY, WalletPayService.USDT_BLACK, "addr", "addr");
        // 后续新增 (商户+通道) -> upInfo 字段注入 在此注册
    }

    public static void register(String merchantCode, String payMethod, String upInfoField, String targetField) {
        REGISTRY.put(key(merchantCode, payMethod), new Rule(upInfoField, targetField));
    }

    public static Rule get(String merchantCode, String payMethod) {
        if (merchantCode == null || payMethod == null) {
            return null;
        }
        return REGISTRY.get(key(merchantCode, payMethod));
    }

    public static Map<String, Rule> view() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    private static String key(String merchantCode, String payMethod) {
        return merchantCode + "|" + payMethod;
    }

    private RechargeUpInfoFieldRules() {}
}
