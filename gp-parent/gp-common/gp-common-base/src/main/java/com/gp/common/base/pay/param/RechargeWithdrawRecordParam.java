package com.gp.common.base.pay.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/22/022 12:11
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeWithdrawRecordParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("0充值1提现)")
    private Integer type;
    @ApiModelProperty("0全部 1 今天, 2 七天 3一个月 4三个月5 昨天)")
    private Integer timeSection;

    @ApiModelProperty("起始时间")
    private Date startTime;
    @ApiModelProperty("结束时间")
    private Date endTime;

    /**
     * 仅 type=0 充值记录生效。true：只返回仍可「去支付」的待支付单（有 payUrl 且在有效支付时间窗内），
     * 不含已超时、无链接的待支付单；不含系统上分。进支付页传 true + pageSize=1 判断是否存在可继续支付的订单。
     */
    @ApiModelProperty("可选，仅充值。true=仅当前仍可支付(有链接且未超时)；不传或 false=不过滤")
    private Boolean onlyPayable;

}
