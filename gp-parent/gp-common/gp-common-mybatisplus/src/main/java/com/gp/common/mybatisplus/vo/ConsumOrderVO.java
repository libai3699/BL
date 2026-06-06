package com.gp.common.mybatisplus.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author scent
 * @version 1.0
 * @date 2022/6/16 15:54
 */
@Data
@ToString
public class ConsumOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("存储每位首存用户的金额")
    private Map<Long, BigDecimal> userAmountMap;

    @ApiModelProperty("存储用户第二次存款的金额和数量")
    private Map<Long, Map<String,Object>> secondDataMap;

    @ApiModelProperty("存储用户复存存款的金额和数量")
    private Map<Long, Map<String,Object>> restoreDataMap;

    @ApiModelProperty("用户结余")
    private BigDecimal userAmount;

    @ApiModelProperty("渠道用户结余")
    private BigDecimal channelAmount;

    @ApiModelProperty("总结余")
    private BigDecimal orderAmount;




}
