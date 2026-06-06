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
public class WithdrawOrder {

    /**
     * 飞机id
     */
    @ApiModelProperty("飞机id ")
    private Long tgUserId;
    /**
     * 飞机昵称
     */
    @ApiModelProperty("飞机昵称")
    private String userTgName;
    /**
     * 飞机用户名
     */
    @ApiModelProperty("飞机用户名")
    private String userTgUsername;
    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNo;
    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;
    /**
     * 提现金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 法币金额，单位为分
     */
    @ApiModelProperty("法币金额")
    private BigDecimal lawAmount;

    /**
     * 支付商户编码
     */
    @ApiModelProperty("支付商户编码")
    private String merchantCode;
    /**
     * 支付通道编码
     */
    @ApiModelProperty("支付通道编码")
    private String payChannelCode;

    /**
     * 支付通道
     */
    @ApiModelProperty("支付通道")
    private PayChannel payChannel;

    /**
     * 支付类型
     */
    @ApiModelProperty("支付类型")
    private PayType payType;

    /**
     * 支付的json参数
     */
    @ApiModelProperty("支付的json参数")
    private String payParam;

    /**
     * 提现参数
     */
    @ApiModelProperty("提现参数")
    private Map<String, Object> withdrawParam;

    /**
     * 绑定数据
     */
    @ApiModelProperty("绑定数据")
    private Map<String, Object> bindData;

    /**
     * 提现地址
     */
    @ApiModelProperty("提现地址")
    private String address;
}
