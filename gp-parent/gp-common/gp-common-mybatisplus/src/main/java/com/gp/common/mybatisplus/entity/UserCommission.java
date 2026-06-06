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
 * 用户佣金对象 t_user_commission
 *
 * @author axing
 * @date 2025-04-30
 */
@ApiModel("用户佣金")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_commission")
public class UserCommission extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserCommission";

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    /**
     * 日期
     */
    @ApiModelProperty("日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("stat_date")
    @Excel(name = "日期")
    private Date statDate;
    @ApiModelProperty("日期数组")
    @TableField(exist = false)
    private String[] statDates;

    /**
     * 佣金金额
     */
    @ApiModelProperty("佣金金额")
    @TableField("commission_amount")
    @Excel(name = "佣金金额")
    private BigDecimal commissionAmount;

    /**
     * 工资比例
     */
    @ApiModelProperty("工资比例")
    @TableField("salary_ratio")
    @Excel(name = "工资比例")
    private BigDecimal salaryRatio;

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
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

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
     * 更新人
     */
    @ApiModelProperty("更新人")
    @TableField("update_by")
    @Excel(name = "更新人")
    private String updateBy;

}
