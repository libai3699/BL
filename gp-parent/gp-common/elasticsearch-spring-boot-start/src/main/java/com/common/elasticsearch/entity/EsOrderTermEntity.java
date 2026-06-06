package com.common.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * es基础bean封装
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsOrderTermEntity extends ESBaseEntity {
    private static final Long serialVersionUID = 1L;

    @Id
    @Field(type = FieldType.Long)
    @Excel(name = "id")
    private Long id;
    /**
     * 游戏厂商编码
     */
    @Field(type = FieldType.Keyword)
    private String dealerCode;

    /**
     * 游戏厂商名称(中文)
     */
    @Field(type = FieldType.Keyword)
    @Excel(name = "游戏厂商名称")
    private String dealerName;

    /**
     * 游戏id
     */
    @Field(type = FieldType.Long)
    @Excel(name = "游戏ID")
    private Long gameId;


    /**
     * 游戏平台编码
     */
    @Excel(name = "游戏平台编码")
    @Field(type = FieldType.Keyword)
    private String plateCode;

    /**
     * 游戏平台名称(中文)
     */
    @Excel(name = "游戏平台名称(中文)")
    @Field(type = FieldType.Keyword)
    private String plateNameZh;

    /**
     * 游戏平台名称(英文)
     */
    @Excel(name = "游戏平台名称(英文)")
    @Field(type = FieldType.Keyword)
    private String plateNameEn;

    @Excel(name = "游戏厂商名称-日语")
    @Field(type = FieldType.Keyword)
    private String plateNameJa;

    @Excel(name = "游戏厂商名称-印度语")
    @Field(type = FieldType.Keyword)
    private String plateNameHi;

    @Excel(name = "游戏厂商名称(泰语)")
    @Field(type = FieldType.Keyword)
    private String plateNameTh;

    @Excel(name = "游戏厂商名称(俄语)")
    @Field(type = FieldType.Keyword)
    private String plateNameRu;

    @Excel(name = "游戏厂商名称(阿拉伯语)")
    @Field(type = FieldType.Keyword)
    private String plateNameAr;

    /**
     * 游戏厂商名称-繁体中文
     */
    @Field(type = FieldType.Keyword)
    @Excel(name = "游戏厂商名称-繁体中文")
    private String plateNameTw;
    /**
     * 游戏平台名称(韩语)
     */
    @Field(type = FieldType.Keyword)
    private String plateNameKo;

    /**
     * 游戏平台名称(葡萄牙)
     */
    @Field(type = FieldType.Keyword)
    private String plateNamePt;

    /**
     * 游戏平台名称(越南语)
     */
    @Excel(name = "游戏平台名称(越南)")
    @Field(type = FieldType.Keyword)
    private String plateNameVi;

    /**
     * 游戏平台名称(越南语)
     */
    @Field(type = FieldType.Keyword)
    private String plateNameTr;

    /**
     * 游戏编码
     */
    @Excel(name = "游戏编码")
    @Field(type = FieldType.Keyword)
    private String gameCode;

    /**
     * 游戏名称(中文)
     */
    @Excel(name = "游戏名称(中文)")
    @Field(type = FieldType.Keyword)
    private String gameNameZh;

    /**
     * 游戏名称(英文)
     */
    @Excel(name = "游戏名称(英文)")
    @Field(type = FieldType.Keyword)
    private String gameNameEn;

    /**
     * 游戏名称(韩语)
     */
    @Field(type = FieldType.Keyword)
    private String gameNameKo;

    /**
     * 游戏名称(葡萄牙)
     */
    @Field(type = FieldType.Keyword)
    private String gameNamePt;

    /**
     * 游戏名称(越南语)
     */
    @Excel(name = "游戏名称(越南语)")
    @Field(type = FieldType.Keyword)
    private String gameNameVi;

    /**
     * 游戏名称(土耳其语)
     */
    @Field(type = FieldType.Keyword)
    private String gameNameTr;

    @Field(type = FieldType.Keyword)
    @Excel(name = "游戏名称-繁体中文")
    private String gameNameTw;

    @Field(type = FieldType.Keyword)
    @Excel(name = "游戏名称-日语")
    private String gameNameJa;

    @Excel(name = "游戏名称-印度语")
    @Field(type = FieldType.Keyword)
    private String gameNameHi;

    @Excel(name = "游戏名称(泰语)")
    @Field(type = FieldType.Keyword)
    private String gameNameTh;

    @Excel(name = "游戏名称(俄语)")
    @Field(type = FieldType.Keyword)
    private String gameNameRu;

    @Field(type = FieldType.Keyword)
    @Excel(name = "游戏名称(阿拉伯语)")
    private String gameNameAr;


    /**
     * 游戏类型
     */
    @Excel(name = "游戏类型", readConverterExp = "1=电子,2=体育,3=视讯,4=彩票,5=棋牌,6=区块链,7=捕鱼,8=宾果,9=弹珠")
    @Field(type = FieldType.Keyword)
    private String gameTypeCode;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    @Field(type = FieldType.Keyword)
    private String orderNo;

    /**
     * 上游主订单号
     */
    @Excel(name = "上游主订单号")
    @Field(type = FieldType.Keyword)
    private String upPreOrderNo;

    /**
     * 上游订单号
     */
    @Excel(name = "上游订单号")
    @Field(type = FieldType.Keyword)
    private String upOrderNo;

    /**
     * 渠道id,没有则为0
     */
    @Excel(name = "渠道ID")
    @Field(type = FieldType.Long)
    private Long channelId;

    /**
     * 渠道工资
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    @Excel(name = "渠道工资")
    private BigDecimal channelRebate;

    /**
     * 渠道编码
     */
    @Field(type = FieldType.Keyword)
    @Excel(name = "渠道编码")
    private String channelCode;

    /**
     * 渠道name
     */
    @Field(type = FieldType.Keyword)
    @Excel(name = "渠道名称")
    private String channelName;

    @Excel(name = "渠道所属用户id")
    @Field(type = FieldType.Long)
    private Long channelUserId;
    /**
     * 币种id
     */
    @Field(type = FieldType.Integer)
    private Integer currencyId;

    /**
     * 币种ID
     */
    @Field(type = FieldType.Integer)
    private Integer itemId;

    /**
     * 链名称
     */
    @Field(type = FieldType.Keyword)
    private String chainTag;

    /**
     * 币加链名称
     */
    @Field(type = FieldType.Keyword)
    private String itemName;

    /**
     * 用户id
     */
    @Field(type = FieldType.Long)
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * tg用户Id
     */
    @Field(type = FieldType.Long)
    @Excel(name = "用户飞机ID")
    private Long tgUserId;

    /**
     * 投注金额
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    @Excel(name = "投注额")
    private BigDecimal betAmount;

    /**
     * 赢得金额
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    @Excel(name = "派彩额")
    private BigDecimal win;

    /**
     * 游戏盈亏(返奖 - 投注金额)
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    @Excel(name = "游戏盈亏")
    private BigDecimal winLoss;

    /**
     * 打码量
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    @Excel(name = "打码量")
    private BigDecimal codeAmount;

    /**
     * 订单状态(0 未开奖, 1 赢, 2 输, 3 和, 4 取消)
     */
    @ApiModelProperty("订单状态(0 未开奖, 1 赢, 2 输, 3 和, 4 取消)")
    @Field(type = FieldType.Integer)
    private Integer orderStatus;

    /**
     * 注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
     */
    @Field(type = FieldType.Integer)
    @Excel(name = "注单类型", readConverterExp = "0=单一注单,1=复合注单,2=复合子注单")
    private Integer orderType;

    /**
     * 结算状态(0 未结算, 1 已结算)
     */
    @Field(type = FieldType.Integer)
    @Excel(name = "结算状态", readConverterExp = "0=未结算,1=已结算")
    private Integer settleStatus;

    /**
     * 反水处理状态(0 未处理, 1 已处理)
     */
    @Field(type = FieldType.Integer)
//    @Excel(name = "反水处理状态", readConverterExp = "0=未处理,1=已处理")
    private Integer rebateStatus;
    /**
     * 下注信息
     */
    @Field(type = FieldType.Keyword)
    private String betInfo;

    /**
     * 下注结果
     */
    @Field(type = FieldType.Keyword)
    private String betResult;

    /**
     * 自己的反水
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
//    @Excel(name = "自己的反水")
    private BigDecimal rebate;

    /**
     * 上级用户ID
     */
    @Field(type = FieldType.Long)
    @Excel(name = "上级飞机用户ID")
    private Long superUserId;

    /**
     * 上级用户飞机ID
     */
    @Field(type = FieldType.Long)
    @Excel(name = "上级飞机ID")
    private Long superUserTgId;

    /**
     * 上级的返水
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
//    @Excel(name = "上级的反水")
    private BigDecimal superRebate;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Field(type = FieldType.Keyword)
    private String remark;

    /**
     * 下单时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @Excel(name = "下单时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @Transient   // Spring Data 标记，不会写入 ES
    private Long updateVersion;

    /**
     * 结算时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @Excel(name = "结算时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;

}
