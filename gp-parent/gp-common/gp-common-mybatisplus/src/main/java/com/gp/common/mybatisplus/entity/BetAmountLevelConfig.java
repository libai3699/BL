package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 下注金额和级别配置对象 t_bet_amount_level_config
 *
 * @author axing
 * @date 2025-09-18
 */
@ApiModel("下注金额和级别配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bet_amount_level_config")
public class BetAmountLevelConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:BetAmountLevelConfig";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 级别code
     */
    @ApiModelProperty("级别code")
    @TableField("level_code")
    @Excel(name = "级别", readConverterExp = "1=一级,2=二级,3=三级,4=四级,5=五级,6=六级,7=七级,8=八级,9=九级,10=十级")
    private Integer levelCode;

    /**
     * 级别名称
     */
    @ApiModelProperty("级别名称")
    @TableField("level_name")
    @Excel(name = "级别名称")
    private String levelName;

    /**
     * 打码量最小值
     */
    @ApiModelProperty("打码量最小值")
    @TableField("bet_amount_min")
    @Excel(name = "打码量最小值")
    private BigDecimal betAmountMin;

    /**
     * 打码量最大值
     */
    @ApiModelProperty("打码量最大值")
    @TableField("bet_amount_max")
    @Excel(name = "打码量最大值")
    private BigDecimal betAmountMax;
    /**
     * 升级奖励
     */
    @ApiModelProperty("升级奖励")
    @TableField("up_bonus")
    @Excel(name = "升级奖励")
    private BigDecimal upBonus;

    /**
     * 周奖励
     */
    @ApiModelProperty("周奖励")
    @TableField("week_bonus")
    @Excel(name = "周奖励")
    private BigDecimal weekBonus;

    /**
     * 月奖励
     */
    @ApiModelProperty("月奖励")
    @TableField("month_bonus")
    @Excel(name = "月奖励")
    private BigDecimal monthBonus;
    /**
     * 本周打码量大于
     */
    @ApiModelProperty("本周打码量大于")
    @TableField("week_code_amount_gt")
    @Excel(name = "本周打码量大于")
    private BigDecimal weekCodeAmountGt;

    /**
     * 本月打码量大于
     */
    @ApiModelProperty("本月打码量大于")
    @TableField("month_code_amount_gt")
    @Excel(name = "本月打码量大于")
    private BigDecimal monthCodeAmountGt;

    /** 升级提现打码量比例 */
    @ApiModelProperty("升级提现打码量比例")
    @TableField("up_withdraw_bonus_ratio")
    @Excel(name = "升级提现打码量比例")
    private BigDecimal upWithdrawBonusRatio;

    /** 周提现打码量比例 */
    @ApiModelProperty("周提现打码量比例")
    @TableField("week_withdraw_bonus_ratio")
    @Excel(name = "周提现打码量比例")
    private BigDecimal weekWithdrawBonusRatio;

    /** 月提现打码量比例 */
    @ApiModelProperty("月提现打码量比例")
    @TableField("month_withdraw_bonus_ratio")
    @Excel(name = "月提现打码量比例")
    private BigDecimal monthWithdrawBonusRatio;

    /** 修改时间 */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

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

}
