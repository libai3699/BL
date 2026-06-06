package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.aspect.Localized;
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
 * 用户投注对象 t_order_term
 *
 * @author axing
 * @date 2024-06-13
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户投注")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_term")
public class OrderTerm extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderTerm";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 游戏厂商编码
     */
    @ApiModelProperty("游戏厂商编码")
    @TableField("dealer_code")
    private String dealerCode;

    /**
     * 游戏厂商名称(中文)
     */
    @ApiModelProperty("游戏厂商名称")
    @TableField("dealer_name")
    @Excel(name = "游戏厂商名称(中文)")
    private String dealerName;

    /**
     * 游戏id
     */
    @ApiModelProperty("游戏id")
    @TableField("game_id")
    @Excel(name = "游戏ID")
    private Long gameId;

    /**
     * 游戏平台编码
     */
    @ApiModelProperty("游戏平台编码")
    @TableField("plate_code")
    @Excel(name = "游戏平台编码")
    private String plateCode;

    /**
     * 游戏平台名称(中文)
     */
    @ApiModelProperty("游戏平台名称(中文)")
    @TableField("plate_name_zh")
    @Excel(name = "游戏平台名称(中文)")
    private String plateNameZh;

    /**
     * 游戏平台名称(英文)
     */
    @ApiModelProperty("游戏平台名称(英文)")
    @TableField("plate_name_en")
    @Excel(name = "游戏平台名称(英文)")
    private String plateNameEn;

    /**
     * 游戏平台名称(韩语)
     */
    @ApiModelProperty("游戏平台名称(韩语)")
    @TableField("plate_name_ko")
    @Excel(name = "游戏平台名称(韩语)")
    private String plateNameKo;

    /**
     * 游戏平台名称(葡萄牙)
     */
    @ApiModelProperty("游戏平台名称(葡萄牙)")
    @TableField("plate_name_pt")
    @Excel(name = "游戏平台名称(葡萄牙)")
    private String plateNamePt;

    /**
     * 游戏平台名称(越南语)
     */
    @ApiModelProperty("游戏平台名称(越南语)")
    @TableField("plate_name_vi")
    @Excel(name = "游戏平台名称(越南语)")
    private String plateNameVi;

    /**
     * 游戏平台名称(土耳其语)
     */
    @ApiModelProperty("游戏平台名称(土耳其语)")
    @TableField("plate_name_tr")
    @Excel(name = "游戏平台名称(土耳其语)")
    private String plateNameTr;

    /**
     * 游戏厂商名称-繁体中文
     */
    @ApiModelProperty("游戏厂商名称-繁体中文")
    @TableField("plate_name_tw")
    @Excel(name = "游戏厂商名称-繁体中文")
    private String plateNameTw;

    /**
     * 游戏厂商名称-日语
     */
    @ApiModelProperty("游戏厂商名称-日语")
    @TableField("plate_name_ja")
    @Excel(name = "游戏厂商名称-日语")
    private String plateNameJa;

    /**
     * 游戏厂商名称-印度语
     */
    @ApiModelProperty("游戏厂商名称-印度语")
    @TableField("plate_name_hi")
    @Excel(name = "游戏厂商名称-印度语")
    private String plateNameHi;

    /**
     * 游戏厂商名称(泰语)
     */
    @ApiModelProperty("游戏厂商名称(泰语)")
    @TableField("plate_name_th")
    @Excel(name = "游戏厂商名称(泰语)")
    private String plateNameTh;

    /**
     * 游戏厂商名称(俄语)
     */
    @ApiModelProperty("游戏厂商名称(俄语)")
    @TableField("plate_name_ru")
    @Excel(name = "游戏厂商名称(俄语)")
    private String plateNameRu;

    /**
     * 游戏厂商名称(阿拉伯语)
     */
    @ApiModelProperty("游戏厂商名称(阿拉伯语)")
    @TableField("plate_name_ar")
    @Excel(name = "游戏厂商名称(阿拉伯语)")
    private String plateNameAr;

    /**
     * 游戏编码
     */
    @ApiModelProperty("游戏编码")
    @TableField("game_code")
    @Excel(name = "游戏编码")
    private String gameCode;

    /**
     * 游戏名称(中文)
     */
    @ApiModelProperty("游戏名称(中文)")
    @TableField("game_name_zh")
    @Excel(name = "游戏名称(中文)")
    private String gameNameZh;

    /**
     * 游戏名称(英文)
     */
    @ApiModelProperty("游戏名称(英文)")
    @TableField("game_name_en")
    @Excel(name = "游戏名称(英文)")
    private String gameNameEn;

    /**
     * 游戏名称(韩语)
     */
    @ApiModelProperty("游戏名称(韩语)")
    @TableField("game_name_ko")
    @Excel(name = "游戏名称(韩语)")
    private String gameNameKo;

    /**
     * 游戏名称(葡萄牙)
     */
    @ApiModelProperty("游戏名称(葡萄牙)")
    @TableField("game_name_pt")
    @Excel(name = "游戏名称(葡萄牙)")
    private String gameNamePt;

    /**
     * 游戏名称(越南语)
     */
    @ApiModelProperty("游戏名称(越南语)")
    @TableField("game_name_vi")
    @Excel(name = "游戏名称(越南语)")
    private String gameNameVi;

    /**
     * 游戏名称(土耳其语)
     */
    @ApiModelProperty("游戏名称(土耳其语)")
    @TableField("game_name_tr")
    @Excel(name = "游戏名称(土耳其语)")
    private String gameNameTr;

    /**
     * 游戏名称-繁体中文
     */
    @ApiModelProperty("游戏名称-繁体中文")
    @TableField("game_name_tw")
    @Excel(name = "游戏名称-繁体中文")
    private String gameNameTw;

    /**
     * 游戏名称-日语
     */
    @ApiModelProperty("游戏名称-日语")
    @TableField("game_name_ja")
    @Excel(name = "游戏名称-日语")
    private String gameNameJa;

    /**
     * 游戏名称-印度语
     */
    @ApiModelProperty("游戏名称-印度语")
    @TableField("game_name_hi")
    @Excel(name = "游戏名称-印度语")
    private String gameNameHi;

    /**
     * 游戏名称(泰语)
     */
    @ApiModelProperty("游戏名称(泰语)")
    @TableField("game_name_th")
    @Excel(name = "游戏名称(泰语)")
    private String gameNameTh;

    /**
     * 游戏名称(俄语)
     */
    @ApiModelProperty("游戏名称(俄语)")
    @TableField("game_name_ru")
    @Excel(name = "游戏名称(俄语)")
    private String gameNameRu;

    /**
     * 游戏名称(阿拉伯语)
     */
    @ApiModelProperty("游戏名称(阿拉伯语)")
    @TableField("game_name_ar")
    @Excel(name = "游戏名称(阿拉伯语)")
    private String gameNameAr;

    /**
     * 游戏类型
     */
    @ApiModelProperty("游戏类型")
    @TableField("game_type_code")
    @Excel(name = "游戏类型")
    private String gameTypeCode;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    /**
     * 上游订单号
     */
    @ApiModelProperty("上游订单号")
    @TableField("up_order_no")
    @Excel(name = "上游订单号")
    private String upOrderNo;

    /**
     * 上游主注单号
     */
    @ApiModelProperty("上游主注单号")
    @TableField("up_pre_order_no")
    @Excel(name = "上游主注单号")
    private String upPreOrderNo;
    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    @TableField("channel_id")
    @Excel(name = "渠道id,没有则为0")
    private Long channelId;

    /**
     * 渠道编码
     */
    @ApiModelProperty("渠道编码")
    @TableField("channel_code")
    @Excel(name = "渠道编码")
    private String channelCode;

    /**
     * 渠道name
     */
    @ApiModelProperty("渠道name")
    @TableField("channel_name")
    @Excel(name = "渠道name")
    private String channelName;

    /**
     * 渠道 所属用户id
     */
    @ApiModelProperty("渠道所属用户id")
    @TableField("channel_user_id")
    @Excel(name = "渠道所属用户id")
    private Long channelUserId;

    /**
     * 渠道工资
     */
    @ApiModelProperty("渠道工资")
    @TableField("channel_rebate")
    @Excel(name = "渠道工资")
    private BigDecimal channelRebate;
    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    @TableField("currency_id")
    private Integer currencyId;

    /**
     * 币种ID
     */
    @ApiModelProperty("币种ID")
    @TableField("item_id")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    private String chainTag;

    /**
     * 币加链名称
     */
    @ApiModelProperty("币加链名称")
    @TableField("item_name")
    private String itemName;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /**
     * tg用户Id
     */
    @ApiModelProperty("tg用户Id")
    @TableField("tg_user_id")
    @Excel(name = "tg用户Id", cellType = Excel.ColumnType.NUMERIC)
    private Long tgUserId;

    /**
     * 投注金额
     */
    @ApiModelProperty("投注金额")
    @TableField("bet_amount")
    @Excel(name = "投注金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal betAmount;

    /**
     * 赢得金额
     */
    @ApiModelProperty("赢得金额")
    @TableField("win")
    @Excel(name = "赢得金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal win;

    /**
     * 游戏盈亏(返奖 - 投注金额)
     */
    @ApiModelProperty("游戏盈亏(返奖 - 投注金额)")
    @TableField("win_loss")
    @Excel(name = "游戏盈亏", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoss;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("code_amount")
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /**
     * 订单状态(0 未开奖, 1 赢, 2 输, 3 和, 4 取消)
     */
    @ApiModelProperty("订单状态(0 未开奖, 1 赢, 2 输, 3 和, 4 取消)")
    @TableField("order_status")
    @Excel(name = "订单状态", readConverterExp = "0=未开奖,1=赢,2=输,3=和,4=取消")
    private Integer orderStatus;

    /**
     * 注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
     */
    @ApiModelProperty("注单类型(0 单一注单, 1 复合注单, 2 复合子注单)")
    @TableField("order_type")
    @Excel(name = "注单类型", readConverterExp = "0=单一注单,1=复合注单,2=复合子注单")
    private Integer orderType;

    /**
     * 下注信息
     */
    @ApiModelProperty("下注信息")
    @TableField("bet_info")
    @Excel(name = "下注信息")
    private String betInfo;

    /**
     * 下注结果
     */
    @ApiModelProperty("下注结果")
    @TableField("bet_result")
    @Excel(name = "下注结果")
    private String betResult;

    /**
     * 自己的反水
     */
    @ApiModelProperty("自己的反水")
    @TableField("rebate")
    @Excel(name = "自己的反水", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rebate;

    /**
     * 上级用户ID
     */
    @ApiModelProperty("上级用户ID")
    @TableField("super_user_id")
    @Excel(name = "上级用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long superUserId;

    /**
     * 上级用户飞机ID
     */
    @ApiModelProperty("上级用户飞机ID")
    @TableField("super_user_tg_id")
    @Excel(name = "上级用户飞机ID", cellType = Excel.ColumnType.NUMERIC)
    private Long superUserTgId;

    /**
     * 上级的返水
     */
    @ApiModelProperty("上级的返水")
    @TableField("super_rebate")
    @Excel(name = "上级的返水", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superRebate;

    /**
     * 反水处理状态(0 未处理, 1 已处理)
     */
    @ApiModelProperty("反水处理状态(0 未处理, 1 已处理)")
    @TableField("rebate_status")
    @Excel(name = "反水处理状态", readConverterExp = "0=未处理,1=已处理")
    private Integer rebateStatus;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "下单时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("下单时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 结算时间
     */
    @ApiModelProperty("结算时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("settle_time")
    @Excel(name = "结算时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;
    @ApiModelProperty("结算时间数组")
    @TableField(exist = false)
    private String[] settleTimes;

    /**
     * 结算状态(0 未结算, 1 已结算)
     */
    @ApiModelProperty("结算状态(0 未结算, 1 已结算)")
    @TableField("settle_status")
    @Excel(name = "结算状态", readConverterExp = "0=未结算,1=已结算")
    private Integer settleStatus;

    @Localized("plateName")
    @TableField(exist = false)
    private String plateName;
    @Localized("gameName")
    @TableField(exist = false)
    private String gameName;

    @TableField(exist = false)
    private String typeCode;
    /**
     * 是否是查询当天数据 0.当天 1.非当天
     */
    @ApiModelProperty("是否是查询当天数据 0.当天 1.非当天")
    @TableField(exist = false)
    private Integer todayFag = 0;

    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @TableField(exist = false)
    private Long updateVersion;

    /**
     * 注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
     */
    @ApiModelProperty("注单类型(0 单一注单, 1 复合注单, 2 复合子注单)")
    @TableField(exist = false)
    private List<Integer> orderTypes;

    /**
     * 用户IdList
     */
    @TableField(exist = false)
    private List<Long> userIdList;
}
