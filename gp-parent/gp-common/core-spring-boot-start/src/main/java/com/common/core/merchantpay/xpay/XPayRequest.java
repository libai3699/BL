package com.common.core.merchantpay.xpay;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * XPay 通用请求类
 * 包含所有 XPay API 可能用到的字段
 */
@Data
public class XPayRequest {
    
    // ========== 通用字段 ==========
    @ApiModelProperty("请求流水号/订单号")
    @JSONField(name = "request_no")
    private String request_no;
    
    @ApiModelProperty("商户自定义ID")
    @JSONField(name = "pid")
    private String pid;
    
    // ========== 创建收款请求 ==========
    @ApiModelProperty("收款金额")
    @JSONField(name = "amount")
    private String amount;
    
    @ApiModelProperty("币种代码")
    @JSONField(name = "currency_code")
    private String currency_code;
    
    @ApiModelProperty("协议代码/协议ID")
    @JSONField(name = "protocol_code")
    private String protocol_code;
    
    @JSONField(name = "protocol_id")
    private Integer protocol_id;


    @JSONField(name = "count")
    private Integer count;
    
    @ApiModelProperty("过期时间(秒)")
    @JSONField(name = "expire_seconds")
    private Integer expire_seconds;
    
    @ApiModelProperty("备注")
    @JSONField(name = "remark")
    private String remark;
    
    // ========== 创建收款地址 ==========
    @ApiModelProperty("网络类型")
    @JSONField(name = "network")
    private String network;
    
    // ========== 查询订单 ==========
    @ApiModelProperty("充值单号")
    @JSONField(name = "recharge_no")
    private String recharge_no;
    
    @ApiModelProperty("提现单号")
    @JSONField(name = "withdraw_no")
    private String withdraw_no;
    
    // ========== 转账 ==========
    @ApiModelProperty("收款用户的 Telegram ID")
    @JSONField(name = "tg_id")
    private Long tg_id;
    
    @ApiModelProperty("TG 用户名")
    @JSONField(name = "tg_username")
    private String tg_username;
    
    @ApiModelProperty("TG 名字")
    @JSONField(name = "tg_first_name")
    private String tg_first_name;
    
    @ApiModelProperty("TG 姓氏")
    @JSONField(name = "tg_last_name")
    private String tg_last_name;
    
    @ApiModelProperty("TG 头像 URL")
    @JSONField(name = "tg_photo_url")
    private String tg_photo_url;
    
    @ApiModelProperty("TG 语言代码")
    @JSONField(name = "tg_language_code")
    private String tg_language_code;
    
    // ========== 链上提现 ==========
    @ApiModelProperty("提现地址")
    @JSONField(name = "to_address")
    private String to_address;
    
    @ApiModelProperty("链类型")
    @JSONField(name = "chain")
    private String chain;
}
