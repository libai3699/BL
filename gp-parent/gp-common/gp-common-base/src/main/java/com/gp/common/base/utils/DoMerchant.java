package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gp.common.base.model.merchant.MerchantRedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DoMerchant {




    public static String createSign(JSONObject jsonObject,String secret) {
        return   generateSign(jsonObject, secret);

    }

    /**
     * 生成sign
     * @param jsonObject
     * @param secret
     * @return
     */
    public static String generateSign(JSONObject jsonObject, String secret) {
        return   PayParamEncryUtil.getMaskSign(jsonObject,secret);
    }
    public static Boolean  checkSign(JSONObject jsonObject,String secret,String oldSign) {
        //校验下时间戳如果说时间戳存在的 比当前时间戳小超过1分钟了就不让他请求了
        //校验签名
        String sign = generateSign(jsonObject, secret);
        if (!oldSign.equals(sign)) {
            log.debug("正确的签名{}",sign);
            //无效签名
            return false;
        }
        return true;
    }
    public static  String doRequest(String url, MerchantRedDto merchantRedDto) {
        String result = null;
        try {
            result=  HttpRequest.post(url).body(JSON.toJSONString(merchantRedDto)).timeout(10000).execute().body();
        } catch (HttpException e) {
            String errorMsg = StrUtil.format("请求商户红包地址, 访问地址: {}",url);
            log.error(errorMsg);

        }
        log.info("返回结果: {}", result);
        return result;
    }

}
