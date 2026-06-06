package com.gp.common.mybatisplus.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * 救济金统计结果实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHelpMoneySum {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 日期
     */
    private String dayStr;

    /**
     * 彩金金额总和
     */
    private BigDecimal totalBonusMoney;

    /**
     * 投注金额总和
     */
    private BigDecimal totalBetMoney;

    /**
     * 中奖金额总和
     */
    private BigDecimal totalWinMoney;




}