package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代理基本信息
 */
@ApiModel(description="代理基本信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyConfigVo implements Serializable {
    @ApiModelProperty(value = "代理金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "极差开关")
    private Boolean switchB;




}
