package com.gp.common.mybatisplus.pay.mpay.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单返回的详情
 */
@Data
public class QueryOrderInfo {

    /** 充值单号 */
    @ApiModelProperty("单号")
    private String orderNo;

    @ApiModelProperty("支付的json参数")
    private String payParam;
}
