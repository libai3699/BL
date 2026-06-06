package com.gp.common.base.utils;


import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 *
 *
 *
 */
public class SignatureUtil {

    private static String TIME_STAMP_KEY = "timeStamp";
    private static String SIGN_KEY = "sign";
    //超时时效，超过此时间认为签名过期
    private static Long EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * 生成签名
     *
     */
    public static Map getSignature(JSONObject param, String secretKey) {
        Map params=null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonStr = objectMapper.writeValueAsString(param);
            params = objectMapper.readValue(jsonStr, Map.class);

        } catch (Exception e) {
            throw new RuntimeException("生成签名：转换json失败");
        }
        params.remove("merchantId");
//        params.put("memberId",param.getInteger("memberId"));
//        params.put("memberToken",param.getString("memberToken"));
        if (param.get(TIME_STAMP_KEY) == null) {
            params.put(TIME_STAMP_KEY, System.currentTimeMillis());
        }
        //对map参数进行排序生成参数
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuilder temp = new StringBuilder();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = String.valueOf(value);
            }
            temp.append(valueString);
        }
        //根据参数生成签名
        String sign = DigestUtils.sha256Hex(temp.toString() + secretKey).toUpperCase();
        params.put(SIGN_KEY, sign);
        return params;
    }

    /**
     * 校验签名有效性
     *
     *
     * @param secretKey VIP提供的私钥
     * @return
     */
    public static boolean checkSignature(Map param, String secretKey) {
        //获取request中的json参数转成map
        String sign = (String) param.get(SIGN_KEY);
        Long start = (Long) param.get(TIME_STAMP_KEY);
        long now = System.currentTimeMillis();
        //校验时间有效性
        if (start == null || now - start > EXPIRE_TIME || start - now > 0L) {
            return false;
        }
        //是否携带签名
        if (StringUtils.isBlank(sign)) {
            return false;
        }
        //获取除签名外的参数
        param.remove(SIGN_KEY);
        //校验签名
        Map paramMap = SignatureUtil.getSignature(new JSONObject(param), secretKey);
        String signature = (String) paramMap.get("sign");
        if (sign.equals(signature)) {
            return true;
        }
        return false;
    }

}

