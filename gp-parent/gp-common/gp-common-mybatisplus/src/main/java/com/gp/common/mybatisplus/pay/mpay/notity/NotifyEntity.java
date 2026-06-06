package com.gp.common.mybatisplus.pay.mpay.notity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NotifyEntity {


    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;
    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;


    /** 充值单号 */
    @ApiModelProperty("充值单号")
    private String orderNo;



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
    //新加的参数






}
