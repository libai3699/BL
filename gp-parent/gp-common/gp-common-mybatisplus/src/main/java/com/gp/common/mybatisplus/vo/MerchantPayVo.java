package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantPayVo {

    /**
     * 支付商户ID
     */
    @ApiModelProperty("支付商户ID")
    private Long merchantId;

    /**
     * 支付商户名称
     */
    @ApiModelProperty("支付商户名称")
    private String merchantName;

    /**
     * 支付通道id
     */
    @ApiModelProperty("支付通道id")
    private Long payChannelId;

    /**
     * 支付通道名称
     */
    @ApiModelProperty("支付通道名称")
    private String payChannelName;

    /**
     * 通道支付类型编id
     */
    @ApiModelProperty("通道支付类型id")
    private Long payTypeId;

    /**
     * 支付类型名称
     */
    @ApiModelProperty("支付类型名称")
    private String payTypeName;

    /**
     * 人数
     */
    @ApiModelProperty("人数")
    private Integer peopleNum;

    /**
     * 订单数
     */
    @ApiModelProperty("订单数")
    private Integer orderNum;

    /**
     * 成功订单数
     */
    @ApiModelProperty("成功订单数")
    private Integer successOrderNum;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 成功金额
     */
    @ApiModelProperty("成功金额")
    private BigDecimal successAmount;

    /**
     * 成功率
     */
    @ApiModelProperty("成功率")
    private BigDecimal successRate;
}
