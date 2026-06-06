package com.gp.common.mybatisplus.pay.mpay.order;

import com.gp.common.mybatisplus.entity.PayChannel;
import com.gp.common.mybatisplus.entity.PayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单返回的详情
 */
@Data
public class CreateOrder {
    /** 原始币种金额 */
    @ApiModelProperty("原始币种金额")
    private BigDecimal originalAmount;
    /** 系统金额 */
    @ApiModelProperty("系统金额")
    private BigDecimal amount;

    @ApiModelProperty("订单号")
    private String orderNo;

    /** 附加参数 */
    @ApiModelProperty("附加参数")
    private String attch;

    /** 附加参数 */
    @ApiModelProperty("附加参数")
    private String extParam;


    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("支付商户编码")
    private String payMerchantCode;

    @ApiModelProperty("请求域名")
    private String domain;

    @ApiModelProperty("支付通道编码")
    private String payChannelCode;

    @ApiModelProperty("支付通道")
    private PayChannel payChannel;

    @ApiModelProperty("支付类型")
    private PayType payType;


    @ApiModelProperty("支付的json参数")
    private String payParam;

    @ApiModelProperty("支付的语言")
    private  String lanKey;

    @ApiModelProperty("userId")
    private Long userId;

    @ApiModelProperty("绑定数据")
    private Map<String, Object> bindData;

}
