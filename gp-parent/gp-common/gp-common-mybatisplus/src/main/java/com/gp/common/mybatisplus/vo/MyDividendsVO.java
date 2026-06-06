package com.gp.common.mybatisplus.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author: c
 * @Date: 2025/9/11 上午 09:52
 **/
@Data
@ToString
@ApiModel("我的分红VO")
public class MyDividendsVO {
    /**
     * 我的分红: 团队盈利 * 负盈利比例
     */
    @ApiModelProperty("我的分红")
    private BigDecimal teamProfit = BigDecimal.ZERO;

    /**
     * 团队盈利: 毛利润 - 管理费
     */
    @ApiModelProperty("团队盈利")
    private BigDecimal teamProfitU = BigDecimal.ZERO;

    /**
     * 负盈利比例:渠道-设置比例-分红比例
     */
    @ApiModelProperty("负盈利比例")
    private BigDecimal negativeProfitRatio = BigDecimal.ZERO;

    /**
     * 总充值:用户每日统计的总充值
     */
    @ApiModelProperty("总充值")
    private BigDecimal totalRecharge = BigDecimal.ZERO;

    /**
     * 总提现:用户每日统计的总提现
     */
    @ApiModelProperty("总提现")
    private BigDecimal totalWithdraw = BigDecimal.ZERO;

    /**
     * 总余额:用户每日统计的用户结余
     */
    @ApiModelProperty("总余额")
    private BigDecimal totalBalance = BigDecimal.ZERO;

    /**
     * 游戏输赢:用户每日统计的游戏输赢
     */
    @ApiModelProperty("游戏输赢")
    private BigDecimal gameWin = BigDecimal.ZERO;

    /**
     * 游戏手续费:团队盈利(U) * 手续费比例(配置管理:userAddressCount)
     */
    @ApiModelProperty("游戏手续费")
    private BigDecimal gameFee = BigDecimal.ZERO;

    /**
     * 实际到账：我的分红 - 游戏手续费
     */
    @ApiModelProperty("实际到账")
    private BigDecimal actualAmount = BigDecimal.ZERO;

    /**
     * 已返水金额
     */
    @ApiModelProperty("已返水金额")
    private BigDecimal alreadyRebateAmount = BigDecimal.ZERO;

    /**
     * 返佣金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("返佣金额")
    private BigDecimal waitReturnCommissionAmount = BigDecimal.ZERO;
    /**
     * 待返水金额(未领取反水)
     */
    @ApiModelProperty("待返水金额")
    private BigDecimal waitRebateAmount;

    /**
     * 已返外水金额
     */
    @ApiModelProperty("已返外水金额")
    private BigDecimal alreadyReturnCommissionAmount;

    /**
     * 彩金金额
     */
    @ApiModelProperty("直充彩金")
    private BigDecimal bonusAmount = BigDecimal.ZERO;

    /**
     * 后台客服手动上分彩金金额
     */
    @ApiModelProperty("手动上分赠送彩金")
    private BigDecimal customerBonusAmount = BigDecimal.ZERO;

    /**
     * 转盘活动奖励金额
     */
    @ApiModelProperty("转盘彩金")
    private BigDecimal playWheelTermAward = BigDecimal.ZERO;

    /**
     * 用户活动奖励领取奖励-彩金金额
     */
    @ApiModelProperty("活动奖励彩金金额")
    private BigDecimal activityAwardBonusAmount = BigDecimal.ZERO;

    /**
     * 总红包彩金
     */
    @ApiModelProperty(value = "总红包彩金")
    private BigDecimal totalPacketAmount = BigDecimal.ZERO;

    /**
     * 转账彩金金额
     */
    @ApiModelProperty("转账彩金金额")
    private BigDecimal transferBonusAmount = BigDecimal.ZERO;

    /**
     * 后台客服手动下分金额-下分类型是扣除积分
     */
    @ApiModelProperty("彩金扣款")
    private BigDecimal customerPointsDeductedAmount = BigDecimal.ZERO;

    /**
     * 彩金总金额
     */
    @ApiModelProperty(value = "总彩金")
    private BigDecimal totalBonusAmount = BigDecimal.ZERO;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    private BigDecimal codeAmount = BigDecimal.ZERO;
    /**
     * 充提差额
     */
    @ApiModelProperty(value = "充提差额")
    private BigDecimal difference = BigDecimal.ZERO;

    /**
     * 充值手续费比例
     */
    @ApiModelProperty(value = "充值手续费比例")
    private BigDecimal rechargeFeeRate = BigDecimal.ZERO;

    /**
     * 充值手续费金额
     */
    @ApiModelProperty(value = "充值手续费")
    private BigDecimal rechargeFee = BigDecimal.ZERO;

    /**
     * 提现手续费比例
     */
    @ApiModelProperty(value = "提现手续费比例")
    private BigDecimal withdrawFeeRate = BigDecimal.ZERO;

    /**
     * 提现手续费金额
     */
    @ApiModelProperty(value = "提现手续费")
    private BigDecimal withdrawFee = BigDecimal.ZERO;

    /**
     * 管理费比例
     */
    @ApiModelProperty(value = "管理费比例")
    private BigDecimal managementFeeRate = BigDecimal.ZERO;

    /**
     * 管理费金额
     */
    @ApiModelProperty(value = "管理费")
    private BigDecimal managementFee = BigDecimal.ZERO;

    /**
     * 官方收取比例(游戏手续费比例)
     */
    @ApiModelProperty(value = "官方收取比例")
    private BigDecimal gameFeeRate = BigDecimal.ZERO;

    /**
     * 毛利润
     */
    @ApiModelProperty(value = "毛利润")
    private BigDecimal grossProfit = BigDecimal.ZERO;
}
