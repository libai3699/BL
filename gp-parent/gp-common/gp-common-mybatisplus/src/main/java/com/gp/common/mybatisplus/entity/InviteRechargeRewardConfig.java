package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;

@ApiModel("邀请新人充值奖励配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_invite_recharge_reward_config")
public class InviteRechargeRewardConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
    private Long id;

    @ApiModelProperty("奖励类型 1=首充 2=每日充值")
    @TableField("reward_type")
    @Excel(name = "奖励类型", readConverterExp = "1=首充,2=每日充值")
    private Integer rewardType;

    @ApiModelProperty("新人首充最低金额")
    @TableField("min_recharge_amount")
    @Excel(name = "新人首充最低金额")
    @Digits(integer = 20, fraction = 2, message = "新人首充最低金额最多支持20位整数和2位小数")
    private BigDecimal minRechargeAmount;

    @ApiModelProperty("奖励给邀请人的金额")
    @TableField("reward_amount")
    @Excel(name = "奖励金额")
    @Digits(integer = 20, fraction = 2, message = "奖励金额最多支持20位整数和2位小数")
    private BigDecimal rewardAmount;

    @ApiModelProperty("提现打码量比例")
    @TableField("withdraw_bonus_ratio")
    @Excel(name = "提现打码量比例")
    @Digits(integer = 10, fraction = 2, message = "提现打码量比例最多支持10位整数和2位小数")
    private BigDecimal withdrawBonusRatio;

    @ApiModelProperty("状态 0=关闭 1=开启")
    @TableField("status")
    @Excel(name = "状态", readConverterExp = "0=关闭,1=开启")
    private Integer status;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;

    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;
}
