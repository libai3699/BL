package com.gp.common.mybatisplus.entity;

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
 * 【请填写功能名称】对象 t_maintain
 *
 * @author axing
 * @date 2024-11-13
 */
@ApiModel("")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_maintain")
public class Maintain extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Maintain";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    @TableField("product_name")
    @Excel(name = "产品名称")
    private String productName;

    /** 产品编码 */
    @ApiModelProperty("产品编码")
    @TableField("product_code")
    @Excel(name = "产品编码")
    private String productCode;

    /** 维护状态(0 无维护, 1 维护中) */
    @ApiModelProperty("维护状态(0 无维护, 1 维护中)")
    @TableField("status")
    @Excel(name = "维护状态(0 无维护, 1 维护中)")
    private Integer status;

    /** 维护标题 */
    @ApiModelProperty("维护标题")
    @TableField("maintain_title")
    @Excel(name = "维护标题")
    private String maintainTitle;

    /** 维护内容 */
    @ApiModelProperty("维护内容")
    @TableField("maintain_content")
    @Excel(name = "维护内容")
    private String maintainContent;

    /** 维护开始时间 */
    @ApiModelProperty("维护开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("maintain_time_start")
    @Excel(name = "维护开始时间")
    private Date maintainTimeStart;
    @ApiModelProperty("维护开始时间数组")
    @TableField(exist = false)
    private String[] maintainTimeStarts;

    /** 维护结束时间 */
    @ApiModelProperty("维护结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("maintain_time_end")
    @Excel(name = "维护结束时间")
    private Date maintainTimeEnd;
    @ApiModelProperty("维护结束时间数组")
    @TableField(exist = false)
    private String[] maintainTimeEnds;


}
