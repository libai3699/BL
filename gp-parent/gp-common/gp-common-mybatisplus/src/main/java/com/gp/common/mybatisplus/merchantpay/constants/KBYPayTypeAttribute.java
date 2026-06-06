package com.gp.common.mybatisplus.merchantpay.constants;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * KBY提现卡
 *
 * @author axing
 */
public class KBYPayTypeAttribute {
	public static final String payTypeCode = PayTypeCons.PAY_KBY;

	// 收款地址（兼容 address / beneficiary 两种字段名）
	public static final String address = "address";
	public static final String beneficiary = "beneficiary";

	public static Map<String, String> parse(JSONObject json) {
		Map<String, String> result = new HashMap<>();
		String addr = json.getString(address);
		if (StrUtil.isBlank(addr)) {
			addr = json.getString(beneficiary);
		}
		result.put("address", addr);
		return result;
	}
}
