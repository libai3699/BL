package com.gp.common.mybatisplus.pay.mpay.param;


import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MerchantQueryOrderDto extends WalletBase {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;


    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;
    //参数
    @ApiModelProperty("商户订单号")
    private String orderNo;


}
