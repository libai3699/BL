package com.gp.common.mybatisplus.pay.mpay.notity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;
    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;
    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;

    @ApiModelProperty("上游单号")
    private String upOrderNo;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("用户id")
    private String clientUserId;
    @ApiModelProperty("itemId")
    private Integer itemId;
    //链名称
    @ApiModelProperty("链名称")
    private String chainTag;
    @ApiModelProperty("下单时间")
    private Long createTime;
    @ApiModelProperty("success成功 faild 失败")
    private String status;
    //订单类型 1充币 2提币
    @ApiModelProperty("1充币 2提币")
    private Integer orderType;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

}
