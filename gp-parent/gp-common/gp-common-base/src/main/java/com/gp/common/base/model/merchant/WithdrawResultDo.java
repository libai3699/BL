package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单返回的详情
 */
@Data
public class WithdrawResultDo  {

    /**
     * 充值单号
     */
    @ApiModelProperty("提现单号")
    private String orderNo;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    @ApiModelProperty("金额")
    private BigDecimal amount;
    /**
     * 提现时间
     */
    @ApiModelProperty("提现时间")
    private Long createTime;

    /** 回调的时间戳 */
    @ApiModelProperty("回调的时间戳")
    private Long timestamp;
}
