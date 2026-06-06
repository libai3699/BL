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
 * 对象 t_pay_log
 *
 * @author axing
 * @date 2026-01-09
 */
@ApiModel("t_pay_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_pay_log")
public class PayLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayLog";

    /** 主键 */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键")
     private Long id;

    /** 会员编号 */
    @ApiModelProperty("会员编号")
    @TableField("member_id")
    @Excel(name = "会员编号")
    private Long memberId;

    /** 会员账号 */
    @ApiModelProperty("会员账号")
    @TableField("member_account")
    @Excel(name = "会员账号")
    private String memberAccount;

    /** 支付平台编号 */
    @ApiModelProperty("支付平台编号")
    @TableField("platform_id")
    @Excel(name = "支付平台编号")
    private String platformId;

    /** 支付平台名称 */
    @ApiModelProperty("支付平台名称")
    @TableField("platform_name")
    @Excel(name = "支付平台名称")
    private String platformName;

    /** 支付通道编号 */
    @ApiModelProperty("支付通道编号")
    @TableField("channel_id")
    @Excel(name = "支付通道编号")
    private String channelId;

    /** 支付通道名称 */
    @ApiModelProperty("支付通道名称")
    @TableField("channel_name")
    @Excel(name = "支付通道名称")
    private String channelName;

    /** 下单金额 */
    @ApiModelProperty("下单金额")
    @TableField("money")
    @Excel(name = "下单金额")
    private BigDecimal money;

    /** 是否下单成功 1成功 0 失败 */
    @ApiModelProperty("是否下单成功 1成功 0 失败")
    @TableField("success")
    @Excel(name = "是否下单成功 1成功 0 失败")
    private Boolean success;

    /** 失败原因 */
    @ApiModelProperty("失败原因")
    @TableField("fail_reason")
    @Excel(name = "失败原因")
    private String failReason;

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


}
