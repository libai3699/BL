package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 游戏类型注单相关统计对象 t_game_type_count_order
 *
 * @author axing
 * @date 2025-06-13
 */
@ApiModel("游戏类型注单相关统计")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game_type_count_order")
public class GameTypeCountOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:GameTypeCountOrder";

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
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency_id")
    @Excel(name = "币种", cellType = Excel.ColumnType.NUMERIC)
    private Integer currencyId;

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
     * 游戏类型编码
     */
    @ApiModelProperty("游戏类型编码")
    @TableField("type_code")
    @Excel(name = "游戏类型编码")
    private String typeCode;

    /**
     * 游戏类型名称(中文)
     */
    @ApiModelProperty("游戏类型名称(中文)")
    @TableField("type_name_zh")
    @Excel(name = "游戏类型名称(中文)")
    private String typeNameZh;

    /**
     * 游戏类型名称(英文)
     */
    @ApiModelProperty("游戏类型名称(英文)")
    @TableField("type_name_en")
    @Excel(name = "游戏类型名称(英文)")
    private String typeNameEn;

    /**
     * 游戏类型名称(韩语)
     */
    @ApiModelProperty("游戏类型名称(韩语)")
    @TableField("type_name_ko")
    @Excel(name = "游戏类型名称(韩语)")
    private String typeNameKo;

    /**
     * 游戏类型名称(葡萄牙)
     */
    @ApiModelProperty("游戏类型名称(葡萄牙)")
    @TableField("type_name_pt")
    @Excel(name = "游戏类型名称(葡萄牙)")
    private String typeNamePt;

    /**
     * 游戏类型名称(越南语)
     */
    @ApiModelProperty("游戏类型名称(越南语)")
    @TableField("type_name_vi")
    @Excel(name = "游戏类型名称(越南语)")
    private String typeNameVi;

    /**
     * 游戏类型名称(土耳其语)
     */
    @ApiModelProperty("游戏类型名称(土耳其语)")
    @TableField("type_name_tr")
    @Excel(name = "游戏类型名称(土耳其语)")
    private String typeNameTr;

    /**
     * 游戏类型名称(中文繁体)
     */
    @ApiModelProperty("游戏类型名称(中文繁体)")
    @TableField("type_name_tw")
    @Excel(name = "游戏类型名称(中文繁体)")
    private String typeNameTw;

    /**
     * 游戏类型名称(日语)
     */
    @ApiModelProperty("游戏类型名称(日语)")
    @TableField("type_name_ja")
    @Excel(name = "游戏类型名称(日语)")
    private String typeNameJa;

    /**
     * 游戏类型名称(印度语)
     */
    @ApiModelProperty("游戏类型名称(印度语)")
    @TableField("type_name_hi")
    @Excel(name = "游戏类型名称(印度语)")
    private String typeNameHi;

    /**
     * 游戏类型名称(泰语)
     */
    @ApiModelProperty("游戏类型名称(泰语)")
    @TableField("type_name_th")
    @Excel(name = "游戏类型名称(泰语)")
    private String typeNameTh;

    /**
     * 游戏类型名称(俄语)
     */
    @ApiModelProperty("游戏类型名称(俄语)")
    @TableField("type_name_ru")
    @Excel(name = "游戏类型名称(俄语)")
    private String typeNameRu;

    /**
     * 游戏类型名称(阿拉伯语)
     */
    @ApiModelProperty("游戏类型名称(阿拉伯语)")
    @TableField("type_name_ar")
    @Excel(name = "游戏类型名称(阿拉伯语)")
    private String typeNameAr;

    /**
     * 用户游戏投注金额(不分输赢)-当日
     */
    @ApiModelProperty("用户游戏投注金额(不分输赢)-当日")
    @TableField("bet_amount")
    @Excel(name = "用户游戏投注金额(不分输赢)-当日", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal betAmount;

    /**
     * 游戏投注数量(不分输赢)
     */
    @ApiModelProperty("游戏投注数量(不分输赢)")
    @TableField("bet_num")
    @Excel(name = "游戏投注数量(不分输赢)", cellType = Excel.ColumnType.NUMERIC)
    private Integer betNum;

    /**
     * 游戏投注人数(不分输赢)
     */
    @ApiModelProperty("游戏投注人数(不分输赢)")
    @TableField("bet_people_num")
    @Excel(name = "游戏投注人数(不分输赢)", cellType = Excel.ColumnType.NUMERIC)
    private Integer betPeopleNum;

    /**
     * 结算金额(返奖金额)-当日
     */
    @ApiModelProperty("结算金额(返奖金额)-当日")
    @TableField("settle_amount")
    @Excel(name = "结算金额(返奖金额)-当日", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal settleAmount;

    /**
     * 输赢金额(返奖额减去投注额)-当日
     */
    @ApiModelProperty("输赢金额(返奖额减去投注额)-当日")
    @TableField("win_lose_amount")
    @Excel(name = "输赢金额(返奖额减去投注额)-当日", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoseAmount;

    /**
     * 有效投注金额(分输赢)-当日
     */
    @ApiModelProperty("有效投注金额(分输赢)-当日")
    @TableField("efficient_bet_amount")
    @Excel(name = "有效投注金额(分输赢)-当日", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal efficientBetAmount;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("code_amount")
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /**
     * 统计计算时间
     */
    @ApiModelProperty("统计计算时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("calculations_time")
    @Excel(name = "统计计算时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date calculationsTime;
    @ApiModelProperty("统计计算时间数组")
    @TableField(exist = false)
    private String[] calculationsTimes;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

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

}
