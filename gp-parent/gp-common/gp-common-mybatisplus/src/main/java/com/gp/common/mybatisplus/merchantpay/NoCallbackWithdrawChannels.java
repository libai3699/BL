package com.gp.common.mybatisplus.merchantpay;

import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.impl.WalletPayService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 无异步回调的提现通道注册表 —— 源头出款、上游同步返回即完成、不会再发异步回调。
 * 下单同步成功后须立即查单推终态（复用 callbackWithdraw → processWithdrawCallback 路径）。
 *
 * key 维度：(merchantCode, payMethod)。后续可平滑迁移到 Nacos 或 t_pay_channel 字段。
 */
public final class NoCallbackWithdrawChannels {

    private static final Set<String> REGISTRY = new HashSet<>();

    static {
        register(PayMerchantCons.WALLET_PAY, WalletPayService.USDT_MERCHANT_WITHDRAW);
        // 后续新增无回调通道在此注册
    }

    public static void register(String merchantCode, String payMethod) {
        REGISTRY.add(key(merchantCode, payMethod));
    }

    public static boolean contains(String merchantCode, String payMethod) {
        if (merchantCode == null || payMethod == null) {
            return false;
        }
        return REGISTRY.contains(key(merchantCode, payMethod));
    }

    public static Set<String> view() {
        return Collections.unmodifiableSet(REGISTRY);
    }

    private static String key(String merchantCode, String payMethod) {
        return merchantCode + "|" + payMethod;
    }

    private NoCallbackWithdrawChannels() {}
}
