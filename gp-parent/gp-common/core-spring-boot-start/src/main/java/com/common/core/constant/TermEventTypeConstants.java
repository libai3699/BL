package com.common.core.constant;


/**
 * 定义用户操作和系统事件的类型常量类。
 * 所有常量值应保持唯一且不可变，用于标识系统中不同的事件类型。
 *
 * TODO 事件类型
 */
public final class TermEventTypeConstants {

    /** 下注 */
    public static final Integer BET = 0;

    /** 结算 */
    public static final Integer REBATE = 1;

    /** 取消 */
    public static final Integer CANCEL = 2;

    // 私有构造器防止实例化
    private TermEventTypeConstants() {
        throw new AssertionError("Cannot instantiate constant utility class");
    }



}
