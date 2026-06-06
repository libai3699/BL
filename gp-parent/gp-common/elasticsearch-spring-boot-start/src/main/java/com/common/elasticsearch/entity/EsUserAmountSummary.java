package com.common.elasticsearch.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EsUserAmountSummary {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /***
     * 飞机名称
     */
    @ApiModelProperty(value = "飞机名称")
    private String userTgName;

    /**
     * 游戏厂商编码
     */
    @ApiModelProperty(value = "游戏厂商编码")
    private String plateCode;

    /**
     * 投注金额
     */
    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    /**
     * 投注笔数
     */
    @ApiModelProperty(value = "投注笔数")
    private Integer betNum;

    /**
     * 游戏类型
     */
    @ApiModelProperty(value = "游戏类型")
    private String gameTypeCode;

    /**
     * 派彩
     */
    @ApiModelProperty(value = "派彩")
    private BigDecimal win;

    /**
     * 打码量
     */
    @ApiModelProperty(value = "打码量")
    private BigDecimal codeAmount;

    /**
     * 盈亏
     */
    @ApiModelProperty(value = "盈亏")
    private BigDecimal winLoss;


    public EsUserAmountSummary(Long userId, BigDecimal betAmount, BigDecimal winLoss, Integer betNum, BigDecimal win, BigDecimal codeAmount,
                               String plateCode, String gameTypeCode) {
        this.userId = userId;
        this.betAmount = betAmount;
        this.winLoss = winLoss;
        this.betNum = betNum;
        this.win = win;
        this.codeAmount = codeAmount;
        this.plateCode = plateCode;
        this.gameTypeCode = gameTypeCode;
    }

}
