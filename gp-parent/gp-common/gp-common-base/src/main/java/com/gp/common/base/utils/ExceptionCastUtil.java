package com.gp.common.base.utils;


import com.gp.common.base.exception.BusinessException;


/**
 * 异常抛出器
 *
 * @author axing
 * @date 2021/08/14
 */
public class ExceptionCastUtil{

    public static void cast(String msg){
        throw new BusinessException(msg);
    }
}
