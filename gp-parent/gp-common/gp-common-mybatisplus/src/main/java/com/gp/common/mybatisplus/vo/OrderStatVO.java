package com.gp.common.mybatisplus.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author scent
 * @version 1.0
 * @date 2022/6/16 15:54
 */
@Data
public class OrderStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ---------------------  当日数据 ------------------------------

    /**
     * 当日 充值金额 = t_count_order表中的 (rechargeAmount(充值金额) + customerRechargeAmount(后台客服手动上分金额))
     */
    @ApiModelProperty("当日充值金额 ")
    private BigDecimal todayRechargeAmount;

    /**
     * 当日 充值订单数量 = t_count_order表中的 (rechargeNum(充值订单数) + customerRechargeNum(后台客服手动上分订单数量))
     */
    @ApiModelProperty("当日充值订单数量")
    private Integer todayRechargeOrderNum;
    /**
     * 当日 当日注册人数 = t_user表中的 (当日创建时间的用户)
     */
    @ApiModelProperty("当日注册人数")
    private Integer todayRegisterNum;
    /**
     * 当日 充值人数 = (t_order_amount 中的user_id  + t_order_person(order_type = 1 类型(1 上分, 2 下分)) 的user_id ) 去重
     */
    @ApiModelProperty("当日充值人数")
    private Integer todayRechargePeopleNum;

    /**
     * 当日赠送总彩金（各类赠送彩金合计，不含彩金扣款；扣款见 customerPointsDeductedAmount）。
     */
    @ApiModelProperty("当日赠送彩金")
    private BigDecimal todayBonusAMount;

    /**
     * 当日总彩金（赠送合计减去彩金扣款 customerPointsDeductedAmount）
     */
    @ApiModelProperty("当日总彩金(扣减彩金扣款后)")
    private BigDecimal todayTotalBonusAmountAll;


    /**
     * 当日 提现金额 = t_count_order表中的 (withdrawAmount(提现金额) + customerRealCashWithdrawalAmount(后台客服手动下分金额-下分类型是真实提现) )
     */
    @ApiModelProperty("当日提现金额")
    private BigDecimal todayWithdrawAmount;

    /**
     * 当日 提现订单数量 = t_count_order表中的 (withdrawNum(提现订单数) + customerRealCashWithdrawalNum(后台客服手动下分订单数量-下分类型是真实提现))
     */
    @ApiModelProperty("当日提现订单数量")
    private Integer todayWithdrawOrderNum;

    /**
     * 当日 提现人数  = (t_order_withdraw 中的user_id  + t_order_person(order_type = 2  类型(1 上分, 2 下分) && lower_subtype = 1 下分类型(1.真实提现;2.扣除积分) ) 的user_id ) 去重
     */
    @ApiModelProperty("当日提现人数")
    private Integer todayWithdrawPeopleNum;

    /**
     * 当日 待返水金额  = t_count_order表中的 waitRebateAmount(待返水总金额)
     */
    @ApiModelProperty("当日待返水金额")
    private BigDecimal todayWaitRebateAmount;

    /**
     * 当日 已返水金额 = t_count_order表中的 alreadyRebateAmount(已返水总金额)
     */
    @ApiModelProperty("当日已返水金额")
    private BigDecimal todayAlreadyRebateAmount;


    /**
     * 当日 待返佣金额 = t_count_order表中的 waitReturnCommissionAmount(待返佣总金额-下级用户返水返给自己的金额)
     */
    @ApiModelProperty("当日待返佣金额")
    private BigDecimal todayWaitReturnCommissionAmount;

    /**
     * 当日 已返佣金额 = t_count_order表中的 alreadyReturnCommissionAmount(已返佣总金额-下级用户返水返给自己的金额)
     */
    @ApiModelProperty("当日已返佣金额")
    private BigDecimal todayAlreadyReturnCommissionAmount;

    /**
     * 当日 首存人数 = t_count_order表中的 firstNum(首存)
     */
    @ApiModelProperty("当日首存人数")
    private Integer todayFirstNum;

    /**
     * 当日 首存金额  = t_count_order表中的 firstAmount(首存金额)
     */
    @ApiModelProperty("当日首存金额")
    private BigDecimal todayFirstAmount;

    /**
     * 当日 二存(含)以上,充值用户人数  = t_count_order表中的 secondNum(二存(含)以上,充值用户人数)
     */
    @ApiModelProperty("当日二存(含)以上,充值用户人数")
    private Integer todaySecondNum;

    /**
     * 当日 二存(含)以上,充值金额  = t_count_order表中的 secondAmount(二存(含)以上,充值金额)
     */
    @ApiModelProperty("当日二存(含)以上,充值金额")
    private BigDecimal todaySecondAmount;

    /**
     * 当日 复存人数  = t_count_order表中的 RestoreNum(复存人数)
     */
    @ApiModelProperty("当日复存人数")
    private Integer todayRestoreNum;

    /**
     * 当日 复存金额  = t_count_order表中的 RestoreAmount(复存金额)
     */
    @ApiModelProperty("当日复存金额")
    private BigDecimal todayRestoreAmount;
    /**
     * 当日 游戏投注总金额(不分输赢)  = t_count_order表中的 betAmount(游戏投注总金额(不分输赢)-当日)
     */
    @ApiModelProperty("当日游戏投注总金额(不分输赢)")
    private BigDecimal todayBetAmount;

    /**
     * 当日 结算总金额(返奖总金额)-当日  = t_count_order表中的 SettleAmount(结算总金额(返奖总金额)-当日)
     */
    @ApiModelProperty("结算总金额(返奖总金额)-当日")
    private BigDecimal todaySettleAmount;

    /**
     *  当日 活动总金额(用户活动奖励领取奖励-彩金金额+转盘彩金 t_count_order表中的 playWheelTermAward+activityAwardBonusAmount)-当日
     */
    @ApiModelProperty("活动总金额")
    private BigDecimal todayActivityAmount;



