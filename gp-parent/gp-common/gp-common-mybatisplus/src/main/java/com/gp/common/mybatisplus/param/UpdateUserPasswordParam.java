package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserPasswordParam implements Serializable {

    /**
     * 用户Id
     */
    @ApiModelProperty(value = "用户Id", required = true)
    private Long userId;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码", required = true)
    private String password;

}
