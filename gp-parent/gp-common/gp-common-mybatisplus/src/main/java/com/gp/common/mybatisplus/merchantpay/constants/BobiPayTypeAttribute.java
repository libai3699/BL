package com.gp.common.mybatisplus.merchantpay.constants;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 波币钱包提现卡
 *
 * @author axing
 */
public class BobiPayTypeAttribute {
	public static final String payTypeCode = PayTypeCons.PAY_BO;

	// 用户在bobi平台的钱包地址
	public static final String beneficiary = "beneficiary";

	public static Map<String, String> parse(JSONObject json) {
		Map<String, String> result = new HashMap<>();
		result.put("address", json.getString(beneficiary));
		return result;
	}
}
