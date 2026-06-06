package com.gp.common.mybatisplus.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author axing
 * @version 1.0
 * @date 2024/5/7/007 11:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BetRank implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户")
    private TUser user;
    @ApiModelProperty(value = "投注额")
    private BigDecimal betAmount;



}
