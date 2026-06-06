package com.common.core.mq;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StreamAddMq {
    /**
     * 充值金额-当日
     */
    @ApiModelProperty("充值金额-当日")
    private BigDecimal rechargeAmount;

    /**
     * 彩金金额
     */
    @ApiModelProperty("彩金金额")
    private BigDecimal bonusAmount;

    /**
     * 充值订单数-当日
     */
    @ApiModelProperty("充值订单数-当日")
    private Integer rechargeNum;

    /**
     * 提现金额-当日
     */
    @ApiModelProperty("提现金额-当日")

    private BigDecimal withdrawAmount;

    /**
     * 提现订单数-当日
     */
    @ApiModelProperty("提现订单数-当日")

    private Integer withdrawNum;

    /**
     * 实际到账金额-当日
     */
    @ApiModelProperty("实际到账金额-当日")
    private BigDecimal realAmount;

    /**
     * 手续费-当日
     */
    @ApiModelProperty("手续费-当日")
    private BigDecimal fee;

    /**
     * 用户结余-当日
     */
    @ApiModelProperty("用户结余-当日")
    private BigDecimal userAmount;

    /**
     * 用户游戏投注金额(不分输赢)-当日
     */
    @ApiModelProperty("用户游戏投注金额(不分输赢)-当日")
    private BigDecimal betAmount;

    /**
     * 游戏投注数量(不分输赢)
     */
    @ApiModelProperty("游戏投注数量(不分输赢)")
    private Integer betNum;

    /**
     * 结算金额(返奖金额)-当日
     */
    @ApiModelProperty("结算金额(返奖金额)-当日")
    private BigDecimal settleAmount;

    /**
     * 输赢金额(返奖额减去投注额)-当日
     */
    @ApiModelProperty("输赢金额(返奖额减去投注额)-当日")
    private BigDecimal winLoseAmount;

    /**
     * 有效投注金额(分输赢)-当日
     */
    @ApiModelProperty("有效投注金额(分输赢)-当日")
    private BigDecimal efficientBetAmount;

    /**
     * 待返水金额
     */
    @ApiModelProperty("待返水金额")
    private BigDecimal waitRebateAmount;

    /**
     * 已返水金额
     */
    @ApiModelProperty("已返水金额")
    private BigDecimal alreadyRebateAmount;

    /**
     * 返佣金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("返佣金额-下级用户返水返给自己的金额")
    private BigDecimal waitReturnCommissionAmount;

    /**
     * 已返佣金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("已返佣金额-下级用户返水返给自己的金额")
    private BigDecimal alreadyReturnCommissionAmount;

    /**
     * 用户活动奖励领取奖励-彩金金额
     */
    @ApiModelProperty("用户活动奖励领取奖励-彩金金额")
    private BigDecimal activityAwardBonusAmount;

    /**
     * 用户活动奖励领取次数-奖励彩金
     */
    @ApiModelProperty("用户活动奖励领取次数-奖励彩金")
    private Integer receiveBonusNum;

    /**
     * 用户活动奖励领取奖励-转盘总次数
     */
    @ApiModelProperty("用户活动奖励领取奖励-转盘总次数")
    private Integer activityAwardTotalNum;

    /**
     * 用户活动奖励领取次数-奖励转盘次数
     */
    @ApiModelProperty("用户活动奖励领取次数-奖励转盘次数")
    private Integer receiveTurntableNum;

    /** 转盘活动奖励金额 */
    @ApiModelProperty("转盘活动奖励金额")
    private BigDecimal playWheelTermAward;

    /** 参入转盘活动次数 */
    @ApiModelProperty("参入转盘活动次数")
    private Integer playWheelTermNum;

    /** 用户收入 */
    @ApiModelProperty("用户收入")
    private BigDecimal userEarnings;

    /** 用户支出 */
    @ApiModelProperty("用户支出")
    private BigDecimal userExpenses;

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

    /** 上级用户id */
    @ApiModelProperty("上级用户id")
    private Long superUserId;

    /** 上级飞机id */
    @ApiModelProperty("上级飞机id")
    private Long superUserTgId;

    /** 渠道id,没有则为0 */
    @ApiModelProperty("渠道id,没有则为0")
    private Long channelId;

    /** 统计计算时间 */
    @ApiModelProperty("统计计算时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date calculationsTime;

    /** MPAY充值金额 */
    @ApiModelProperty("MPAY充值金额")
    private BigDecimal mpayRechargeAmount;

    /** MPAY充值充值数量 */
    @ApiModelProperty("MPAY充值充值数量")
    private Integer mpayRechargeNum;

    /** 区块链直冲金额 */
    @ApiModelProperty("区块链直冲金额")
    private BigDecimal blockchainRechargeAmount;

    /** 区块链直冲数量 */
    @ApiModelProperty("区块链直冲数量")
    private Integer blockchainRechargeNum;


    /** 打码量 */
    @ApiModelProperty("打码量")
    private BigDecimal codeAmount;
}


