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
 * 游戏奖池对象 t_play_prize_pool
 *
 * @author axing
 * @date 2024-05-12
 */
@ApiModel("游戏奖池")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_play_prize_pool")
public class PlayPrizePool extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PlayPrizePool";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 奖池名称 */
    @ApiModelProperty("奖池名称")
    @TableField("prize_pool_name")
    @Excel(name = "奖池名称")
    private String prizePoolName;

    /** 奖池编码 */
    @ApiModelProperty("奖池编码")
    @TableField("prize_pool_code")
    @Excel(name = "奖池编码")
    private String prizePoolCode;

    /** 奖池被领取的额度 */
    @ApiModelProperty("奖池被领取的额度")
    @TableField("prize_pool_amount")
    @Excel(name = "奖池被领取的额度")
    private BigDecimal prizePoolAmount;

    /** 奖池二阶段赔率额度 */
    @ApiModelProperty("奖池二阶段赔率额度")
    @TableField("second_odds_amount")
    @Excel(name = "奖池二阶段赔率额度")
    private BigDecimal secondOddsAmount;

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

    /** 修改时间 */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;


}
