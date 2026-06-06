package com.gp.common.mybatisplus.merchantpay.constants;

import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * CB提现卡
 *
 * @author qicheng
 */
public class MpayPayTypeAttribute {
	public static final String payTypeCode = PayTypeCons.MPAY;

	// 收款地址
	public static final String accountID = "accountID";


	public static Map<String, String> parse(JSONObject json) {
		Map<String, String> result = new HashMap<>();
		result.put("accountID", json.getString(accountID));
		return result;
	}
}
