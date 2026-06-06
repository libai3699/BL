package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class UserCountTotalAmount
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderAmount";

    /** 充值金额-当日 */
    @ApiModelProperty("充值金额")
    private BigDecimal rechargeAmount;
    /** pix充值金额-当日 */
    @ApiModelProperty("pix充值金额")
    private BigDecimal pixpayRechargeAmount;
    /** 越南盾-当日 */
    @ApiModelProperty("越南盾充值金额")
    private BigDecimal yndpayRechargeAmount;
    /** upay充值金额-当日 */
    @ApiModelProperty("upay充值金额")
    private BigDecimal upayRechargeAmount;
    /** upay充值金额-当日 */
    @ApiModelProperty("pay1818充值金额")
    private BigDecimal pay1818RechargeAmount;

    /** 彩金金额 */
    @ApiModelProperty("彩金金额")
    private BigDecimal bonusAmount;

    /**红包彩金*/
    @ApiModelProperty("红包彩金")
    private BigDecimal totalPacketAmount;

    /** 充值订单数-当日 */
    @ApiModelProperty("充值订单数")
    private Integer rechargeNum;

    /** 提现金额-当日 */
    @ApiModelProperty("提现金额")
    private BigDecimal withdrawAmount;

    /** 提现订单数-当日 */
    @ApiModelProperty("提现订单数")
    private Integer withdrawNum;

    /** 实际到账金额-当日 */
    @ApiModelProperty("实际到账金额")
    private BigDecimal realAmount;

    /** 手续费-当日 */
    @ApiModelProperty("手续费")
    private BigDecimal fee;



    /** 用户游戏投注金额(不分输赢)-当日 */
    @ApiModelProperty("用户游戏投注金额(不分输赢)")
    private BigDecimal betAmount;


    @ApiModelProperty("打码量")
    private BigDecimal codeAmount;

    /** 游戏投注数量(不分输赢) */
    @ApiModelProperty("游戏投注数量(不分输赢)")
    private Integer betNum;

    /** 结算金额(返奖金额)-当日 */
    @ApiModelProperty("结算金额(返奖金额)")
    private BigDecimal settleAmount;

    /** 输赢金额(返奖额减去投注额)-当日 */
    @ApiModelProperty("输赢金额(返奖额减去投注额)-当日")
    private BigDecimal winLoseAmount;

    /** 有效投注金额(分输赢)-当日 */
    @ApiModelProperty("有效投注金额(分输赢)-当日")
    private BigDecimal efficientBetAmount;

    /** 待返水金额 */
    @ApiModelProperty("待返水金额")
    private BigDecimal waitRebateAmount;

    /** 已返水金额 */
    @ApiModelProperty("已返水金额")
    private BigDecimal alreadyRebateAmount;

    /** 返佣金额-下级用户返水返给自己的金额 */
    @ApiModelProperty("返佣金额-下级用户返水返给自己的金额")
    private BigDecimal waitReturnCommissionAmount;

    /** 已返佣金额-下级用户返水返给自己的金额 */
    @ApiModelProperty("已返佣金额-下级用户返水返给自己的金额")
    private BigDecimal alreadyReturnCommissionAmount;

    /** 用户活动奖励领取奖励-彩金金额 */
    @ApiModelProperty("用户活动奖励领取奖励-彩金金额")
    private BigDecimal activityAwardBonusAmount;
    @ApiModelProperty("转盘彩金-彩金金额")
    private BigDecimal playWheelTermAward;

    /** 用户活动奖励领取次数-奖励彩金 */
    @ApiModelProperty("用户活动奖励领取次数-奖励彩金")
    private Integer receiveBonusNum;

    /** pix-奖励彩金 */
    @ApiModelProperty("pix-彩金彩金")
    private BigDecimal pixBonusAward;
    /** 越南盾-奖励彩金 */
    @ApiModelProperty("越南盾彩金彩金")
    private BigDecimal yndBonusAward;
    /** pix-奖励彩金 */
    @ApiModelProperty("upay-彩金彩金")
    private BigDecimal upayBonusAward;
    /** pix-奖励彩金 */
    @ApiModelProperty("py1818彩金彩金")
    private BigDecimal pay1818BonusAmount;

    @ApiModelProperty("转账彩金")
    private BigDecimal transferBonusAmount;
    /** 用户活动奖励领取奖励-转盘总次数 */
    @ApiModelProperty("用户活动奖励领取奖励-转盘总次数")
    private Integer activityAwardTotalNum;

    /** 用户活动奖励领取次数-奖励转盘次数 */
    @ApiModelProperty("用户活动奖励领取次数-奖励转盘次数")
    private Integer receiveTurntableNum;


    /** 后台客服手动上分金额 */
    @ApiModelProperty("后台客服手动上分金额")
    private BigDecimal customerRechargeAmount;

    /** 后台客服手动上分彩金金额 */
    @ApiModelProperty("后台客服手动上分彩金金额")
    private BigDecimal customerBonusAmount;

    /** 后台客服手动上分订单数量 */
    @ApiModelProperty("后台客服手动上分订单数量")
    private Integer customerRechargeNum;

    /** 后台客服手动下分金额-下分类型是真实提现 */
    @ApiModelProperty("后台客服手动下分金额-下分类型是真实提现")
    private BigDecimal customerRealCashWithdrawalAmount;

    /** 后台客服手动下分订单数量-下分类型是真实提现 */
    @ApiModelProperty("后台客服手动下分订单数量-下分类型是真实提现")
    private Integer customerRealCashWithdrawalNum;

    /** 后台客服手动下分实际金额-下分类型是真实提现 */
    @ApiModelProperty("后台客服手动下分实际金额-下分类型是真实提现")
    private BigDecimal customerRealCashWithdrawalRealAmount;

    /** 后台客服手动下分金额-下分类型是扣除积分 */
    @ApiModelProperty("后台客服手动下分金额-下分类型是扣除积分")
    private BigDecimal customerPointsDeductedAmount;

    /** 后台客服手动下分订单数量-下分类型是扣除积分 */
    @ApiModelProperty("后台客服手动下分订单数量-下分类型是扣除积分")
    private Integer customerPointsDeductedNum;


    /** pix-奖励彩金 */
    @ApiModelProperty("法币提现")
    private BigDecimal  lawWithdrawAmount;
    /** 客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)] */
    @ApiModelProperty("客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]")
    private BigDecimal customerLossAmount;
    /** pix-奖励彩金 */


    /**
     * 恒聚财-印度卢比彩金金额
     */
    @ApiModelProperty("恒聚财-印度卢比彩金金额")
    @TableField("rupee_bonus_amount")
    @Excel(name = "恒聚财-印度卢比彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rupeeBonusAmount;

    /**
     * 恒聚财-波币彩金金额
     */
    @ApiModelProperty("恒聚财-波币彩金金额")
    @TableField("zloty_bonus_amount")
    @Excel(name = "恒聚财-波币彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal zlotyBonusAmount;

    /**
     * 恒聚财-汇旺彩金金额
     */
    @ApiModelProperty("恒聚财-汇旺彩金金额")
    @TableField("hwpay_bonus_amount")
    @Excel(name = "恒聚财-汇旺彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal hwpayBonusAmount;

    /**
     * 恒聚财-U支付彩金金额
     */
    @ApiModelProperty("恒聚财-U支付彩金金额")
    @TableField("u_bonus_amount")
    @Excel(name = "恒聚财-U支付彩金金额", cellType = Excel.ColumnType.NUMERIC)
    @JsonProperty("uBonusAmount")
    private BigDecimal uBonusAmount;

    /**
     * 获取总充值金额（普通充值 + 客服手动上分）
     * @return 总充值金额
     */
    public BigDecimal getTotalRecharge() {
        BigDecimal recharge = rechargeAmount != null ? rechargeAmount : BigDecimal.ZERO;
        BigDecimal customerRecharge = customerRechargeAmount != null ? customerRechargeAmount : BigDecimal.ZERO;
        return recharge.add(customerRecharge);
    }

    /**
     * 获取总提现金额（普通提现 + 客服手动下分 + 法币提现）
     * @return 总提现金额
     */
    public BigDecimal getTotalWithdraw() {
        BigDecimal withdraw = withdrawAmount != null ? withdrawAmount : BigDecimal.ZERO;
        BigDecimal customerWithdraw = customerRealCashWithdrawalAmount != null ? customerRealCashWithdrawalAmount : BigDecimal.ZERO;
        BigDecimal lawWithdraw = lawWithdrawAmount != null ? lawWithdrawAmount : BigDecimal.ZERO;
        return withdraw.add(customerWithdraw).add(lawWithdraw);
    }

}
