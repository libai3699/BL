package com.gp.common.mybatisplus.pay.mpay.createAddress;


import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateAddressDto extends WalletBase {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;

    @ApiModelProperty("itemId")
    private Integer itemId;

    @ApiModelProperty("币名称")
    private String itemName;

    @ApiModelProperty("mask链名称")
    private String chainTag;

    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;
    //参数
    @ApiModelProperty("用户id")
    private Long userId;


}
