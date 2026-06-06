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
 * 活动奖励领取对象 t_activity_award_receive
 *
 * @author axing
 * @date 2024-08-08
 */
@ApiModel("活动奖励领取")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_activity_award_receive")
public class ActivityAwardReceive extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:ActivityAwardReceive";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /**
     * 飞机id
     */
    @ApiModelProperty("飞机id")
    @TableField("user_tg_id")
    @Excel(name = "飞机id", cellType = Excel.ColumnType.NUMERIC)
    private Long userTgId;

    /**
     * 活动id
     */
    @ApiModelProperty("活动id")
    @TableField("activity_id")
    @Excel(name = "活动id", cellType = Excel.ColumnType.NUMERIC)
    private Long activityId;

    /**
     * 活动任务id
     */
    @ApiModelProperty("活动任务id")
    @TableField("activity_task_id")
    @Excel(name = "活动任务id", cellType = Excel.ColumnType.NUMERIC)
    private Long activityTaskId;

    @ApiModelProperty("活动任务名称")
    @TableField(exist = false)
    @Excel(name = "活动任务名称")
    private String taskName;

    /**
     * 当时金额
     */
    @ApiModelProperty("当时金额")
    @TableField("amount")
    @Excel(name = "当时金额")
    private BigDecimal amount;

    /**
     * 当时比例
     */
    @ApiModelProperty("当时比例")
    @TableField("ratio")
    @Excel(name = "当时比例")
    private BigDecimal ratio;

    /**
     * 固定金额还是比例 0 固定金额 1 比例
     */
    @ApiModelProperty("固定金额还是比例 0 固定金额 1 比例")
    @TableField("is_fixed")
    @Excel(name = "固定金额还是比例 0 固定金额 1 比例")
    private Integer isFixed;

    /**
     * 活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数)
     */
    @ApiModelProperty("活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数)")
    @TableField("type")
    @Excel(name = "活动奖励类型", readConverterExp = "1=彩金,2=转盘次数")
    private Integer type;

    /**
     * 奖励(彩金额度, 转盘次数)
     */
    @ApiModelProperty("奖励(彩金额度, 转盘次数)")
    @TableField("award")
    @Excel(name = "奖励(彩金额度, 转盘次数)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal award;

    /**
     * 领取时间
     */
    @ApiModelProperty("领取时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "领取时间")
    private Date createTime;
    @ApiModelProperty("领取时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;


}
