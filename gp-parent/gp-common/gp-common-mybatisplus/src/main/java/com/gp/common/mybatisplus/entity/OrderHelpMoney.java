package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;

/**
 * 用户救济金申领对象 t_order_help_money
 *
 * @author axing
 * @date 2024-09-05
 */
@ApiModel("用户救济金申领")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_help_money")
public class OrderHelpMoney extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderHelpMoney";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** tg用户Id */
    @ApiModelProperty("tg用户Id")
    @TableField("tg_user_id")
    @Excel(name = "tg用户Id")
    private Long tgUserId;

    /** 奖励id */
    @ApiModelProperty("奖励id")
    @TableField("award_id")
    @Excel(name = "奖励id")
    private Long awardId;

    /** 日期(年月日) */
    @ApiModelProperty("日期(年月日)")
    @TableField("day_str")
    @Excel(name = "日期(年月日)")
    private String dayStr;

    /** 游戏类型 0代表 是彩金赠送 */
    @ApiModelProperty("游戏类型 0代表 是彩金赠送")
    @TableField("game_type_code")
    @Excel(name = "游戏类型 -1 代表通用吧 只能这样做 0代表 是彩金赠送")
    private String gameTypeCode;

    /** 彩金金额 */
    @ApiModelProperty("彩金金额")
    @TableField("bonus_money")
    @Excel(name = "彩金金额")
    private BigDecimal bonusMoney;

    /** 0未领取 1 领取 */
    @ApiModelProperty("0未领取 1 领取")
    @TableField("bet_money")
    @Excel(name = "0未领取 1 领取")
    private BigDecimal betMoney;

    /** 中奖金额 */
    @ApiModelProperty("中奖金额")
    @TableField("win_money")
    @Excel(name = "中奖金额")
    private BigDecimal winMoney;

    /** 计算金额 投注金额减去中奖金额 */
    @ApiModelProperty("计算金额 投注金额减去中奖金额")
    @TableField("cal_money")
    @Excel(name = "计算金额 投注金额减去中奖金额")
    private BigDecimal calMoney;

    /** 计算金额比例 */
    @ApiModelProperty("计算金额比例")
    @TableField("cal_ratio")
    @Excel(name = "计算金额比例")
    private BigDecimal calRatio;

    /** 计算出的救济金金额 */
    @ApiModelProperty("计算出的救济金金额")
    @TableField("receive_money")
    @Excel(name = "计算出的救济金金额")
    private BigDecimal receiveMoney;

    /** 是否领取 */
    @ApiModelProperty("是否领取")
    @TableField("is_receive")
    @Excel(name = "是否领取")
    private Integer isReceive;

    /** 签名时间 */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间")
    private Long signTime;

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

    /** 签名 */
    @ApiModelProperty("签名")
    @TableField("sign")
    @Excel(name = "签名")
    private String sign;


}
