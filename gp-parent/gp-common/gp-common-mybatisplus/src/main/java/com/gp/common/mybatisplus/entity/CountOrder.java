package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 每日订单统计对象 t_count_order
 *
 * @author axing
 * @date 2024-05-09
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("每日订单统计")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_count_order")
public class CountOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:CountOrder";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日期(年月日)
     */
    @ApiModelProperty("日期(年月日)")
    @TableField("day_str")
    @Excel(name = "日期(年月日)")
    private String dayStr;


    /**
     * 充提差额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "充提差额")
    @Excel(name = "充提差额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal difference;

    /**
     * 总充值
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总充值")
    @Excel(name = "总充值", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalRechargeAmount;


    /**
     * 用户剩余积分
     */
    @ApiModelProperty("用户剩余积分")
    @TableField("usdt_balance")
    @Excel(name = "剩余积分", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal usdtBalance;

    /**
     * 后台客服手动上分金额
     */
    @ApiModelProperty("后台客服手动上分金额")
    @TableField("customer_recharge_amount")
    @Excel(name = "手动上分金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerRechargeAmount;

    /**
     * 区块链直冲金额
     */
    @ApiModelProperty("区块链直冲金额")
    @TableField("blockchain_recharge_amount")
    private BigDecimal blockchainRechargeAmount;

    /**
     * pix充值金额
     */
    @ApiModelProperty("pix充值金额")
    @TableField("pixpay_recharge_amount")
    private BigDecimal pixpayRechargeAmount;

    /**
     * 越南盾充值金额
     */
    @ApiModelProperty("越南盾充值金额")
    @TableField("yndpay_recharge_amount")
    private BigDecimal yndpayRechargeAmount;

    /**
     * upay充值金额
     */
    @ApiModelProperty("upay充值金额")
    @TableField("upay_recharge_amount")
    private BigDecimal upayRechargeAmount;

    /**
     * pay1818充值金额
     */
    @ApiModelProperty("pay1818充值金额")
    @TableField("pay1818_recharge_amount")
    private BigDecimal pay1818RechargeAmount;

    /**
     * MPAY充值金额
     */
    @ApiModelProperty("MPAY充值金额")
    @TableField("mpay_recharge_amount")
    private BigDecimal mpayRechargeAmount;

    /**
     * 总充值笔数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总充值笔数")
    @Excel(name = "充值笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer totalRechargeCount;

    /**
     * 充值人数
     */
    @ApiModelProperty("充值人数")
    @TableField("recharge_people_num")
    @Excel(name = "充值人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer rechargePeopleNum;

    /**
     * 总提现
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总提现")
    @Excel(name = "总提现", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalWithdrawalAmount;

    /**
     * 提现订单数
     */
    @ApiModelProperty("提现订单数")
    @TableField("withdraw_num")
    @Excel(name = "提现订单数", cellType = Excel.ColumnType.NUMERIC)
    private Integer withdrawNum;

    /**
     * 提现金额
     */
    @ApiModelProperty("提现金额")
    @TableField("withdraw_amount")
    @Excel(name = "提现订单金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal withdrawAmount;

    /**
     * 法币提现订单数量
     */
    @ApiModelProperty("法币提现订单数量")
    @TableField("law_withdraw_num")
    @Excel(name = "法币提现订单数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer lawWithdrawNum;

    /**
     * 法币提现金额
     */
    @ApiModelProperty("法币提现金额")
    @TableField("law_withdraw_amount")
    @Excel(name = "法币订单提现金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal lawWithdrawAmount;

    /**
     * 后台客服手动下分金额-下分类型是真实提现
     */
    @ApiModelProperty("后台客服手动下分金额-下分类型是真实提现")
    @TableField("customer_real_cash_withdrawal_amount")
    @Excel(name = "手动提现金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerRealCashWithdrawalAmount;

    /**
     * 总提现笔数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总提现笔数")
    @Excel(name = "提现笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer totalWithdrawalCount;

    /**
     * 提现人数
     */
    @ApiModelProperty("提现人数")
    @TableField("withdrawal_people_num")
    @Excel(name = "提现人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer withdrawalPeopleNum;

    /**
     * 彩金总金额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "赠送总彩金")
    @Excel(name = "赠送总彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalBonusAmount;

    /**
     * 彩金总金额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总金额")
    @Excel(name = "总彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalBonusAmountAll;
    /**
     * 彩金金额
     */
    @ApiModelProperty("彩金金额")
    @TableField("bonus_amount")
    @Excel(name = "直充彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal bonusAmount;

    /**
     * pix彩金金额
     */
    @ApiModelProperty("pix彩金金额")
    @TableField("pix_bonus_amount")
    private BigDecimal pixBonusAmount;

    /**
     * 越南盾彩金金额
     */
    @ApiModelProperty("越南盾彩金金额")
    @TableField("ynd_bonus_amount")
    private BigDecimal yndBonusAmount;

    /**
     * upay彩金金额
     */
    @ApiModelProperty("upay彩金金额")
    @TableField("upay_bonus_amount")
    private BigDecimal upayBonusAmount;

    /**
     * pay1818彩金金额
     */
    @ApiModelProperty("pay1818彩金金额")
    @TableField("pay1818_bonus_amount")
    private BigDecimal pay1818BonusAmount;

    /**
     * 恒聚财-印度卢比充值金额
     */
    @ApiModelProperty("恒聚财-印度卢比充值金额")
    @TableField("rupee_recharge_amount")
    private BigDecimal rupeeRechargeAmount;

    /**
     * 恒聚财-印度卢比充值数量
     */
    @ApiModelProperty("恒聚财-印度卢比充值数量")
    @TableField("rupee_recharge_num")
    private Integer rupeeRechargeNum;

    /**
     * 恒聚财-印度卢比彩金金额
     */
    @ApiModelProperty("恒聚财-印度卢比彩金金额")
    @TableField("rupee_bonus_amount")
    private BigDecimal rupeeBonusAmount;

    /**
     * 恒聚财-波币充值金额
     */
    @ApiModelProperty("恒聚财-波币充值金额")
    @TableField("zloty_recharge_amount")
    private BigDecimal zlotyRechargeAmount;

    /**
     * 恒聚财-波币充值数量
     */
    @ApiModelProperty("恒聚财-波币充值数量")
    @TableField("zloty_recharge_num")
    private Integer zlotyRechargeNum;

    /**
     * 恒聚财-波币彩金金额
     */
    @ApiModelProperty("恒聚财-波币彩金金额")
    @TableField("zloty_bonus_amount")
    private BigDecimal zlotyBonusAmount;

    /**
     * 恒聚财-汇旺充值金额
     */
    @ApiModelProperty("恒聚财-汇旺充值金额")
    @TableField("hwpay_recharge_amount")
    private BigDecimal hwpayRechargeAmount;

    /**
     * 恒聚财-汇旺充值数量
     */
    @ApiModelProperty("恒聚财-汇旺充值数量")
    @TableField("hwpay_recharge_num")
    private Integer hwpayRechargeNum;

    /**
     * 恒聚财-汇旺彩金金额
     */
    @ApiModelProperty("恒聚财-汇旺彩金金额")
    @TableField("hwpay_bonus_amount")
    private BigDecimal hwpayBonusAmount;

    /**
     * 恒聚财-U支付充值金额
     */
    @ApiModelProperty("恒聚财-U支付充值金额")
    @TableField("u_recharge_amount")
    @JsonProperty("uRechargeAmount")
    private BigDecimal uRechargeAmount;

    /**
     * 恒聚财-U支付充值数量
     */
    @ApiModelProperty("恒聚财-U支付充值数量")
    @TableField("u_recharge_num")
    @JsonProperty("uRechargeNum")
    private Integer uRechargeNum;

    /**
     * 恒聚财-U支付彩金金额
     */
    @ApiModelProperty("恒聚财-U支付彩金金额")
    @TableField("u_bonus_amount")
    @JsonProperty("uBonusAmount")
    private BigDecimal uBonusAmount;

    /**
     * 后台客服手动上分彩金金额
     */
    @ApiModelProperty("后台客服手动上分彩金金额")
    @TableField("customer_bonus_amount")
    @Excel(name = "手动上分赠送彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerBonusAmount;

    /**
     * 转盘活动奖励总金额
     */
    @ApiModelProperty("转盘活动奖励总金额")
    @TableField("play_wheel_term_award")
    @Excel(name = "转盘彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal playWheelTermAward;

    /**
     * 参入转盘活动总次数
     */
    @ApiModelProperty("参入转盘活动总次数")
    @TableField("play_wheel_term_num")
    @Excel(name = "转盘次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer playWheelTermNum;

    /**
     * 用户活动奖励领取奖励-彩金金额
     */
    @ApiModelProperty("用户活动奖励领取奖励-彩金金额")
    @TableField("activity_award_bonus_amount")
    @Excel(name = "用户活动奖励领取奖励-彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal activityAwardBonusAmount;

    /**
     * 用户活动奖励领取次数-奖励彩金
     */
    @ApiModelProperty("用户活动奖励领取次数-奖励彩金")
    @TableField("receive_bonus_num")
    @Excel(name = "用户活动奖励领取次数-奖励彩金", cellType = Excel.ColumnType.NUMERIC)
    private Integer receiveBonusNum;

    /**
     * 用户活动奖励领取奖励-转盘总次数
     */
    @ApiModelProperty("用户活动奖励领取奖励-转盘总次数")
    @TableField("activity_award_total_num")
    @Excel(name = "用户活动奖励领取奖励-转盘总次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer activityAwardTotalNum;

    /**
     * 用户活动奖励领取次数-奖励转盘次数
     */
    @ApiModelProperty("用户活动奖励领取次数-奖励转盘次数")
    @TableField("receive_turntable_num")
    @Excel(name = "用户活动奖励领取次数-奖励转盘次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer receiveTurntableNum;

    /**
     * 后台客服手动下分金额-下分类型是扣除积分
     */
    @ApiModelProperty("后台客服手动下分金额-下分类型是扣除积分")
    @TableField("customer_points_deducted_amount")
    @Excel(name = "彩金扣款", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerPointsDeductedAmount;

    /**
     * 后台客服手动下分订单数量-下分类型是扣除积分
     */
    @ApiModelProperty("后台客服手动下分订单数量-下分类型是扣除积分")
    @TableField("customer_points_deducted_num")
    @Excel(name = "彩金扣款次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer customerPointsDeductedNum;

    /**
     * 游戏投注总数量(不分输赢)
     */
    @ApiModelProperty("游戏投注总数量(不分输赢)")
    @TableField("bet_num")
    @Excel(name = "投注次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer betNum;

    /**
     * 游戏投注总金额(不分输赢)-当日
     */
    @ApiModelProperty("游戏投注总金额(不分输赢)-当日")
    @TableField("bet_amount")
    @Excel(name = "投注额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal betAmount;

    /**
     * 结算总金额(返奖总金额)-当日
     */
    @ApiModelProperty("结算总金额(返奖总金额)-当日")
    @TableField("settle_amount")
    @Excel(name = "派彩额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal settleAmount;

    /**
     * 有效投注总金额(分输赢)-当日
     */
    @ApiModelProperty("有效投注总金额(分输赢)-当日")
    @TableField("efficient_bet_amount")
    @Excel(name = "有效投注额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal efficientBetAmount;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("code_amount")
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /**
     * 输赢总金额(返奖额减去投注额)-当日
     */
    @ApiModelProperty("输赢总金额(返奖额减去投注额)-当日")
    @TableField("win_lose_amount")
    @Excel(name = "游戏输赢", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoseAmount;

    /**
     * 未结算注单数量
     */
    @ApiModelProperty("未结算注单数量")
    @TableField("unsettled_bets_num")
    @Excel(name = "未结算注单数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer unsettledBetsNum;

    /**
     * 未结算注单金额
     */
    @ApiModelProperty("未结算注单金额")
    @TableField("unsettled_bets_amount")
    @Excel(name = "未结算注单金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal unsettledBetsAmount;

    /**
     * 已返水总金额
     */
    @ApiModelProperty("已返水总金额")
    @TableField("already_rebate_amount")
    @Excel(name = "已返水总金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal alreadyRebateAmount;

    /**
     * 待返水总金额
     */
    @ApiModelProperty("待返水总金额")
    @TableField("wait_rebate_amount")
    @Excel(name = "累计待返水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitRebateAmount;

    /**
     * 待反水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待反水")
    @Excel(name = "待反水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitRebate;

    /**
     * 已返佣总金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("已返佣总金额-下级用户返水返给自己的金额")
    @TableField("already_return_commission_amount")
    @Excel(name = "外水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal alreadyReturnCommissionAmount;
    /**
     * 未领取的代理工资
     */
    @ApiModelProperty("未领取的代理工资")
    @TableField("unclaimed_agent_salary_amount")
    @Excel(name = "未领取的代理工资", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal unclaimedAgentSalaryAmount;

    /**
     * 已领取的代理工资
     */
    @ApiModelProperty("已领取的代理工资")
    @TableField("claimed_agent_salary_amount")
    @Excel(name = "已领取的代理工资", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal claimedAgentSalaryAmount;
    /**
     * 待返佣总金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("待返佣总金额-下级用户返水返给自己的金额")
    @TableField("wait_return_commission_amount")
    @Excel(name = "累计待反外水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitReturnCommissionAmount;

    /**
     * 待反外水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待反外水")
    @Excel(name = "待反外水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitAntiExternalWater;

    /**
     * 客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]
     */
    @ApiModelProperty("客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]")
    @TableField("customer_loss_amount")
    @Excel(name = "客损", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerLossAmount;


    /**
     * 新增用户
     */
    @ApiModelProperty("新增用户")
    @TableField("user_num")
    @Excel(name = "注册人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer userNum;

    /**
     * 活跃人数
     */
    @ApiModelProperty("活跃人数")
    @TableField("active_people_num")
    @Excel(name = "活跃人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer activePeopleNum;

    /**
     * 首存
     */
    @ApiModelProperty("首存")
    @TableField("first_num")
    @Excel(name = "首存人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer firstNum;

    /**
     * 首存金额
     */
    @ApiModelProperty("首存金额")
    @TableField("first_amount")
    @Excel(name = "首存金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal firstAmount;

    /**
     * 二存(含)以上,充值用户人数
     */
    @ApiModelProperty("二存(含)以上,充值用户人数")
    @TableField("second_num")
    @Excel(name = "二存人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer secondNum;

    /**
     * 二存(含)以上,充值金额
     */
    @ApiModelProperty("二存(含)以上,充值金额")
    @TableField("second_amount")
    @Excel(name = "二存金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal secondAmount;

    /**
     * 复存人数
     */
    @TableField("restore_num")
    @ApiModelProperty(value = "复存人数")
    @Excel(name = "复存人数", cellType = Excel.ColumnType.NUMERIC)
    private Integer restoreNum;

    /**
     * 复存金额
     */
    @TableField("restore_amount")
    @ApiModelProperty(value = "复存金额")
    @Excel(name = "复存金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal restoreAmount;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency_id")
    private Integer currencyId;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    @TableField("item_id")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    private String chainTag;

    /**
     * 充值金额
     */
    @ApiModelProperty("充值金额")
    @TableField("recharge_amount")
    private BigDecimal rechargeAmount;

    /**
     * 充值订单数
     */
    @ApiModelProperty("充值订单数")
    @TableField("recharge_num")
    private Integer rechargeNum;

    /**
     * 实际到账金额
     */
    @ApiModelProperty("实际到账金额")
    @TableField("real_amount")
    private BigDecimal realAmount;

    /**
     * 手续费
     */
    @ApiModelProperty("手续费")
    @TableField("fee")
    private BigDecimal fee;

    /**
     * 用户结余
     */
    @ApiModelProperty("用户结余")
    @TableField("user_amount")
    private BigDecimal userAmount;

    /**
     * 三天前注册人数中当日有打码量的人数
     */
    @ApiModelProperty("三天前注册人数中当日有打码量的人数")
    @TableField("three_remain_num")
    private Integer threeRemainNum;

    /**
     * 三日留存,单位(%),三天前注册人数中当日有打码量的人数除以三天前注册的用户人数
     */
    @ApiModelProperty("三日留存,单位(%),三天前注册人数中当日有打码量的人数除以三天前注册的用户人数")
    @TableField("three_remain")
    private BigDecimal threeRemain;

    /**
     * 七天前注册人数中当日有打码量的人数
     */
    @ApiModelProperty("七天前注册人数中当日有打码量的人数")
    @TableField("seven_remain_num")
    private Integer sevenRemainNum;

    /**
     * 七日留存,单位(%),七天前注册人数中当日有打码量的人数除以七天前注册的用户人数
     */
    @ApiModelProperty("七日留存,单位(%),七天前注册人数中当日有打码量的人数除以七天前注册的用户人数")
    @TableField("seven_remain")
    private BigDecimal sevenRemain;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /**
     * 后台客服手动上分订单数量
     */
    @ApiModelProperty("后台客服手动上分订单数量")
    @TableField("customer_recharge_num")
    private Integer customerRechargeNum;

    /**
     * 后台客服手动下分订单数量-下分类型是真实提现
     */
    @ApiModelProperty("后台客服手动下分订单数量-下分类型是真实提现")
    @TableField("customer_real_cash_withdrawal_num")
    private Integer customerRealCashWithdrawalNum;

    /**
     * 后台客服手动下分实际金额-下分类型是真实提现
     */
    @ApiModelProperty("后台客服手动下分实际金额-下分类型是真实提现")
    @TableField("customer_real_cash_withdrawal_real_amount")
    private BigDecimal customerRealCashWithdrawalRealAmount;

    /**
     * 用户收入
     */
    @ApiModelProperty("用户收入")
    @TableField("user_earnings")
    private BigDecimal userEarnings;

    /**
     * 用户支出
     */
    @ApiModelProperty("用户支出")
    @TableField("user_expenses")
    private BigDecimal userExpenses;

    /**
     * MPAY充值充值数量
     */
    @ApiModelProperty("MPAY充值充值数量")
    @TableField("mpay_recharge_num")
    private Integer mpayRechargeNum;

    /**
     * 区块链直冲数量
     */
    @ApiModelProperty("区块链直冲数量")
    @TableField("blockchain_recharge_num")
    private Integer blockchainRechargeNum;

    /**
     * pix充值数量
     */
    @ApiModelProperty("pix充值数量")
    @TableField("pixpay_recharge_num")
    private Integer pixpayRechargeNum;

    /**
     * 越南盾充值数量
     */
    @ApiModelProperty("越南盾充值数量")
    @TableField("yndpay_recharge_num")
    private Integer yndpayRechargeNum;

    /**
     * upay充值数量
     */
    @ApiModelProperty("upay充值数量")
    @TableField("upay_recharge_num")
    private Integer upayRechargeNum;

    /**
     * pay1818充值充值数量
     */
    @ApiModelProperty("pay1818充值充值数量")
    @TableField("pay1818_recharge_num")
    private Integer pay1818RechargeNum;

    /**
     * 新人 红包数量
     */
    @ApiModelProperty("新人红包数量")
    @TableField("new_person_num")
    @Excel(name = "新人红包数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer newPersonNum;

    /**
     * 新人红包金额
     */
    @ApiModelProperty("新人红包金额")
    @TableField("new_person_amount")
    @Excel(name = "新人红包金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal newPersonAmount;

    /**
     * 私人红包数量
     */
    @ApiModelProperty("私人红包数量")
    @TableField("private_person_num")
    @Excel(name = "私人红包数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer privatePersonNum;

    /**
     * 私人红包金额
     */
    @ApiModelProperty("私人红包金额")
    @TableField("private_person_amount")
    @Excel(name = "私人红包金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal privatePersonAmount;

    /**
     * 群红包数量
     */
    @ApiModelProperty("群红包数量")
    @TableField("group_num")
    @Excel(name = "群红包数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer groupNum;

    /**
     * 群红包金额
     */
    @ApiModelProperty("群红包金额")
    @TableField("group_amount")
    @Excel(name = "群红包金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal groupAmount;

    /**
     * 超级红包数量
     */
    @ApiModelProperty("超级红包数量")
    @TableField("super_num")
    @Excel(name = "群红包数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer superNum;

    /**
     * 超级群红包金额
     */
    @ApiModelProperty("超级群红包金额")
    @TableField("super_amount")
    @Excel(name = "群红包金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superAmount;

    /**
     * 红包雨-私人红包数量
     */
    @ApiModelProperty("红包雨-私人红包数量")
    @TableField("red_private_num")
    @Excel(name = "红包雨-私人红包数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer redPrivateNum;

    /**
     * 红包雨-私人红包金额
     */
    @ApiModelProperty("红包雨-私人红包金额")
    @TableField("red_private_amount")
    @Excel(name = "红包雨-私人红包金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal redPrivateAmount;

    /**
     * 红包雨-私人红包数量
     */
    @ApiModelProperty("红包雨数量")
    @TableField("red_num")
    @Excel(name = "红包雨数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer redNum;

    /**
     * 红包雨-私人红包金额
     */
    @ApiModelProperty("红包雨金额")
    @TableField("red_amount")
    @Excel(name = "红包雨金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal redAmount;

    /**
     * 总红包彩金
     */
    @TableField("total_packet_amount")
    @ApiModelProperty(value = "总红包彩金")
    @Excel(name = "总红包彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalPacketAmount;

    /**
     * vip等级奖励金额
     */
    @TableField("vip_reward_amount")
    @ApiModelProperty(value = "vip等级奖励金额")
    @Excel(name = "vip等级奖励金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal vipRewardAmount;

    /**
     * vip等级奖励次数
     */
    @TableField("vip_reward_num")
    @ApiModelProperty(value = "vip等级奖励次数")
    @Excel(name = "vip等级奖励次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer vipRewardNum;

    /**
     * 转账彩金金额
     */
    @ApiModelProperty("转账彩金金额")
    @TableField("transfer_bonus_amount")
    @Excel(name = "转账彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal transferBonusAmount;

    /**
     * 统计计算时间
     */
    @ApiModelProperty("统计计算时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("calculations_time")
    @Excel(name = "统计计算时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date calculationsTime;

    @ApiModelProperty("类型")
    @TableField(exist = false)
    private Integer type = 0;

    /**
     * 3日前注册总人数
     */
    @ApiModelProperty("3日前注册总人数")
    @TableField(exist = false)
    private Integer threeTotal;

    /**
     * 7日前注册总人数
     */
    @ApiModelProperty("7日前注册总人数")
    @TableField(exist = false)
    private Integer sevenTotal;
}
