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
public class OrderGameCareerHeard
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderAmount";

    /** 累计充值 */
    @ApiModelProperty("累计充值)")
    private BigDecimal rechargeAmount;
    /** 累计提现 */
    @ApiModelProperty("客服充值")
    private BigDecimal customerRechargeAmount;
    /** 累计提现 */
    @ApiModelProperty("累计提现")
    private BigDecimal withdrawAmount;
     @ApiModelProperty("下分真实提现")
    private BigDecimal customerRealCashWithdrawalAmount;
    @ApiModelProperty("下分扣除积分")
    private BigDecimal customerPointsDeductedAmount;
    @ApiModelProperty("下分账遍里的")
    private BigDecimal downAmount;
    @ApiModelProperty("法币提现金额")
    private BigDecimal lawWithdrawAmount;



}
