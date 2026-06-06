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
 * 级差层级对象 t_differential_level
 *
 * @author axing
 * @date 2025-04-29
 */
@ApiModel("级差层级")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_differential_level")
public class DifferentialLevel extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:DifferentialLevel";

    /** 主键id */
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id")
     private Long id;

    /** 活跃人数 */
    @ApiModelProperty("活跃人数")
    @TableField("min_active_count")
    @Excel(name = "活跃人数")
    private Integer minActiveCount;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField("code_amount")
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /** 工资比例 */
    @ApiModelProperty("工资比例")
    @TableField("salary_rate")
    @Excel(name = "工资比例")
    private BigDecimal salaryRate;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 更新人 */
    @ApiModelProperty("更新人")
    @TableField("update_by")
    @Excel(name = "更新人")
    private String updateBy;


}
