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
public class MailConfigStart
{
    private String emailAppKey;
    private String emailTemplateId;
    private String emailAlias;

}
