package com.gp.common.mybatisplus.vo;

import com.common.mybatisplus.domain.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 代理基本信息
 */
@ApiModel(description = "代理基本信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyUserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "下级用户")
    private PageDTO<UserLowVo> page;

    @ApiModelProperty(value = "下级用户数量")
    private String lowNum;

}
