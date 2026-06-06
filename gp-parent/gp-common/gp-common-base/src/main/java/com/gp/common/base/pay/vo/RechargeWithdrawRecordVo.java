package com.gp.common.base.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeWithdrawRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "方式")
    private String method;

    @ApiModelProperty(value = "金额")
    private String amountStr;

    @ApiModelProperty(value = "到账金额")
    private String arrivalAmountStr;

    @ApiModelProperty(value = "状态 0 充值提现中 1 成功 2 失败")
    private Integer status;

    @ApiModelProperty(value = "支付链接")
    private String payUrl;

    @ApiModelProperty(value = "时间")
    private Date createTime;
}
