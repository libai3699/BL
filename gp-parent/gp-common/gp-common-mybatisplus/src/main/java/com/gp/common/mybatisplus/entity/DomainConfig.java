package com.gp.common.mybatisplus.entity;

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
 * 域名配置对象 t_domain_config
 *
 * @author axing
 * @date 2026-02-23
 */
@ApiModel("域名配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_domain_config")
public class DomainConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:DomainConfig";

    /** 域名ID */
    @ApiModelProperty("域名ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "域名ID")
     private Long id;

    /** 域名地址 */
    @ApiModelProperty("域名地址")
    @TableField("domain_name")
    @Excel(name = "域名地址")
    private String domainName;

    /** 域名类型(1 图片域名 2 网站域名) */
    @ApiModelProperty("域名类型(1 图片域名 2 网站域名)")
    @TableField("domain_type")
    @Excel(name = "域名类型(1 图片域名 2 网站域名)")
    private Integer domainType;

    /** 地区 */
    @ApiModelProperty("地区")
    @TableField("region_code")
    @Excel(name = "地区")
    private String regionCode;

    /** 排序 */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /** 状态(0 禁用, 1 启用) */
    @ApiModelProperty("状态(0 禁用, 1 启用)")
    @TableField("status")
    @Excel(name = "状态(0 禁用, 1 启用)")
    private Integer status;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

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

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

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


}
