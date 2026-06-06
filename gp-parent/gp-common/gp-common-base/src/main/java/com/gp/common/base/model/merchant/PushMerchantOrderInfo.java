package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PushMerchantOrderInfo {


    private static final long serialVersionUID = 1L;


    /** 充值单号 */
    @ApiModelProperty("充值单号")
    private String orderNo;

    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    /** 用户id */
    @ApiModelProperty("用户id")
    private Long userId;

    /** tg用户Id */
    @ApiModelProperty("tg用户Id")
    private Long tgUserId;

    /** 金额 */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /** 订单状态(0 待支付, 1 已支付, 2 已取消) */
    @ApiModelProperty("订单状态(0 待支付, 1 已支付, 2 已取消)")
    private Integer orderStatus;

    /** 实际到账金额 */
    @ApiModelProperty("实际到账金额")
    private BigDecimal actualAmount;

    /** 商户订单号 */
    @ApiModelProperty("商户订单号")
    private String merchantOrderNo;

    /** 附加参数 */
    @ApiModelProperty("附加参数")
    private String attch;

    @ApiModelProperty("备注")
    private String remark;

    /** 支付时间 */
    @ApiModelProperty("支付时间")
    private Long payTime;

    /** 下单时间 */
    @ApiModelProperty("下单时间")
    private Long createTime;


    /** 回调的时间戳 */
    @ApiModelProperty("回调的时间戳")
    private Long timestamp;

    @ApiModelProperty("签名")
    private String sign;
    @ApiModelProperty("appKey")
    private  String appKey;


}
