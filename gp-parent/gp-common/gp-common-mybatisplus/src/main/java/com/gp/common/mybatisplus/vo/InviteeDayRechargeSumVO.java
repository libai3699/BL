package com.gp.common.mybatisplus.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户自然日内充值汇总（t_order_amount 已支付 + t_order_person 上分）
 */
@Data
public class InviteeDayRechargeSumVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private BigDecimal totalAmount;
}
