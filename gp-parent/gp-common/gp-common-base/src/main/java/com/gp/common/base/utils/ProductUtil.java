package com.gp.common.base.utils;

import org.slf4j.MDC;

import java.io.Serializable;

/**
 * 存入当前线程产品Id,获取线程Id
 * @author axing
 * @version 1.0
 * @date 2024/10/22/022 16:33
 */
public class ProductUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String productId = "productId";

    public static void set(String value){
        MdcUtil.set(productId, value);
    }

    public static String get(){
        return MdcUtil.get(productId);
    }
}
