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
 * 转盘活动订单对象 t_play_wheel_term
 *
 * @author axing
 * @date 2024-05-31
 */
@ApiModel("转盘活动订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_play_wheel_term")
public class PlayWheelTerm extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PlayWheelTerm";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 订单号 */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    /** 编号 */
    @ApiModelProperty("编号")
    @TableField("no")
    @Excel(name = "编号")
    private Integer no;

    /** 当前奖项权重 */
    @ApiModelProperty("当前奖项权重")
    @TableField("weight")
    @Excel(name = "当前奖项权重")
    private Integer weight;

    /** 奖励类型 0 转盘次数 1 奖金 */
    @ApiModelProperty("奖励类型 0 转盘次数 1 奖金")
    @TableField("type")
    @Excel(name = "奖励类型 0 转盘次数 1 奖金")
    private Integer type;

    /** 奖励金额 */
    @ApiModelProperty("奖励金额")
    @TableField("award")
    @Excel(name = "奖励金额")
    private BigDecimal award;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 用户飞机id */
    @ApiModelProperty("用户飞机id")
    @TableField("user_tg_id")
    @Excel(name = "用户飞机id")
    private Long userTgId;

    /** 老奖池 */
    @ApiModelProperty("老奖池")
    @TableField("old_prize_pool")
    @Excel(name = "老奖池")
    private BigDecimal oldPrizePool;

    /** 新奖池 */
    @ApiModelProperty("新奖池")
    @TableField("new_prize_pool")
    @Excel(name = "新奖池")
    private BigDecimal newPrizePool;

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
