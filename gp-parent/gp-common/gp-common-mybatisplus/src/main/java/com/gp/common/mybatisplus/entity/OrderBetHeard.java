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
public class OrderBetHeard
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderAmount";

    /**
     * 投注额
     */
    @ApiModelProperty(value = "投注额")
    private BigDecimal betAmount =BigDecimal.ZERO;
    /**
     * 投注额
     */
    @ApiModelProperty(value = "打码量")
    private BigDecimal codeAmount =BigDecimal.ZERO;
    /**
     * 反奖
     */
    @ApiModelProperty(value = "反奖")
    private BigDecimal settleAmount=BigDecimal.ZERO;
    /**
     * 反水
     */
    @ApiModelProperty(value = "反水")
    private BigDecimal rebate=BigDecimal.ZERO;

}
