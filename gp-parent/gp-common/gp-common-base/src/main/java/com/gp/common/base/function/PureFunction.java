package com.gp.common.base.function;

/**
 * 业务函数式接口
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@FunctionalInterface
public interface PureFunction {
    void action() throws Exception;
}
