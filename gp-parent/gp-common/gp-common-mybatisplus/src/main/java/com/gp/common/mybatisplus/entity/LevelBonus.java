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
 * 等级奖励对象 t_level_bonus
 *
 * @author axing
 * @date 2025-07-10
 */
@ApiModel("等级奖励")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_level_bonus")
public class LevelBonus extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:LevelBonus";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long id;
    /** 订单号 */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    private String orderNo;
    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 0升级奖励 1 周奖励 2 月奖励 */
    @ApiModelProperty("0升级奖励 1 周奖励 2 月奖励")
    @TableField("type")
    @Excel(name = "0升级奖励 1 周奖励 2 月奖励")
    private Integer type;

    /** 等级 */
    @ApiModelProperty("等级")
    @TableField("level")
    @Excel(name = "等级")
    private Integer level;

    /** 奖金 */
    @ApiModelProperty("奖金")
    @TableField("bonus")
    @Excel(name = "奖金")
    private BigDecimal bonus;

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
