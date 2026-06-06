package com.common.core.merchantpay.xpay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * XPay 回调请求（最新格式）
 */
@Data
public class XPayNotifyRequest {
    
    @ApiModelProperty("通知单号")
    private String notification_id;
    
    @ApiModelProperty("订单类型：1-充值, 2-提现, 3-收款请求")
    private Integer order_type;
    
    @ApiModelProperty("应用ID")
    private String app_id;
    
    @ApiModelProperty("时间戳")
    private Long timestamp;
    
    @ApiModelProperty("签名")
    private String sign;
    
    // ========== 充值/提现共用字段 ==========
    
    @ApiModelProperty("订单号（充值/提现）")
    private String order_id;
    
    @ApiModelProperty("金额")
    private String amount;
    
    @ApiModelProperty("手续费")
    private String fee_amount;
    
    @ApiModelProperty("实际到账金额")
    private String actual_amount;
    
    @ApiModelProperty("币种（充值/提现用 currency）")
    private String currency;
    
    @ApiModelProperty("地址")
    private String address;
    
    @ApiModelProperty("付款/转出地址")
    private String from_address;
    
    @ApiModelProperty("交易哈希")
    private String tx_hash;
    
    @ApiModelProperty("链类型")
    private String chain;
    
    @ApiModelProperty("状态：completed-已完成")
    private String status;
    
    @ApiModelProperty("确认数")
    private Integer confirmations;
    
    @ApiModelProperty("商户自定义ID")
    private String pid;
    
    // ========== 收款请求专用字段 ==========
    
    @ApiModelProperty("收款请求号（收款请求回调）")
    private String request_no;
    
    @ApiModelProperty("币种代码（收款请求用 currency_code）")
    private String currency_code;
    
    @ApiModelProperty("协议代码（收款请求）")
    private String protocol_code;
    
    @ApiModelProperty("付款用户ID（收款请求）")
    private Long pay_user_id;
    
    @ApiModelProperty("转账单号（收款请求）")
    private String transfer_no;
}
