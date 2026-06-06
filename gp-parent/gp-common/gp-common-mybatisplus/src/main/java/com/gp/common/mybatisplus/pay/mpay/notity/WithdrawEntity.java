package com.gp.common.mybatisplus.pay.mpay.notity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WithdrawEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private String appKey;
    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    @ApiModelProperty("上游单号")
    private String upOrderNo;


    @ApiModelProperty("success成功 faild 失败")
    private String status;
    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 实际到账金额
     */
    @ApiModelProperty("实际到账金额")
    private BigDecimal actualAmount;
    /**
     * 实际到账金额
     */
    @ApiModelProperty("fee")
    private BigDecimal fee;

    @ApiModelProperty("itemId")
    private Integer itemId;
    //链名称
    @ApiModelProperty("链名称")
    private String chainTag;
    @ApiModelProperty("下单时间")
    private Long createTime;

    //订单类型 1充币 2提币
    @ApiModelProperty("1充币 2提币")
    private Integer orderType;
    /**
     * 商户订单号
     */
    @ApiModelProperty("商户订单号")
    private String merchantOrderNo;
    //时间戳
    @ApiModelProperty("当前时间戳")
    private Long timestamp;

    //签名
    @ApiModelProperty("签名")
    private String sign;

}
