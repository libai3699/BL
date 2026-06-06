package com.common.datasource.annotation;

import java.lang.annotation.*;

/**
 * 方法或者类包含此注解 不切库
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CecuExclude {
    /**
     * 排除无视 value 的值
     */
    boolean value() default true;
}
