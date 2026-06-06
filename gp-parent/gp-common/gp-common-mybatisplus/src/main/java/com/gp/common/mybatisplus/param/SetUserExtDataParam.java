package com.gp.common.mybatisplus.param;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SetUserExtDataParam implements Serializable  {


    /** t_user_ext表主键id */
    @ApiModelProperty("userExtId, t_user_ext表主键id")
    private Long userExtId;

    /** 加减数值 */
    @ApiModelProperty("加减数值")
    private BigDecimal addOrSubAmount;

    @ApiModelProperty("数值状态:1.加;2.减")
    private Integer amountStatus;


}
