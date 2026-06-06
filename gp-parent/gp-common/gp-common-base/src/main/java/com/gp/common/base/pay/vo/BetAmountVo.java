package com.gp.common.base.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author axing
 * @version 1.0
 * @date 2024/5/7/007 11:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetAmountVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 汇旺商户
     */
    @ApiModelProperty(value="充值金额")
    private BigDecimal rechargeAmount;
    /**
     * 汇旺商户
     */
    @ApiModelProperty(value="到账金额")
    private BigDecimal receivedAmount;
    /**
     * 汇旺商户
     */
    @ApiModelProperty(value="赠送金额")
    private BigDecimal awardAmount;
    /**
     * 汇旺账户
     */
    @ApiModelProperty(value="提现流水")
    private BigDecimal withdrawBetAmount;

}
