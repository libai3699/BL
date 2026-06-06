package com.gp.common.base.utils;


import com.gp.common.base.exception.BusinessException;


/**
 * 异常抛出器
 *
 * @author axing
 * @date 2021/08/14
 */
public class ExceptionUtil {

    public static String getFullStackTrace(Throwable e, int depth) {
        if (e == null) return "异常对象为空";
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append(e.toString()).append("\n");
        for (int i = 0; i < Math.min(depth, stackTrace.length); i++) {
            builder.append("\tat ").append(stackTrace[i].toString()).append("\n");
        }
        return builder.toString();
    }

}
