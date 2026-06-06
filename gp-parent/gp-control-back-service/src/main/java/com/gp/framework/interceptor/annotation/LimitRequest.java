package com.gp.framework.interceptor.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止接口重复请求
 * 接口url和userId作为key
 * @LimitRequest(key = "getUserId()") 使用方式
 * @author ruoyi
 *
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitRequest
{
    String key();
    int time() default 3;

}
