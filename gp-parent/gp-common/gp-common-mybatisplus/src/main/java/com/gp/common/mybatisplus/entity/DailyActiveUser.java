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
 * 每日活跃用户对象 t_daily_active_user
 *
 * @author axing
 * @date 2025-04-29
 */
@ApiModel("每日活跃用户")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_daily_active_user")
public class DailyActiveUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:DailyActiveUser";

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
     * 上级用户ID
     */
    @ApiModelProperty("上级用户ID")
    @TableField("parent_user_id")
    @Excel(name = "上级用户ID")
    private Long parentUserId;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("turnover_amount")
    @Excel(name = "打码量")
    private BigDecimal turnoverAmount;

    @ApiModelProperty("0不是新人 1 是新人")
    @TableField("is_new")
    private Integer isNew;

    /**
     * 上级 ppth
     */
    @ApiModelProperty("上级列表")
    @TableField("p_path")
    @Excel(name = "上级列表")
    private String pPath;

    /**
     * 活跃日期
     */
    @ApiModelProperty("活跃日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("active_date")
    @Excel(name = "活跃日期")
    private Date activeDate;
    @ApiModelProperty("活跃日期数组")
    @TableField(exist = false)
    private String[] activeDates;

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
