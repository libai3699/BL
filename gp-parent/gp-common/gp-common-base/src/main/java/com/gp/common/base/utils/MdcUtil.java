package com.gp.common.base.utils;

import org.slf4j.MDC;

import java.io.Serializable;

/**
 * mdc的数据是存入线程
 * @author axing
 * @version 1.0
 * @date 2024/10/22/022 16:33
 */
public class MdcUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    public static void set(String key, String value){
        MDC.put(key, value);
    }

    public static String get(String key){
        return MDC.get(key);
    }
}
