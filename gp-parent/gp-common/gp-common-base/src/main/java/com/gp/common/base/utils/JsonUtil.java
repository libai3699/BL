package com.gp.common.base.utils;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/24 15:28
 */
public class JsonUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    public static <T> T parseObject(Object object, Class<T> clazz){
       return JSONObject.parseObject(JSONObject.toJSONString(object), clazz);
    }
}
