package com.gp.common.mybatisplus.merchantpay.constants;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * CB提现卡
 *
 * @author qicheng
 */
public class USDTPayTypeAttribute {
	public static final String payTypeCode = PayTypeCons.PAY_USDT;

	// 收款地址
	public static final String address = "address";


	public static Map<String, String> parse(JSONObject json) {
		Map<String, String> result = new HashMap<>();
		result.put("address", json.getString(address));
		return result;
	}
}
