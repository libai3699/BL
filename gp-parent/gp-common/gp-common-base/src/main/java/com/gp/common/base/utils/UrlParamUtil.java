package com.gp.common.base.utils;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/29 22:11
 */
public class UrlParamUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static JSONObject convertQueryStringToJson(String queryString) {
        JSONObject jsonObject = new JSONObject();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                jsonObject.put(keyValue[0], keyValue[1]);
            } else {
                jsonObject.put(keyValue[0], "");
            }
        }
        return jsonObject;
    }
}
