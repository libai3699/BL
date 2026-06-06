package com.gp.common.base.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.AssertException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/13/013 14:36
 */
@Slf4j
@Data
public class AssertUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String errorCode = "errorCode";
    public static void setCode(Integer code){
        MDC.put(errorCode, code.toString());
    }
    public static void delCode(){
        MDC.remove(errorCode);
    }
    public static Integer getCode(){
        String code = MDC.get(errorCode);
        if (StrUtil.isBlank(code)) {
            return CodeEnum.Error.getCode();
        }else {
            return Integer.parseInt(code);
        }
    }

    public static void isTrue(boolean expression, Integer code, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        Assert.isTrue(expression, () -> new AssertException(code, StrUtil.format(errorMsgTemplate, params)));
    }

    public static void isFalse(boolean expression, Integer code, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        Assert.isFalse(expression, () -> new AssertException(code, StrUtil.format(errorMsgTemplate, params)));
    }
    public static void isNull(Object object, Integer code, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        Assert.isNull(object, () -> new AssertException(code, StrUtil.format(errorMsgTemplate, params)));
    }
}
