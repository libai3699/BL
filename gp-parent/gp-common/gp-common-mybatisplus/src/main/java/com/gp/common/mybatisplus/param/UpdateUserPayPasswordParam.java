package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserPayPasswordParam implements Serializable {

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("新支付密码")
    private String payPassword;

}
