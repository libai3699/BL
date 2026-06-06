package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
    * 用户表
    */
@ApiModel(description="校验信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCheckInfoVo {

    @ApiModelProperty("是否校验手机号! (0 否, 1 是)")
    private Integer checkPhone = 0;
    @ApiModelProperty("是否校验邮箱! (0 否, 1 是)")
    private Integer checkEmail = 0;
    @ApiModelProperty("是否校验谷歌验证码! (0 否, 1 是)")
    private Integer checkGoogleCode = 0;

}
