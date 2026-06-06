package com.gp.common.mybatisplus.merchantpay.constants;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 银联提现卡
 *
 * @author qicheng
 */
public class YLPayTypeAttribute {
    public static final String payTypeCode = PayTypeCons.PAY_YL;

    // 银行名称
    public static final String bankName = "bankName";
    // 支行名称
    public static final String bankBranchName = "bankBranchName";
    // 用户名称
    public static final String accountName = "accountName";
    // 卡号
    public static final String bankAccount = "bankAccount";

    public static Map<String, String> parse(JSONObject json) {
        Map<String, String> result = new HashMap<>();
        result.put("accountName", json.getString(accountName));
        result.put("accountNumber", json.getString(bankAccount));
        result.put("bankName", json.getString(bankName));
        result.put("bankBranchName", json.getString(bankBranchName));
        return result;
    }
}
