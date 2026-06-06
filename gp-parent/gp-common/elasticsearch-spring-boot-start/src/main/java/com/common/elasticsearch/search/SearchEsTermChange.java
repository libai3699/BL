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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchEsTermChange {
    /**
     * id
     */
    private Long id;

    /**
     * 游戏厂商编码
     */
    private String dealerCode;

    /**
     * 游戏厂商名称(中文)
     */
    private String dealerNameZh;

    /**
     * 游戏厂商名称(英文)
     */
    private String dealerNameEn;

    /**
     * 游戏厂商名称(韩语)
     */
    private String dealerNameKo;

    /**
     * 游戏厂商名称(葡萄牙)
     */
    private String dealerNamePt;

    /**
     * 游戏厂商名称(越南语)
     */
    private String dealerNameVi;

    /**
     * 游戏厂商名称(土耳其语)
     */
    private String dealerNameTr;

    /**
     * 游戏平台编码
     */
    private String plateCode;

    /**
     * 游戏平台名称(中文)
     */
    private String plateNameZh;

    /**
     * 游戏平台名称(英文)
     */
    private String plateNameEn;

    /**
     * 游戏平台名称(韩语)
     */
    private String plateNameKo;

    /**
     * 游戏平台名称(葡萄牙)
     */
    private String plateNamePt;

    /**
     * 游戏平台名称(越南语)
     */
    private String plateNameVi;

    /**
     * 游戏平台名称(越南语)
     */
    private String plateNameTr;

    /**
     * 游戏编码
     */
    private String gameCode;

    /**
     * 游戏名称(中文)
     */
    private String gameNameZh;

    /**
     * 游戏名称(英文)
     */
    private String gameNameEn;

    /**
     * 游戏名称(韩语)
     */
    private String gameNameKo;

    /**
     * 游戏名称(葡萄牙)
     */
    private String gameNamePt;

    /**
     * 游戏名称(越南语)
     */
    private String gameNameVi;

    /**
     * 游戏名称(土耳其语)
     */
    private String gameNameTr;

    /**
     * 游戏类型
     */
    private String gameTypeCode;

    /**
     * 游戏类型
     */
    private List<Integer> gameTypeCodeArr;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 上游主订单号
     */
    private String upPreOrderNo;

    /**
     * 上游订单号
     */
    private String upOrderNo;

    /**
     * 渠道id,没有则为0
     */
    private Long channelId;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道name
     */
    private String channelName;

    /**
     * 币种id
     */
    private Integer currencyId;

    /**
     * 币种ID
     */
    private Integer itemId;

    /**
     * 链名称
     */
    private String chainTag;

    /**
     * 币加链名称
     */
    private String itemName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * tg用户Id
     */
    private Long tgUserId;

    /**
     * 投注金额
     */
    private BigDecimal betAmount;

    /**
     * 赢得金额
     */
    private BigDecimal win;

    /**
     * 游戏盈亏(返奖 - 投注金额)
     */
    private BigDecimal winLoss;

    /**
     * 打码量
     */
    private BigDecimal codeAmount;

    /**
     * 订单状态(0 未开奖, 1 赢, 2 输, 3 和, 4 取消)
     */
    private Integer orderStatus;

    /**
     * 订单状态集合
     * 0 未开奖, 1 赢, 2 输, 3 和, 4 取消
     */
    private List<Integer> orderStatusList;

    /**
     * 注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
     */
    private Integer orderType;

    /**
     * 注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
     */
    private List<Integer> orderTypes;

    /**
     * 自己的反水
     */
    private BigDecimal rebate;

    /**
     * 上级用户ID
     */
    private Long superUserId;

    /**
     * 上级用户飞机ID
     */
    private Long superUserTgId;

    /**
     * 上级的返水
     */
    private BigDecimal superRebate;

    /**
     * 反水处理状态(0 未处理, 1 已处理)
     */
    private Integer rebateStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 下单时间
     */

    private Date createTime;

    /**
     * 下单时间数组
     */
    private String[] createTimes;

    /**
     * 结算时间
     */

    private Date settleTime;

    /**
     * 结算时间数组
     */
    private String[] settleTimes;

    /**
     * 结算状态(0 未结算, 1 已结算)
     */
    private Integer settleStatus;
    /**
     * 备注
     */
    @ApiModelProperty("开始时间")

    private Date startTime;
    /**
     * 备注
     */
    @ApiModelProperty("结束时间")

    private Date endTime;

    /**
     * 备注
     */
    @ApiModelProperty("产品id")

    private String productId;

    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Integer page;

    /**
     * 页大小
     */
    @ApiModelProperty("页大小")
    private Integer size;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String sortField;

    /**
     * 排序方式
     */
    @ApiModelProperty("排序方式")
    private SortOrder sortOrder;
}
