package com.gp.common.mybatisplus.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 充值订单对象 t_order_amount
 *
 * @author axing
 * @date 2024-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameBetAmountTotalAmount implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 电子游戏类型代码
     */
    private BigDecimal gameTypeCode1;

    /**
     * 体育游戏类型代码
     */
    private BigDecimal gameTypeCode2;

    /**
     * 视讯游戏类型代码
     */
    private BigDecimal gameTypeCode3;

    /**
     * 彩票游戏类型代码
     */
    private BigDecimal gameTypeCode4;

    /**
     * 棋牌游戏类型代码
     */
    private BigDecimal gameTypeCode5;

    /**
     * 区块链游戏类型代码
     */
    private BigDecimal gameTypeCode6;

    /**
     * 捕鱼游戏类型代码
     */
    private BigDecimal gameTypeCode7;

    /**
     * 宾果游戏类型代码
     */
    private BigDecimal gameTypeCode8;

    /**
     * 弹珠游戏类型代码
     */
    private BigDecimal gameTypeCode9;

    // ==================== 投注额 ====================
    private BigDecimal gameTypeBet1;
    private BigDecimal gameTypeBet2;
    private BigDecimal gameTypeBet3;
    private BigDecimal gameTypeBet4;
    private BigDecimal gameTypeBet5;
    private BigDecimal gameTypeBet6;
    private BigDecimal gameTypeBet7;
    private BigDecimal gameTypeBet8;
    private BigDecimal gameTypeBet9;

    // ==================== 派彩额 ====================
    private BigDecimal gameTypeSettle1;
    private BigDecimal gameTypeSettle2;
    private BigDecimal gameTypeSettle3;
    private BigDecimal gameTypeSettle4;
    private BigDecimal gameTypeSettle5;
    private BigDecimal gameTypeSettle6;
    private BigDecimal gameTypeSettle7;
    private BigDecimal gameTypeSettle8;
    private BigDecimal gameTypeSettle9;

    // ==================== 游戏输赢 ====================
    private BigDecimal gameTypeWinLoss1;
    private BigDecimal gameTypeWinLoss2;
    private BigDecimal gameTypeWinLoss3;
    private BigDecimal gameTypeWinLoss4;
    private BigDecimal gameTypeWinLoss5;
    private BigDecimal gameTypeWinLoss6;
    private BigDecimal gameTypeWinLoss7;
    private BigDecimal gameTypeWinLoss8;
    private BigDecimal gameTypeWinLoss9;

}