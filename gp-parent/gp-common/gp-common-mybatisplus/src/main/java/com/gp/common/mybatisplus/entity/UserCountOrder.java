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
import java.util.List;

/**
 * 每日用户订单统计对象 t_user_count_order
 *
 * @author axing
 * @date 2024-05-16
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@ApiModel("每日用户订单统计")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_count_order")
public class UserCountOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserCountOrder";

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
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 飞机名称
     */
    @ApiModelProperty("飞机名称")
    @TableField(exist = false)
    @Excel(name = "飞机名称")
    private String userTgName;
    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    @TableField("channel_id")
    @Excel(name = "渠道id")
    private Long channelId;
    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名称")
    @TableField(exist = false)
    @Excel(name = "渠道名称")
    private String channelName;

    /**
     * 当日渠道注册人数（按注册人数排序的代理排行接口返回）
     */
    @ApiModelProperty("当日渠道注册人数")
    @TableField(exist = false)
    @Excel(name = "当日渠道注册人数", cellType = Excel.ColumnType.NUMERIC)
    private Long channelRegisterCount;
    /**
     * 充提差额
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "充提差额")
    @Excel(name = "充提差额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal difference;

    /**
     * 区块链直冲金额
     */
    @ApiModelProperty("区块链直冲金额")
    @TableField("blockchain_recharge_amount")
    @Excel(name = "区块链直冲金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal blockchainRechargeAmount;

    /**
     * 总充值
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总充值")
    @Excel(name = "总充值", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalRechargeAmount;

    /**
     * 后台客服手动上分金额
     */
    @ApiModelProperty("后台客服手动上分金额")
    @TableField("customer_recharge_amount")
    @Excel(name = "后台客服手动上分金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerRechargeAmount;

    /**
     * pix充值金额
     */
    @ApiModelProperty("pix充值金额")
    @TableField("pixpay_recharge_amount")
    @Excel(name = "pix充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal pixpayRechargeAmount;

    /**
     * 越南盾充值金额
     */
    @ApiModelProperty("越南盾充值金额")
    @TableField("yndpay_recharge_amount")
    @Excel(name = "越南盾充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal yndpayRechargeAmount;

    /**
     * upay充值金额
     */
    @ApiModelProperty("upay充值金额")
    @TableField("upay_recharge_amount")
    @Excel(name = "upay充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal upayRechargeAmount;

    /**
     * pay1818充值金额
     */
    @ApiModelProperty("pay1818充值金额")
    @TableField("pay1818_recharge_amount")
    @Excel(name = "pay1818充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal pay1818RechargeAmount;

    /**
     * MPAY充值金额
     */
    @ApiModelProperty("MPAY充值金额")
    @TableField("mpay_recharge_amount")
    @Excel(name = "MPAY充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal mpayRechargeAmount;

    /**
     * 总充值笔数
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总充值笔数")
    @Excel(name = "充值笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer totalRechargeCount;

    /**
     * 总提现
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "总提现")
    @Excel(name = "总提现", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalWithdrawalAmount;

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
     * 提现订单数-当日
     */
    @ApiModelProperty("提现订单数-当日")
    @TableField("withdraw_num")
    @Excel(name = "提现订单数", cellType = Excel.ColumnType.NUMERIC)
    private Integer withdrawNum;

    /**
     * 提现金额-当日
     */
    @ApiModelProperty("提现金额-当日")
    @TableField("withdraw_amount")
    @Excel(name = "提现订单金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal withdrawAmount;

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
    @ApiModelProperty(value = "彩金总金额")
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
    @Excel(name = "pix充值彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal pixBonusAmount;

    /**
     * 越南盾彩金金额
     */
    @ApiModelProperty("越南盾彩金金额")
    @TableField("ynd_bonus_amount")
    @Excel(name = "越南盾彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal yndBonusAmount;

    /**
     * upay彩金金额
     */
    @ApiModelProperty("upay彩金金额")
    @TableField("upay_bonus_amount")
    @Excel(name = "upay彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal upayBonusAmount;

    /**
     * pay1818彩金金额
     */
    @ApiModelProperty("pay1818彩金金额")
    @TableField("pay1818_bonus_amount")
    @Excel(name = "pay1818彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal pay1818BonusAmount;

    /**
     * 恒聚财-印度卢比充值金额
     */
    @ApiModelProperty("恒聚财-印度卢比充值金额")
    @TableField("rupee_recharge_amount")
    @Excel(name = "恒聚财-印度卢比充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rupeeRechargeAmount;

    /**
     * 恒聚财-印度卢比充值数量
     */
    @ApiModelProperty("恒聚财-印度卢比充值数量")
    @TableField("rupee_recharge_num")
    @Excel(name = "恒聚财-印度卢比充值数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer rupeeRechargeNum;

    /**
     * 恒聚财-印度卢比彩金金额
     */
    @ApiModelProperty("恒聚财-印度卢比彩金金额")
    @TableField("rupee_bonus_amount")
    @Excel(name = "恒聚财-印度卢比彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rupeeBonusAmount;

    /**
     * 恒聚财-波币充值金额
     */
    @ApiModelProperty("恒聚财-波币充值金额")
    @TableField("zloty_recharge_amount")
    @Excel(name = "恒聚财-波币充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal zlotyRechargeAmount;

    /**
     * 恒聚财-波币充值数量
     */
    @ApiModelProperty("恒聚财-波币充值数量")
    @TableField("zloty_recharge_num")
    @Excel(name = "恒聚财-波币充值数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer zlotyRechargeNum;

    /**
     * 恒聚财-波币彩金金额
     */
    @ApiModelProperty("恒聚财-波币彩金金额")
    @TableField("zloty_bonus_amount")
    @Excel(name = "恒聚财-波币彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal zlotyBonusAmount;

    /**
     * 恒聚财-汇旺充值金额
     */
    @ApiModelProperty("恒聚财-汇旺充值金额")
    @TableField("hwpay_recharge_amount")
    @Excel(name = "恒聚财-汇旺充值金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal hwpayRechargeAmount;

    /**
     * 恒聚财-汇旺充值数量
     */
    @ApiModelProperty("恒聚财-汇旺充值数量")
    @TableField("hwpay_recharge_num")
    @Excel(name = "恒聚财-汇旺充值数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer hwpayRechargeNum;

    /**
     * 恒聚财-汇旺彩金金额
     */
    @ApiModelProperty("恒聚财-汇旺彩金金额")
    @TableField("hwpay_bonus_amount")
    @Excel(name = "恒聚财-汇旺彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal hwpayBonusAmount;

    /**
     * 恒聚财-U支付充值金额
     */
    @ApiModelProperty("恒聚财-U支付充值金额")
    @TableField("u_recharge_amount")
    @Excel(name = "恒聚财-U支付充值金额", cellType = Excel.ColumnType.NUMERIC)
    @JsonProperty("uRechargeAmount")
    private BigDecimal uRechargeAmount;

    /**
     * 恒聚财-U支付充值数量
     */
    @ApiModelProperty("恒聚财-U支付充值数量")
    @TableField("u_recharge_num")
    @Excel(name = "恒聚财-U支付充值数量", cellType = Excel.ColumnType.NUMERIC)
    @JsonProperty("uRechargeNum")
    private Integer uRechargeNum;

    /**
     * 恒聚财-U支付彩金金额
     */
    @ApiModelProperty("恒聚财-U支付彩金金额")
    @TableField("u_bonus_amount")
    @Excel(name = "恒聚财-U支付彩金金额", cellType = Excel.ColumnType.NUMERIC)
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
     * 转账彩金金额
     */
    @ApiModelProperty("转账彩金金额")
    @TableField("transfer_bonus_amount")
    @Excel(name = "转账彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal transferBonusAmount;

    /**
     * 转盘活动奖励金额
     */
    @ApiModelProperty("转盘活动奖励金额")
    @TableField("play_wheel_term_award")
    @Excel(name = "转盘彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal playWheelTermAward;

    /**
     * 参入转盘活动次数
     */
    @ApiModelProperty("参入转盘活动次数")
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
     * 游戏投注数量(不分输赢)
     */
    @ApiModelProperty("游戏投注数量(不分输赢)")
    @TableField("bet_num")
    @Excel(name = "投注次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer betNum;

    /**
     * 用户游戏投注金额(不分输赢)-当日
     */
    @ApiModelProperty("用户游戏投注金额(不分输赢)-当日")
    @TableField("bet_amount")
    @Excel(name = "投注额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal betAmount;

    /**
     * 结算金额(返奖金额)-当日
     */
    @ApiModelProperty("结算金额(返奖金额)-当日")
    @TableField("settle_amount")
    @Excel(name = "派彩额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal settleAmount;

    /**
     * 有效投注金额(分输赢)-当日
     */
    @ApiModelProperty("有效投注金额(分输赢)-当日")
    @TableField("efficient_bet_amount")
    @Excel(name = "有效投注金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal efficientBetAmount;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("code_amount")
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /**
     * 输赢金额(返奖额减去投注额)-当日
     */
    @ApiModelProperty("输赢金额(返奖额减去投注额)-当日")
    @TableField("win_lose_amount")
    @Excel(name = "输赢金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoseAmount;

    /**
     * 已返水金额
     */
    @ApiModelProperty("已返水金额")
    @TableField("already_rebate_amount")
    @Excel(name = "已领取返水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal alreadyRebateAmount;

    /**
     * 待返水金额-未领取返水金额
     */
    @ApiModelProperty("待返水金额")
    @TableField("wait_rebate_amount")
    @Excel(name = "未领取返水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitRebateAmount;

    /**
     * 待反水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待反水")
    private BigDecimal waitRebate;

    /**
     * 已返佣金额-下级用户返水返给自己的金额
     */
    @ApiModelProperty("已返佣金额-下级用户返水返给自己的金额")
    @TableField("already_return_commission_amount")
    @Excel(name = "已领取外水金额", cellType = Excel.ColumnType.NUMERIC)
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
     * 返佣金额-下级用户返水返给自己的金额 - 未领取外水金额
     */
    @ApiModelProperty("返佣金额-下级用户返水返给自己的金额")
    @TableField("wait_return_commission_amount")
    @Excel(name = "未领取外水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitReturnCommissionAmount;

    /**
     * 待反外水
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "待反外水")
    private BigDecimal waitAntiExternalWater;

    /**
     * 计算 客损 = 输赢 + 已领取返水金额 + 已领取返佣金额  +  总彩金(= t_count_order表中的 (bonusAmount(彩金金额)
     * + customerBonusAmount(后台客服手动上分彩金金额)  + playWheelTermAward(转盘活动彩金)
     * + activityAwardBonusAmount(用户活动奖励领取奖励-彩金金额) + totalPacketAmount(总红包彩金)
     * - customerPointsDeductedAmount(后台客服手动下分金额-下分类型是扣除积分) ))
     */
    @ApiModelProperty("客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]")
    @TableField("customer_loss_amount")
    @Excel(name = "客损", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerLossAmount;

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
     * 统计计算时间
     */
    @ApiModelProperty("统计计算时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("calculations_time")
    @Excel(name = "统计计算时间")
    private Date calculationsTime;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency_id")
    private Integer currencyId;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField(exist = false)
    @Excel(name = "币种")
    private String currencyName;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    @TableField("item_id")
    @Excel(name = "币种id", cellType = Excel.ColumnType.NUMERIC)
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /**
     * 充值金额-当日
     */
    @ApiModelProperty("充值金额-当日")
    @TableField("recharge_amount")
    @Excel(name = "直充金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rechargeAmount;

    /**
     * 充值订单数-当日
     */
    @ApiModelProperty("充值订单数-当日")
    @TableField("recharge_num")
    @Excel(name = "直充笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer rechargeNum;

    /**
     * 实际到账金额-当日
     */
    @ApiModelProperty("实际到账金额-当日")
    @TableField("real_amount")
    @Excel(name = "直充实际到账金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal realAmount;

    /**
     * 手续费-当日
     */
    @ApiModelProperty("手续费-当日")
    @TableField("fee")
    @Excel(name = "直充手续费", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal fee;

    /**
     * 用户结余-当日
     */
    @ApiModelProperty("用户结余-当日")
    @TableField("user_amount")
    @Excel(name = "用户结余", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userAmount;

    /**
     * 后台客服手动上分订单数量
     */
    @ApiModelProperty("后台客服手动上分订单数量")
    @TableField("customer_recharge_num")
    @Excel(name = "手动上分笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer customerRechargeNum;

    /**
     * 后台客服手动下分订单数量-下分类型是真实提现
     */
    @ApiModelProperty("后台客服手动下分订单数量-下分类型是真实提现")
    @TableField("customer_real_cash_withdrawal_num")
    @Excel(name = "手动提现笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer customerRealCashWithdrawalNum;

    /**
     * 后台客服手动下分实际金额-下分类型是真实提现
     */
    @ApiModelProperty("后台客服手动下分实际金额-下分类型是真实提现")
    @TableField("customer_real_cash_withdrawal_real_amount")
    @Excel(name = "手动提现实际到账金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerRealCashWithdrawalRealAmount;

    /**
     * 用户收入
     */
    @ApiModelProperty("用户收入")
    @TableField("user_earnings")
    @Excel(name = "用户收入", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userEarnings;

    /**
     * 用户支出
     */
    @ApiModelProperty("用户支出")
    @TableField("user_expenses")
    @Excel(name = "用户支出", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userExpenses;

    /**
     * MPAY充值充值数量
     */
    @ApiModelProperty("MPAY充值充值数量")
    @TableField("mpay_recharge_num")
    @Excel(name = "MPAY充值充值笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer mpayRechargeNum;

    /**
     * 区块链直冲数量
     */
    @ApiModelProperty("区块链直冲数量")
    @TableField("blockchain_recharge_num")
    @Excel(name = "区块链直冲笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer blockchainRechargeNum;

    /**
     * pix充值数量
     */
    @ApiModelProperty("pix充值数量")
    @TableField("pixpay_recharge_num")
    @Excel(name = "pix充值笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer pixpayRechargeNum;

    /**
     * 越南盾充值数量
     */
    @ApiModelProperty("越南盾充值数量")
    @TableField("yndpay_recharge_num")
    @Excel(name = "越南盾充值笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer yndpayRechargeNum;

    /**
     * upay充值数量
     */
    @ApiModelProperty("upay充值数量")
    @TableField("upay_recharge_num")
    @Excel(name = "upay充值数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer upayRechargeNum;

    /**
     * pay1818充值充值数量
     */
    @ApiModelProperty("pay1818充值充值数量")
    @TableField("pay1818_recharge_num")
    @Excel(name = "pay1818充值充值数量", cellType = Excel.ColumnType.NUMERIC)
    private Integer pay1818RechargeNum;

    /**
     * 上级用户id
     */
    @ApiModelProperty("上级用户id")
    @TableField("super_user_id")
    @Excel(name = "上级用户id")
    private Long superUserId;

    /**
     * 上级飞机id
     */
    @ApiModelProperty("上级飞机id")
    @TableField("super_user_tg_id")
    @Excel(name = "上级飞机id")
    private Long superUserTgId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
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
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    @ApiModelProperty("类型")
    @TableField(exist = false)
    private Integer type = 0;

    @ApiModelProperty("每日新增人数")
    @TableField(exist = false)
    private Integer countNew;

    @ApiModelProperty(value = "弹珠打码量")
    @TableField(exist = false)
    private BigDecimal dZBetAmount;

    /**
     * 股东ID
     */
    @TableField(exist = false)
    private Long shareholderId;

    /**
     * 用户list
     */
    @TableField(exist = false)
    private List<Long> userIds;

    /**
     * 商户编码
     */
    @ApiModelProperty("商户编码")
    @TableField(exist = false)
    private String merchantCode;

    /**
     * 商户名称
     */
    @ApiModelProperty("商户名称")
    @TableField(exist = false)
    private String merchantName;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @TableField(exist = false)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @TableField(exist = false)
    private String endTime;

    /**
     * 标识 1-日 2-月
     */
    @ApiModelProperty("标识 1-日 2-月")
    @TableField(exist = false)
    private Integer identity = 2;
}
