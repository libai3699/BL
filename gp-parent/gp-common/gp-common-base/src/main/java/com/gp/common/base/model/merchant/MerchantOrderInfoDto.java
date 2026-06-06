package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantOrderInfoDto {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appId")
    @NotBlank(message = "appId不能为空")
    private  String appKey;

    @ApiModelProperty("订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
    //签名
    @ApiModelProperty("签名")
    @NotBlank(message = "签名不能为空")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳毫秒值13位")
    @NotBlank(message = "当前时间戳不能为空")
    private  Long timestamp;

}
