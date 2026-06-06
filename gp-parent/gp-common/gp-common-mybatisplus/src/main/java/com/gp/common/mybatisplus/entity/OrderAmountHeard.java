package com.gp.common.mybatisplus.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 充值订单对象 t_order_amount
 *
 * @author axing
 * @date 2024-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAmountHeard
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderAmount";

    /**
     * 收入
     */
    @ApiModelProperty(value = "收入")
    private BigDecimal income =BigDecimal.ZERO;
    /**
     * 关联的订单号
     */
    @ApiModelProperty(value="支出")
    private BigDecimal expend = BigDecimal.ZERO;

}
