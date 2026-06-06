package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.aspect.Localized;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包雨活动配置对象 t_red_packet_rain_config
 *
 * @author axing
 * @date 2025-08-13
 */
@ApiModel("红包雨活动配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_red_packet_rain_config")
public class RedPacketRainConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:RedPacketRainConfig";

    /** 主键 */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
    private Long id;

    /** 内容-中文 */
    @ApiModelProperty("内容-中文")
    @TableField("content_zh")
    @Excel(name = "内容-中文")
    @Size(max = 255, message = "内容-中文长度不能超过255个字符")
    private String contentZh;

    /** 内容-英文 */
    @ApiModelProperty("内容-英文")
    @TableField("content_en")
    @Excel(name = "内容-英文")
    @Size(max = 255, message = "内容-英文长度不能超过255个字符")
    private String contentEn;

    /** 内容-韩语 */
    @ApiModelProperty("内容-韩语")
    @TableField("content_ko")
    @Excel(name = "内容-韩语")
    @Size(max = 255, message = "内容-韩语长度不能超过255个字符")
    private String contentKo;

    /** 内容-葡萄牙 */
    @ApiModelProperty("内容-葡萄牙")
    @TableField("content_pt")
    @Excel(name = "内容-葡萄牙")
    @Size(max = 255, message = "内容-葡萄牙长度不能超过255个字符")
    private String contentPt;

    /** 内容-越南语 */
    @ApiModelProperty("内容-越南语")
    @TableField("content_vi")
    @Excel(name = "内容-越南语")
    @Size(max = 255, message = "内容-越南语长度不能超过255个字符")
    private String contentVi;

    /** 内容-土耳其语 */
    @ApiModelProperty("内容-土耳其语")
    @TableField("content_tr")
    @Excel(name = "内容-土耳其语")
    @Size(max = 255, message = "内容-土耳其语长度不能超过255个字符")
    private String contentTr;

    /** 内容-繁体中文 */
    @ApiModelProperty("内容-繁体中文")
    @TableField("content_tw")
    @Excel(name = "内容-繁体中文")
    @Size(max = 255, message = "内容-繁体中文长度不能超过255个字符")
    private String contentTw;

    /** 内容-日语 */
    @ApiModelProperty("内容-日语")
    @TableField("content_ja")
    @Excel(name = "内容-日语")
    @Size(max = 255, message = "内容-日语长度不能超过255个字符")
    private String contentJa;

    /** 内容-印度语 */
    @ApiModelProperty("内容-印度语")
    @TableField("content_hi")
    @Excel(name = "内容-印度语")
    @Size(max = 255, message = "内容-印度语长度不能超过255个字符")
    private String contentHi;

    /** 内容-泰语 */
    @ApiModelProperty("内容-泰语")
    @TableField("content_th")
    @Excel(name = "内容-泰语")
    @Size(max = 255, message = "内容-泰语长度不能超过255个字符")
    private String contentTh;

    /** 内容-俄语 */
    @ApiModelProperty("内容-俄语")
    @TableField("content_ru")
    @Excel(name = "内容-俄语")
    @Size(max = 255, message = "内容-俄语长度不能超过255个字符")
    private String contentRu;

    /** 内容-阿拉伯语 */
    @ApiModelProperty("内容-阿拉伯语")
    @TableField("content_ar")
    @Excel(name = "内容-阿拉伯语")
    @Size(max = 255, message = "内容-阿拉伯语长度不能超过255个字符")
    private String contentAr;

    /** 开始时间 */
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    @Excel(name = "开始时间")
    private Date startTime;
    @ApiModelProperty("开始时间数组")
    @TableField(exist = false)
    private String[] startTimes;

    /** 等级要求达到多少 */
    @ApiModelProperty("等级要求达到多少")
    @TableField("level")
    @Excel(name = "等级要求达到多少")
    @Max(value = 999999999, message = "等级不能大于9位数字")
    private Integer level;

    /** 游戏类型code */
    @ApiModelProperty("游戏类型code")
    @TableField("type_code_bet")
    @Excel(name = "游戏类型code")
    private String typeCodeBet;

    /** 每日打码量多少 */
    @ApiModelProperty("每日打码量多少")
    @TableField("day_bet_amount")
    @Excel(name = "每日打码量多少")
    @Digits(integer = 20, fraction = 8, message = "每日打码量最多支持20位整数和8位小数")
    private BigDecimal dayBetAmount;

    /** 充值类型 1 每日 2 永久 3 周 4 月 */
    @ApiModelProperty("充值类型 1 每日 2 永久 3 周 4 月")
    @TableField("recharge_type")
    @Excel(name = "充值类型 1 每日 2 永久 3 周 4 月")
    private Integer rechargeType;

    /** 最小充值金额 */
    @ApiModelProperty("最小充值金额")
    @TableField("min_recharge_amount")
    @Excel(name = "最小充值金额")
    @Digits(integer = 20, fraction = 8, message = "最小充值金额最多支持20位整数和8位小数")
    private BigDecimal minRechargeAmount;

    /** 提现打码量比例 */
    @ApiModelProperty("提现打码量比例")
    @TableField("withdraw_bonus_ratio")
    @Excel(name = "提现打码量比例")
    @Digits(integer = 20, fraction = 8, message = "提现打码量比例最多支持20位整数和8位小数")
    private BigDecimal withdrawBonusRatio;

    /** 结束时间 */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    @Excel(name = "结束时间")
    private Date endTime;
    @ApiModelProperty("结束时间数组")
    @TableField(exist = false)
    private String[] endTimes;

    /** 持续时间（秒） */
    @ApiModelProperty("持续时间（秒）")
    @TableField("duration")
    @Excel(name = "持续时间（秒）")
    @Max(value = 999999999, message = "持续时间不能大于9位数字")
    private Integer duration;

    /** 可中奖红包总数 */
    @ApiModelProperty("可中奖红包总数")
    @TableField("total_count")
    @Excel(name = "可中奖红包总数")
    @Max(value = 999999999, message = "可中奖红包总数不能大于9位数字")
    private Integer totalCount;

    /** 单个红包最小金额 */
    @ApiModelProperty("单个红包最小金额")
    @TableField("min_amount")
    @Excel(name = "单个红包最小金额")
    @Digits(integer = 20, fraction = 8, message = "单个红包最小金额最多支持20位整数和8位小数")
    private BigDecimal minAmount;

    /** 单个红包最大金额 */
    @ApiModelProperty("单个红包最大金额")
    @TableField("max_amount")
    @Excel(name = "单个红包最大金额")
    @Digits(integer = 20, fraction = 8, message = "单个红包最大金额最多支持20位整数和8位小数")
    private BigDecimal maxAmount;

    /** 红包雨总金额上限 */
    @ApiModelProperty("红包雨总金额上限，0或null表示不限制")
    @TableField("total_amount")
    @Excel(name = "红包雨总金额上限")
    @Digits(integer = 20, fraction = 2, message = "总金额上限最多支持20位整数和2位小数")
    private BigDecimal totalAmount;

    /** 状态 0=关闭 1=开启 */
    @ApiModelProperty("状态 0=关闭 1=开启")
    @TableField("status")
    @Excel(name = "状态 0=关闭 1=开启")
    private Integer status;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;
    @Localized("content")
    @TableField(exist = false)
    public String content;

}
