package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 语言表
 */
@ApiModel(description = "语言表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_language")
public class Language {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Language";

    /**
     * 语言id
     */
    @ApiModelProperty("语言id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "语言id")
    private Integer id;

    /**
     * 语言key
     */
    @ApiModelProperty("语言key")
    @TableField("lan_key")
    @Excel(name = "语言key")
    private String lanKey;

    /**
     * 语言名称
     */
    @ApiModelProperty("语言名称")
    @TableField("lan_name")
    @Excel(name = "语言名称")
    private String lanName;

    /**
     * 是否默认 0不是 1是
     */
    @ApiModelProperty("是否默认 0不是 1是")
    @TableField("is_default")
    @Excel(name = "是否默认 0不是 1是")
    private Integer isDefault;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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
