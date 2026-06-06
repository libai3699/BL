package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
    * 用户表
    */
@ApiModel(description="校验信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeExtValueVo {
    @ApiModelProperty("用户Id")
    private Long userId;
    @ApiModelProperty("用户属性类型")
    private Integer extType;
    @ApiModelProperty("变更值")
    private BigDecimal updateValue;
    @ApiModelProperty("关联的订单号")
    private String orderNo;
    @ApiModelProperty("关联的订单号类型")
    private Integer orderType;
    @ApiModelProperty("收入支出类型")
    private Integer accountType;
    @ApiModelProperty("账变类型")
    private Integer changeType;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("操作人")
    private String operator;
    @ApiModelProperty("扩展参数1")
    private String extParam1;
    @ApiModelProperty("扩展参数2")
    private String extParam2;

}
