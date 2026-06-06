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
 * 活动奖励对象 t_activity_award
 *
 * @author axing
 * @date 2024-09-06
 */
@ApiModel("活动奖励")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_activity_award")
public class ActivityAward extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:ActivityAward";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.INPUT)
    @Excel(name = "id",cellType= Excel.ColumnType.NUMERIC)
     private Long id;

    @ApiModelProperty("活动名称")
    @TableField(exist = false)
    @Excel(name = "活动任务名称")
    private String activityNameZh;

    /** 活动任务id */
    @ApiModelProperty("活动任务id")
    @TableField("activity_task_id")
    @Excel(name = "活动任务id",cellType= Excel.ColumnType.NUMERIC)
    private Long activityTaskId;

    /** 任务额度 */
    @ApiModelProperty("任务额度")
    @TableField(exist = false)
    @Excel(name = "任务额度")
    private BigDecimal taskAmount;

    @ApiModelProperty("活动任务名称")
    @TableField(exist = false)
    @Excel(name = "活动任务名称")
    private String taskName;

    /** 活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数) */
    @ApiModelProperty("活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数)")
    @TableField("type")
    @Excel(name = "活动奖励类型",readConverterExp="1=彩金,2=转盘次数")
    private Integer type;

    /** 固定金额还是比例 0 固定金额 1 比例 */
    @ApiModelProperty("固定金额还是比例 0 固定金额 1 比例")
    @TableField("is_fixed")
    @Excel(name = "固定金额还是比例 0 固定金额 1 比例")
    private Integer isFixed;


    /** 提现打码量比例 */
    @ApiModelProperty("提现打码量比例")
    @TableField("withdraw_bonus_ratio")
    @Excel(name = "提现打码量比例")
    private BigDecimal withdrawBonusRatio;


    /** 按比例的比例 */
    @ApiModelProperty("按比例的比例")
    @TableField("ratio")
    @Excel(name = "按比例的比例")
    private BigDecimal ratio;

    /** 奖励(彩金额度, 转盘次数) */
    @ApiModelProperty("奖励(彩金额度, 转盘次数)")
    @TableField("award")
    @Excel(name = "奖励(彩金额度, 转盘次数)",cellType= Excel.ColumnType.NUMERIC)
    private BigDecimal award;


    /** 金额上限 */
    @ApiModelProperty("金额上限")
    @TableField("amount_cap")
    @Excel(name = "金额上限",cellType= Excel.ColumnType.NUMERIC)
    private BigDecimal amountCap;

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


}
