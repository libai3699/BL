package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 私人红包配置对象 t_private_red_packet_config
 *
 * @author axing
 * @date 2025-08-15
 */
@ApiModel("私人红包配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_private_red_packet_config")
public class PrivateRedPacketConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PrivateRedPacketConfig";

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
    private Long id;

    /**
     * 等级要求达到多少
     */
    @ApiModelProperty("等级要求达到多少")
    @TableField("level")
    @Excel(name = "等级要求达到多少")
    private Integer level;

    /**
     * 每日打码量多少
     */
    @ApiModelProperty("每日打码量多少")
    @TableField("day_bet_amount")
    @Excel(name = "每日打码量多少")
    private BigDecimal dayBetAmount;

    /**
     * 提现打码量比例
     */
    @ApiModelProperty("提现打码量比例")
    @TableField("withdraw_bonus_ratio")
    @Excel(name = "提现打码量比例")
    private BigDecimal withdrawBonusRatio;

    /**
     * 指定的用户id
     */
    @ApiModelProperty("指定的用户id")
    @TableField("target_user_ids")
    @Excel(name = "指定的用户id")
    private String targetUserIds;

    /**
     * 指定的用户名称
     */
    @ApiModelProperty("指定的用户名称")
    @TableField(exist = false)
    @Excel(name = "指定的用户名称")
    private String usernames;

    /**
     * 最小充值金额
     */
    @ApiModelProperty("最小充值金额")
    @TableField("min_recharge_amount")
    @Excel(name = "最小充值金额")
    private BigDecimal minRechargeAmount;

    /**
     * 状态 0=关闭 1=开启
     */
    @ApiModelProperty("状态 0=关闭 1=开启")
    @TableField("status")
    @Excel(name = "状态 0=关闭 1=开启")
    private Integer status;

    /**
     * 配置json
     */
    @ApiModelProperty("配置json")
    @TableField("json")
    @Excel(name = "配置json")
    private String json;

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

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /** 游戏类型code */
    @ApiModelProperty("游戏类型code")
    @TableField("type_code_bet")
    @Excel(name = "游戏类型code")
    private String typeCodeBet;

    /** 充值类型 1 每日 2 永久 3 周 4 月 */
    @ApiModelProperty("充值类型 1 每日 2 永久 3 周 4 月")
    @TableField("recharge_type")
    @Excel(name = "充值类型 1 每日 2 永久 3 周 4 月")
    private Integer rechargeType;


}
