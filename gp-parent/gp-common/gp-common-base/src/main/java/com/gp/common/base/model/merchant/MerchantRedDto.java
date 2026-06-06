package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MerchantRedDto    {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;

    @ApiModelProperty("用户飞机id")
    private Long userTgId;



}
