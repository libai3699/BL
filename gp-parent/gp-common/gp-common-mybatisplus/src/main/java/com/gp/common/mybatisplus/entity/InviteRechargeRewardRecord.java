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

@ApiModel("邀请新人充值奖励记录")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_invite_recharge_reward_record")
public class InviteRechargeRewardRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
    private Long id;

    @ApiModelProperty("邀请人userId")
    @TableField("inviter_user_id")
    @Excel(name = "邀请人userId")
    private Long inviterUserId;

    @ApiModelProperty("被邀请人userId")
    @TableField("invitee_user_id")
    @Excel(name = "被邀请人userId")
    private Long inviteeUserId;

    @ApiModelProperty("奖励类型 1=首充 2=每日充值")
    @TableField("reward_type")
    @Excel(name = "奖励类型", readConverterExp = "1=首充,2=每日充值")
    private Integer rewardType;

    @ApiModelProperty("统计归属日(每日奖励为自然日;首充为首笔达标日)")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("biz_date")
    @Excel(name = "统计日", width = 20, dateFormat = "yyyy-MM-dd")
    private Date bizDate;

    @ApiModelProperty("新人首充金额")
    @TableField("recharge_amount")
    @Excel(name = "新人首充金额")
    private BigDecimal rechargeAmount;

    @ApiModelProperty("奖励金额")
    @TableField("reward_amount")
    @Excel(name = "奖励金额")
    private BigDecimal rewardAmount;

    @ApiModelProperty("增加的提现打码量")
    @TableField("withdraw_bet_amount")
    @Excel(name = "提现打码量")
    private BigDecimal withdrawBetAmount;

    @ApiModelProperty("匹配的配置id")
    @TableField("config_id")
    @Excel(name = "配置id")
    private Long configId;

    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty("状态 0=待发放 1=已发放 2=发放失败")
    @TableField("status")
    @Excel(name = "状态", readConverterExp = "0=待发放,1=已发放,2=发放失败")
    private Integer status;

    @ApiModelProperty("已重试次数")
    @TableField("retry_count")
    @Excel(name = "重试次数")
    private Integer retryCount;

    @ApiModelProperty("失败原因")
    @TableField("fail_reason")
    @Excel(name = "失败原因")
    private String failReason;

    @ApiModelProperty("实际发放时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("grant_time")
    @Excel(name = "发放时间")
    private Date grantTime;

    @ApiModelProperty("发放时间数组")
    @TableField(exist = false)
    private String[] grantTimes;

    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;
}
