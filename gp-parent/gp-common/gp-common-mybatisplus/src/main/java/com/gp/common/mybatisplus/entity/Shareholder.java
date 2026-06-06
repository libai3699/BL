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

import java.util.Date;

/**
 * 股东对象 t_shareholder
 *
 * @author axing
 * @date 2025-05-20
 */
@ApiModel("股东")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_shareholder")
public class Shareholder extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Shareholder";

    /**
     * 股东ID
     */
    @ApiModelProperty("股东ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "股东ID")
    private Long id;

    /**
     * 股东名称
     */
    @ApiModelProperty("股东名称")
    @TableField("name")
    @Excel(name = "股东名称")
    private String name;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    @TableField(exist = false)
    @Excel(name = "用户名称")
    private String username;

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
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

}
