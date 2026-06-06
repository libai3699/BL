package com.gp.common.mybatisplus.pay.mpay.order;

import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单返回的详情
 */
@Data
public class QueryRechargeOrderDto extends WalletBase {


    @ApiModelProperty("appKey")
    private  String appKey;

    @ApiModelProperty("itemId")
    private Integer itemId;

    @ApiModelProperty("币名称")
    private String itemName;

    @ApiModelProperty("mask链名称")
    private String chainTag;
    /** 充值单号 */
    @ApiModelProperty("单号")
    private String upOrderNo;
    /**
     * 推送的用户id
     */
    @ApiModelProperty("用户id")
    private String clientUserId;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;
}
