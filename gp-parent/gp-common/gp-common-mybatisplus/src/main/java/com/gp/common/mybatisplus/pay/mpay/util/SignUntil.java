package com.gp.common.mybatisplus.pay.mpay.util;

import com.alibaba.fastjson2.JSONObject;
import com.gp.common.base.utils.PayParamEncryUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SignUntil {

    public static String generateSign(JSONObject jsonObject, String secret) {
        return   PayParamEncryUtil.getMaskSign(jsonObject,secret);
    }
    public static String generateUPAYSign(JSONObject jsonObject, String secret) {
        return   PayParamEncryUtil.getUPAYSign(jsonObject,secret);
    }
    public static Boolean  checkSign(JSONObject jsonObject,String secret,String oldSign) {
        //校验下时间戳如果说时间戳存在的 比当前时间戳小超过1分钟了就不让他请求了
        //校验签名
        String sign = generateSign(jsonObject, secret);
        if (!oldSign.equals(sign)) {
            log.error("正确的签名{}",sign);
            //无效签名
            return false;
        }
        return true;
    }
    public static Boolean  checkSignUpperCase(JSONObject jsonObject,String secret,String oldSign) {
        //校验下时间戳如果说时间戳存在的 比当前时间戳小超过1分钟了就不让他请求了
        //校验签名
        String sign = generateSign(jsonObject, secret).toUpperCase();
        if (!oldSign.equals(sign)) {
            log.error("正确的签名{}",sign);
            //无效签名
            return false;
        }
        return true;
    }

    public static Boolean  checkUPaySignUpperCase(JSONObject jsonObject,String secret,String oldSign) {
        //校验下时间戳如果说时间戳存在的 比当前时间戳小超过1分钟了就不让他请求了
        //校验签名
        String sign = PayParamEncryUtil.getUPAYSign(jsonObject, secret);
        if (!oldSign.equals(sign)) {
            log.error("正确的签名{}",sign);
            //无效签名
            return false;
        }
        return true;
    }


}