//    /**
//     * 当日有效投注总金额(分输赢)  = t_count_order表中的 efficientBetAmount(有效投注总金额(分输赢)-当日)
//     */
//    @ApiModelProperty("")
//    private BigDecimal todayEfficientBetAmount;


    /**
     * 当日 活跃人数 = 当日有进行过投注的人数
     */
    @ApiModelProperty("当日活跃人数")
    private Integer todayActivePeopleNum;

    /**
     * 当日 客损 =  t_count_order表中的 customerLossAmount(客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)])
     */
    @ApiModelProperty("当日客损")
    private BigDecimal todayCustomerLossAmount;

    /**
     * 当日 充提差 = todayRechargeAmount(当日充值金额) - todayWithdrawAmount(当日提现金额);
     */
    @ApiModelProperty("当日充提差")
    private BigDecimal todayDifference;

    /**
     * 当前客户剩余分数(usdt余额) = t_user_wallet表  currency_id = 6  amount 的累计
     *  =  t_count_order表中的 userAmount(用户结余)
     */
    @ApiModelProperty("当前客户剩余分数(usdt余额)")
    private BigDecimal customerTotalUsdtBalance;

    /** 未结算注单金额 */
    @ApiModelProperty("未结算注单金额")
    private BigDecimal unsettledBetsAmount;

    /** 未结算注单数量 */
    @ApiModelProperty("未结算注单数量")
    private Integer unsettledBetsNum;



    // ---------------------  累计数据 ------------------------------

    //首存 和 2存 如果是累计的  ->  举个例子:
    // 我(假设就我一个人),  前天 第一次存, 存了100块,  那前天  首存人数 1,  二存 0, 首存笔数 1, 二存 0, 首存金额 100, 二存 0
    // 昨天 我又存了 300块,因为累计 所以   那 昨天           首存人数 1,  二存 1, 首存笔数 1, 二存 1, 首存金额  100, 二存 300
    //我今天又存了500块,   因为累计 , 所以 我今天的统计       首存人数 1,  二存 2, 首存笔数 1, 二存 2, 首存金额  100, 二存 800


    /**
     * 累计 充值金额 = t_count_order表中的 ( SUM(rechargeAmount)(充值金额) + SUM(customerRechargeAmount)(后台客服手动上分金额) )
     */
    @ApiModelProperty("累计充值金额")
    private BigDecimal totalRechargeAmount;

    /**
     * 累计 充值订单数量 = t_count_order表中的 ( SUM(rechargeNum)(充值订单数) + SUM(customerRechargeNum)(后台客服手动上分订单数量) )
     */
    @ApiModelProperty("累计充值订单数量")
    private Integer totalRechargeOrderNum;

    /**
     * 累计 充值人数 = (t_order_amount 中的user_id  + t_order_person(order_type = 1 类型(1 上分, 2 下分)) 的user_id )所有的数据 去重
     */
    @ApiModelProperty("累计充值人数")
    private Integer totalRechargePeopleNum;


    /**
     * 累计赠送总彩金（各类赠送彩金 SUM 合计，不含彩金扣款；扣款见 SUM(customerPointsDeductedAmount)）。
     */
    @ApiModelProperty("累计赠送彩金")
    private BigDecimal totalBonusAmount;

    /**
     * 累计总彩金（赠送分项 SUM 合计减去 SUM(customerPointsDeductedAmount)）
     */
    @ApiModelProperty("累计总彩金(扣减彩金扣款后)")
    private BigDecimal totalBonusAmountAll;


    /**
     * 累计 提现金额 = t_count_order表中的 (SUM(withdrawAmount)(提现金额) + SUM(customerRealCashWithdrawalAmount)(后台客服手动下分金额-下分类型是真实提现) )
     */
    @ApiModelProperty("累计提现金额")
    private BigDecimal totalWithdrawAmount;

    /**
     * 累计 提现订单数量 = t_count_order表中的 (SUM(withdrawNum)(提现订单数) + SUM(customerRealCashWithdrawalNum)(后台客服手动下分订单数量-下分类型是真实提现))
     */
    @ApiModelProperty("累计提现订单数量")
    private Integer totalWithdrawOrderNum;

    /**
     * 累计 提现人数  = (t_order_withdraw 中的user_id  + t_order_person(order_type = 2  类型(1 上分, 2 下分) && lower_subtype = 1 下分类型(1.真实提现;2.扣除积分) ) 的user_id ) 所有的数据 去重
     */
    @ApiModelProperty("累计提现人数")
    private Integer totalWithdrawPeopleNum;


    /**
     * 累计 待返水金额  = t_count_order表中的 SUM(waitRebateAmount)(待返水总金额)
     */
    @ApiModelProperty("累计未返水金额-实时获取")
    private BigDecimal totalWaitRebateAmount;

    /**
     * 累计 已返水金额 = t_count_order表中的 SUM(alreadyRebateAmount)(已返水总金额)
     */
    @ApiModelProperty("累计已领返水金额")
    private BigDecimal totalAlreadyRebateAmount;


    /**
     * 累计 待返佣金额 = t_count_order表中的 SUM(waitReturnCommissionAmount)(待返佣总金额-下级用户返水返给自己的金额)
     */
    @ApiModelProperty("累计未返外水金额-实时获取")
    private BigDecimal totalWaitReturnCommissionAmount;

    /**
     * 累计 已返佣金额 = t_count_order表中的 SUM(alreadyReturnCommissionAmount)(已返佣总金额-下级用户返水返给自己的金额)
     */
    @ApiModelProperty("累计领取外水金额")
    private BigDecimal totalAlreadyReturnCommissionAmount;


    /**
     * 累计 游戏投注总金额(不分输赢)  = t_count_order表中的 SUM(betAmount)(游戏投注总金额(不分输赢)-当日)
     */
    @ApiModelProperty("累计游戏投注总金额(不分输赢)")
    private BigDecimal totalBetAmount;


    /**
     * 累计 客损 =  t_count_order表中的 SUM(customerLossAmount)(客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)])
     */
    @ApiModelProperty("累计客损")
    private BigDecimal totalCustomerLossAmount;


    /**
     * 累计 充提差 = totalRechargeAmount(累计充值金额) - totalWithdrawAmount(累计提现金额);
     */
    @ApiModelProperty("累计充提差")
    private BigDecimal totalDifference;


    @Excel(name = "游戏输赢", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoseAmount;












}
