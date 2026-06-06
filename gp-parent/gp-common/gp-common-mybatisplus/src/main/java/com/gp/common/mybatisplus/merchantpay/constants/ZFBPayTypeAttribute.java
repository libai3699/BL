package com.gp.common.mybatisplus.merchantpay.constants;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝提现卡
 *
 * @author qicheng
 */
public class ZFBPayTypeAttribute {
	public static final String payTypeCode = PayTypeCons.PAY_ZFB;

	// 用户名称
	public static final String accountName = "accountName";
	// 支付账号
	public static final String account = "account";
	// 收款二维码
	public static final String qrCode = "qrCode";

	public static Map<String, String> parse(JSONObject json) {
		Map<String, String> result = new HashMap<>();
		result.put("accountName", json.getString(accountName));
		result.put("accountNumber", json.getString(account));
		return result;
	}
}
