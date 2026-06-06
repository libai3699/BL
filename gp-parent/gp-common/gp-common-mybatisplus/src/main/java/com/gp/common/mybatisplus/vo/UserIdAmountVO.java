package com.gp.common.mybatisplus.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * user_id + 聚合金额（控制后台用户列表等批量查询）
 */
@Data
public class UserIdAmountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private BigDecimal amount;
}
