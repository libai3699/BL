package com.gp.maintain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 【请填写功能名称】对象 t_maintain
 *
 * @author axing
 * @date 2024-11-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintainVO
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Maintain";


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

    /** 维护结束时间 */
    @ApiModelProperty("维护结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("maintain_time_end")
    @Excel(name = "维护结束时间")
    private Date maintainTimeEnd;



}
