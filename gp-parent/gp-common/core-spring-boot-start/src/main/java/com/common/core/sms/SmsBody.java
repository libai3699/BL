package com.common.core.sms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 短信验证码对象
 *
 * @author ruoyi
 */
@Data
@ApiModel
public class SmsBody
{
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空 !")
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    private String code;
}
