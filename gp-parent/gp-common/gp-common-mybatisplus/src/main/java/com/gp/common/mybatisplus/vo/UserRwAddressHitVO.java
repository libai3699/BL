package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 冲提地址反查命中的用户（弹窗列表）
 */
@ApiModel(description = "冲提地址关联用户")
@Data
public class UserRwAddressHitVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("TG用户ID")
    private Long userTgId;

    @ApiModelProperty("TG昵称")
    private String userTgName;

    @ApiModelProperty("TG用户名")
    private String userTgUsername;

    @ApiModelProperty("渠道ID")
    private Long channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;
}
