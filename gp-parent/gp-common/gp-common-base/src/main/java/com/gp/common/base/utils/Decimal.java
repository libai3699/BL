package com.gp.common.base.utils;

import java.math.BigDecimal;

/**
 * @author axing
 */
public final class Decimal {

    private final BigDecimal value;

    private Decimal(BigDecimal value) {
        this.value = value;
    }

    public static Decimal of(BigDecimal value) {
        return new Decimal(value);
    }

    // 小于
    public boolean lt(BigDecimal val) {
        return value.compareTo(val) == -1;
    }

    // 等于
    public boolean eq(BigDecimal val) {
        return value.compareTo(val) == 0;
    }

    // 大于
    public boolean gt(BigDecimal val) {
        return value.compareTo(val) == 1;
    }

    // 大于等于
    public boolean ge(BigDecimal val) {
        return value.compareTo(val) > -1;
    }

    // 小于等于
    public boolean le(BigDecimal val) {
        return value.compareTo(val) < 1;
    }

}
