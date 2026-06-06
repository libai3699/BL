package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: c
 * @Date: 2026/3/2 下午 06:18
 **/
@Data
public class UpdateAttentionLevelParam {

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 关注等级
     */
    @ApiModelProperty("关注状态:0-10")
    @NotNull(message = "关注等级不能为空")
    private Integer followStatus;

}
