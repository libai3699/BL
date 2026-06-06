package com.gp.common.mybatisplus.pay.mpay.createAddress;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单返回的详情
 */
@Data
public class CreateAddress {
    /** 用户id */
    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("itemId")
    private Integer itemId;
    @ApiModelProperty("币名称")
    private String itemName;

    @ApiModelProperty("mask链名称")
    private String chainTag;

    @ApiModelProperty("支付的json参数")
    private String payParam;

    // XPay 专用字段
    @ApiModelProperty("协议ID（XPay）")
    private Integer protocolId;

    @ApiModelProperty("创建数量（XPay，1-100）")
    private Integer count;

    @ApiModelProperty("商户自定义ID（XPay，最大100字符）")
    private String pid;

}
