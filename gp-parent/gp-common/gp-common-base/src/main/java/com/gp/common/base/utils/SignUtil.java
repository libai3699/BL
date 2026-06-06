package com.gp.common.base.utils;


import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.Base64;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/29 22:11
 */
public class SignUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 生成签名
     * @param param
     * @param key
     * @return
     */
    public static String getSign(String param, String key) {
        String str = MD5Util.getStr(MD5Util.getStr(param + key) + key);
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * 校验签名
     * @param param
     * @param key
     * @param sign
     * @return
     */
    public static boolean checkSign(String param, String key, String sign) {
        if( StrUtil.isEmpty(sign) ){
            return false;
        }
        if (getSign(param, key).equals(sign)) {
            return true;
        }else {
            return false;
        }
    }


}
