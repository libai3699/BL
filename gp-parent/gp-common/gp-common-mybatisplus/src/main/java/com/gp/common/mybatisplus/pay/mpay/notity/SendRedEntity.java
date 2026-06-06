package com.gp.common.mybatisplus.pay.mpay.notity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SendRedEntity {


    private static final long serialVersionUID = 1L;

    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;

    /** tg用户Id */
    @ApiModelProperty("tg用户Id")
    private Long userTgId;




}
