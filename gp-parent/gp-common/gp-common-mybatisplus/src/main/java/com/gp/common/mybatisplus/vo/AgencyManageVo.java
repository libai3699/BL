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
@ApiModel(description = "代理基本信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgencyManageVo implements Serializable {
    private static final long serialVersionUID = 1L;



    @ApiModelProperty(value = "人数")
    private Integer peopleNum;
    @ApiModelProperty(value = "比例")
    private BigDecimal rate;
}
