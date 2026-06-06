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
 * 活动任务对象 t_activity_task
 *
 * @author axing
 * @date 2024-08-08
 */
@ApiModel("活动任务")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_activity_task")
public class ActivityTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:ActivityTask";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "id",cellType= Excel.ColumnType.NUMERIC)
     private Long id;

    /** 活动id */
    @ApiModelProperty("活动id")
    @TableField("activity_id")
    @Excel(name = "活动id",cellType= Excel.ColumnType.NUMERIC)
    private Long activityId;


    /** 规则类型(1 签到次数, 2 充值额度, 3 打码量 4 特殊活动 5 救援金) */
    @ApiModelProperty("规则类型(1 签到活动, 2 充值活动, 3 打码量活动 4 特殊活动 5 渠道活动 6 救援金  7转盘活动)")
    @TableField("type")
    @Excel(name = "规则类型(1 签到活动, 2 充值活动, 3 打码量活动 4 特殊活动 5 渠道活动 6 救援金  7转盘活动))")
    private Integer type;

    /** 时间类型(1 每日, 2 永久, 3 每周 4 每月) */
    @ApiModelProperty("时间类型(1 每日, 2 永久, 3 每周 4 每月 5 第一次 6第二次)")
    @TableField("time_type")
    @Excel(name = "时间类型",readConverterExp="1 每日, 2 永久, 3 每周 4 每月5 第一次 6第二次")
    private Integer timeType;

    /** 任务额度 */
    @ApiModelProperty("任务额度")
    @TableField("task_amount")
    @Excel(name = "任务额度")
    private BigDecimal taskAmount;

    /** 状态(0 关闭, 1 开启) */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态",readConverterExp="0=关闭,1=开启")
    private Integer status;

    /** 排序 */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    @ApiModelProperty("活动任务名称")
    @TableField("task_name")
    @Excel(name = "活动任务名称")
    private String taskName;

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

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;
    /** 任务额度 */


    @ApiModelProperty("任务说明")
    @TableField(exist = false)
    private String taskExplain;

}
