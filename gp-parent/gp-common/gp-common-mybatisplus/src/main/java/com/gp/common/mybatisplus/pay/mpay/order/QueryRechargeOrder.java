package com.gp.common.mybatisplus.pay.mpay.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单返回的详情
 */
@Data
public class QueryRechargeOrder {

    /** 充值单号 */
    @ApiModelProperty("上游单号")
    private String upOrderNo;
    @ApiModelProperty("支付的json参数")
    private String payParam;
    /**
     * 推送的用户id
     */
    @ApiModelProperty("用户id")
    private String clientUserId;
}
