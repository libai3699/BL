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
public class AgencyInfoDetailsVo implements Serializable {
    private static final long serialVersionUID = 1L;



    @ApiModelProperty("头像")
    private String userAvatar;

    /** 飞机名称 */
    @ApiModelProperty("飞机名称")
    private String userTgName;




    @ApiModelProperty(value = "当日总打码")
    private BigDecimal todayBetAmount;       // 当日总打码

    @ApiModelProperty(value = "当月总打码")
    private BigDecimal monthBetAmount;     // 当月总打码

    @ApiModelProperty(value = "当日总存款")
    private BigDecimal todayRechargeAmount;    // 当日总存款

    @ApiModelProperty(value = "当月总存款")
    private BigDecimal monthRechargeAmount;  // 当月总存款

    @ApiModelProperty(value = "当日总提款")
    private BigDecimal todayWithdrawAmount; // 当日总提款

    @ApiModelProperty(value = "当月总提款")
    private BigDecimal monthWithdrawAmount; // 当月总提款

    @ApiModelProperty(value = "当日总返水")
    private BigDecimal todayAlreadyRebateAmount;     // 当日总返水

    @ApiModelProperty(value = "当月总返水", example = "0.0557")
    private BigDecimal monthAlreadyRebateAmount;   // 当月总返水

    @ApiModelProperty(value = "当日总返佣")
    private BigDecimal todayAlreadyReturnCommissionAmount; // 当日总返佣

    @ApiModelProperty(value = "当月总返佣")
    private BigDecimal monthAlreadyReturnCommissionAmount; // 当月总返佣

    @ApiModelProperty(value = "当日总彩金")
    private BigDecimal todayTotalBonus;      // 当日总彩金

    @ApiModelProperty(value = "当月总彩金")
    private BigDecimal monthTotalBonus;    // 当月总彩金

    @ApiModelProperty(value = "当日亏损", example = "100.00")
    private BigDecimal todayLoss;      // 当日亏损

    @ApiModelProperty(value = "当月亏损", example = "1500.00")
    private BigDecimal monthLoss;    // 当月亏损
}
