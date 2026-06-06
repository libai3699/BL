package com.common.elasticsearch.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.sort.SortOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEsAmountChange {
    /** 商户账变id */
    @ApiModelProperty("商户账变id")
    private Long id;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long userId;

    /** tg用户ID */
    @ApiModelProperty("tg用户ID")
    private Long tgUserId;

    /** 用户名称 */
    @ApiModelProperty("用户名称")
    private String username;

    /**
     * 关联的订单号
     */
    @ApiModelProperty(value="币种图标")
    private String icon;

    /**
     * currencyId
     */
    @ApiModelProperty(value = "currencyId")
    private Integer currencyId;

    /** 关联的订单号 */
    @ApiModelProperty("关联的订单号")
    private String orderNo;

    @ApiModelProperty("订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单, 4 转账订单, 5 红包发送订单, 6 红包接收订单 7充值会员订单 8 充值话费订单 9,购买靓号订单10 商户订单11 闪兑订单12 购买理财13赎回理财14购买理财审核拒绝 15购买理财审核成功16 商户提现)")
    private Integer orderType;

    /** 地址类型(1 内部地址, 2 外部地址) */
    @ApiModelProperty("地址类型(1 内部地址, 2 外部地址)")
    private Integer exchangeType;

    /** 收支类型(1 收入, 2 支出) */
    @ApiModelProperty("收支类型(1收入, 2支出)")
    private Integer accountType;

    /** 帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现, 5 金额转账, 6 红包发送, 7 红包接收, 8 提现失败) */
    @ApiModelProperty("帐变类型@ApiModelProperty(\"1 人工上下分订单 2 人工下分, 3 充值订单, 4 提现订单, 5 转账订单, 6 红包发送订单,\" +\n" +
            "            \" 7 红包接收订单 8 审核拒绝 9提现失败 10  红包退回 11,老钱包转移 12 充值会员 13 充值话费 14 充值会员回退 15充值话费回退\" +\n" +
            "            \"16购买靓号 17商户付款 18闪兑 19 购买理财  20 理财赎回 21 购买理财拒绝 22 购买理财成功 24 商户提现\")")
    private Integer type;

    /** 帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现, 5 金额转账, 6 红包发送, 7 红包接收, 8 提现失败) */
    private List<Integer> typeArr;
    /** 变更金额 */
    @ApiModelProperty("变更金额")
    private BigDecimal amount;

    /** 变更前金额 */
    @ApiModelProperty("变更前金额")
    private BigDecimal oldAmount;

    /** 变更后金额 */
    @ApiModelProperty("变更后金额")
    private BigDecimal newAmount;


    /** 备注 */
    @ApiModelProperty("备注")

    private String remark;


    /** 备注 */
    @ApiModelProperty("开始时间")

    private Date startTime;
    /** 备注 */
    @ApiModelProperty("结束时间")

    private Date endTime;

    /** 备注 */
    @ApiModelProperty("产品id")

    private String productId;


    /** 页码 */
    @ApiModelProperty("页码")
    private Integer page;

    /** 页大小 */
    @ApiModelProperty("页大小")
    private Integer size;

    /** 排序字段 */
    @ApiModelProperty("排序字段")
    private String sortField;



    /** 排序方式 */
    @ApiModelProperty("排序方式")
    private SortOrder sortOrder;

}
