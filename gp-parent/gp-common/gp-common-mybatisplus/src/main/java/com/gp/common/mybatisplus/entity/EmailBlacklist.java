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
 * 邮箱黑名单对象 t_email_blacklist
 *
 * @author axing
 * @date 2025-04-09
 */
@ApiModel("邮箱黑名单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_email_blacklist")
public class EmailBlacklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:EmailBlacklist";

    /** 主键id */
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id")
     private Long id;

    /** 邮箱后缀 */
    @ApiModelProperty("邮箱后缀")
    @TableField("email_suffix")
    @Excel(name = "邮箱后缀")
    private String emailSuffix;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

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

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
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
