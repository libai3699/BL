package com.common.core.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 短信验证码对象
 *
 * @author ruoyi
 */
@Data
@ApiModel
public class UploadDto {
    /**
     * 类型
     */
    @NotNull(message = "1游戏、2-轮播、3-头像、4-转盘、5-公共、6-活动、7-购买币网站、8-社群通知、9-红包、10-上传文件、11-交易图片")
    @ApiModelProperty("类型")
    private Integer type;

    /**
     * 类型
     */
    @NotBlank(message = "fileName")
    @ApiModelProperty("文件名称")
    private String fileName;

}
