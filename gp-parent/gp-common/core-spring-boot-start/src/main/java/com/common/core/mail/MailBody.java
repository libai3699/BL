package com.common.core.mail;

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
public class MailBody
{
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空 !")
    @ApiModelProperty("邮箱")
    private String email;

}
