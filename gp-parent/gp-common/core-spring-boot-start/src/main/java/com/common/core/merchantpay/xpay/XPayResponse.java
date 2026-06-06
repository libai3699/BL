package com.common.core.merchantpay.xpay;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * XPay 通用响应类
 * 包含所有 XPay API 可能返回的字段
 */
@Data
public class XPayResponse {
    
    // ========== 通用字段 ==========
    @ApiModelProperty("响应码: 0-成功")
    @JSONField(name = "code")
    private Integer code;
    
    @ApiModelProperty("响应消息")
    @JSONField(name = "message")
    private String message;
    
    // ========== 创建收款请求 ==========
    @ApiModelProperty("收款请求号")
    @JSONField(name = "request_no")
    private String request_no;
    
    @ApiModelProperty("支付URL")
    @JSONField(name = "payment_url")
    private String payment_url;
    
    @ApiModelProperty("金额")
    @JSONField(name = "amount")
    private String amount;
    
    @ApiModelProperty("币种代码")
    @JSONField(name = "currency_code")
    private String currency_code;
    
    @ApiModelProperty("币种")
    @JSONField(name = "currency")
    private String currency;
    
    @ApiModelProperty("协议代码")
    @JSONField(name = "protocol_code")
    private String protocol_code;
    
    @ApiModelProperty("商户自定义ID")
    @JSONField(name = "pid")
    private String pid;
    
    @ApiModelProperty("过期时间")
    @JSONField(name = "expire_time")
    private Long expire_time;
    
    @ApiModelProperty("状态: 1-待支付, 2-已完成, 3-已取消, 4-已过期")
    @JSONField(name = "status")
    private Integer status;
    
    @ApiModelProperty("状态文本")
    @JSONField(name = "status_text")
    private String status_text;
    
    // ========== 创建收款地址 ==========
    @ApiModelProperty("收款地址")
    @JSONField(name = "address")
    private String address;
    
    @ApiModelProperty("网络类型")
    @JSONField(name = "network")
    private String network;
    
    // ========== 查询订单详情 ==========
    @ApiModelProperty("充值单号")
    @JSONField(name = "recharge_no")
    private String recharge_no;
    
    @ApiModelProperty("提现单号")
    @JSONField(name = "withdraw_no")
    private String withdraw_no;
    
    @ApiModelProperty("转账单号")
    @JSONField(name = "transfer_no")
    private String transfer_no;
    
    @ApiModelProperty("手续费")
    @JSONField(name = "fee_amount")
    private String fee_amount;
    
    @ApiModelProperty("实际到账金额")
    @JSONField(name = "actual_amount")
    private String actual_amount;
    
    @ApiModelProperty("付款地址")
    @JSONField(name = "from_address")
    private String from_address;
    
    @ApiModelProperty("收款地址/提现地址")
    @JSONField(name = "to_address")
    private String to_address;
    
    @ApiModelProperty("交易哈希")
    @JSONField(name = "tx_hash")
    private String tx_hash;
    
    @ApiModelProperty("链类型")
    @JSONField(name = "chain")
    private String chain;
    
    @ApiModelProperty("区块高度")
    @JSONField(name = "block_number")
    private Long block_number;
    
    @ApiModelProperty("确认数")
    @JSONField(name = "confirmations")
    private Integer confirmations;
    
    @ApiModelProperty("付款用户ID")
    @JSONField(name = "pay_user_id")
    private Long pay_user_id;
    
    @ApiModelProperty("收款用户ID")
    @JSONField(name = "to_uid")
    private String to_uid;
    
    @ApiModelProperty("收款 TG ID")
    @JSONField(name = "to_tg_id")
    private Long to_tg_id;
    
    @ApiModelProperty("是否新用户")
    @JSONField(name = "is_new_user")
    private Boolean is_new_user;
    
    @ApiModelProperty("支付时间")
    @JSONField(name = "pay_time")
    private Long pay_time;
    
    @ApiModelProperty("创建时间")
    @JSONField(name = "create_time")
    private Long create_time;
    
    @ApiModelProperty("状态更新时间")
    @JSONField(name = "status_time")
    private Long status_time;
    
    @ApiModelProperty("拒绝原因")
    @JSONField(name = "reject_reason")
    private String reject_reason;
    
    @ApiModelProperty("失败原因")
    @JSONField(name = "fail_reason")
    private String fail_reason;
    
    @ApiModelProperty("备注")
    @JSONField(name = "remark")
    private String remark;
    
    // ========== 创建地址返回 ==========
    @ApiModelProperty("地址列表")
    @JSONField(name = "addresses")
    private java.util.List<AddressInfo> addresses;
    
    /**
     * 地址信息
     */
    @Data
    public static class AddressInfo {
        @ApiModelProperty("地址")
        @JSONField(name = "address")
        private String address;
        
        @ApiModelProperty("协议代码")
        @JSONField(name = "protocol_code")
        private String protocol_code;
        
        @ApiModelProperty("商户自定义ID")
        @JSONField(name = "pid")
        private String pid;
    }
}
