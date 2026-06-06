package com.gp.common.mybatisplus.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * t_user_ext：type=2 累计打码量、type=3 提现打码量（与列表「未完成打码量 = 提现打码量 - 累计打码量」一致）
 */
@Data
public class UserListWagerExtAggVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private BigDecimal withdrawWager;
    private BigDecimal cumWager;
}
