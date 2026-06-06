package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;

/**
 * 【请填写功能名称】对象 t_pay_agent_log
 *
 * @author axing
 * @date 2026-01-09
 */
@ApiModel("【请填写功能名称】")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_pay_agent_log")
public class PayAgentLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayAgentLog";

    /** 主键 */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
     private Integer id;

    /** 提现订单号 */
    @ApiModelProperty("提现订单号")
    @TableField("withdraw_order_no")
    @Excel(name = "提现订单号")
    private String withdrawOrderNo;

    /** 三方代付订单号 */
    @ApiModelProperty("三方代付订单号")
    @TableField("pay_agent_order_no")
    @Excel(name = "三方代付订单号")
    private String payAgentOrderNo;

    /** 三方代付平台ID */
    @ApiModelProperty("三方代付平台ID")
    @TableField("pay_agent_plat_id")
    @Excel(name = "三方代付平台ID")
    private String payAgentPlatId;

    /** 三方代付平台名称 */
    @ApiModelProperty("三方代付平台名称")
    @TableField("pay_agent_plat_name")
    @Excel(name = "三方代付平台名称")
    private String payAgentPlatName;

    /** 会员ID */
    @ApiModelProperty("会员ID")
    @TableField("member_id")
    @Excel(name = "会员ID")
    private String memberId;

    /** 会员账号 */
    @ApiModelProperty("会员账号")
    @TableField("member_account")
    @Excel(name = "会员账号")
    private String memberAccount;

    /** 提现金额 */
    @ApiModelProperty("提现金额")
    @TableField("withdraw_money")
    @Excel(name = "提现金额")
    private BigDecimal withdrawMoney;

    /** 提交时间 */
    @ApiModelProperty("提交时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "提交时间")
    private Date createTime;
    @ApiModelProperty("提交时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 回调时间 */
    @ApiModelProperty("回调时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("callback_time")
    @Excel(name = "回调时间")
    private Date callbackTime;
    @ApiModelProperty("回调时间数组")
    @TableField(exist = false)
    private String[] callbackTimes;

    /** 回调状态 0 回调中 1 成功 2失败 */
    @ApiModelProperty("回调状态 0 回调中 1 成功 2失败")
    @TableField("callback_status")
    @Excel(name = "回调状态 0 回调中 1 成功 2失败")
    private Integer callbackStatus;


}
