package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户支付统计
 */
@ApiModel(description="商户支付统计")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantPayStatVO implements Serializable {

    @ApiModelProperty(value = "商户id")
    private Long merchantId;
    @ApiModelProperty(value = "商户名称")
    private String merchantName;
    @ApiModelProperty(value = "通道id")
    private Long payChannelId;
    @ApiModelProperty(value = "通道名称")
    private String payChannelName;
    @ApiModelProperty("通道支付类型id")
    private Long payTypeId;
    @ApiModelProperty("支付类型名称")
    private String payTypeName;
    @ApiModelProperty(value = "充值人数")
    private Integer peopleNum;
    @ApiModelProperty(value = "充值订单数")
    private Integer orderNum;
    @ApiModelProperty(value = "成功订单数")
    private Integer successOrderNum;
    @ApiModelProperty(value = "拉单总额")
    private BigDecimal pullOrderAmount;
    @ApiModelProperty(value = "充值总额")
    private BigDecimal rechargeTotalAmount;
    @ApiModelProperty(value = "充值成功金额")
    private BigDecimal successAmount;
    @ApiModelProperty(value = "充值成功率（成功订单数 / 总订单数）")
    private BigDecimal successRate;

}
