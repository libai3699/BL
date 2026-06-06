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
 * 轮播图对象 t_banner
 *
 * @author axing
 * @date 2024-05-02
 */
@ApiModel("轮播图")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_banner")
public class Banner extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Banner";

    /** 轮播图ID */
    @ApiModelProperty("轮播图ID")
    @TableId(value = "id", type = IdType.AUTO)
     private Long id;

    /** 地址 */
    @ApiModelProperty("地址")
    @TableField("banner_url")
    @Excel(name = "地址")
    private String bannerUrl;

    /** 类型(0 首页) */
    @ApiModelProperty("类型(0 首页)")
    @TableField("type")
    @Excel(name = "类型",readConverterExp="0=首页")
    private Integer type;

    /** 0内部 1外部 */
    @ApiModelProperty("0内部 1外部")
    @TableField("jump_type")
    @Excel(name = "0内部 1外部",readConverterExp="0=内部,1,外部")
    private Integer jumpType;

    /** 跳转url */
    @ApiModelProperty("跳转url")
    @TableField("jump_url")
    @Excel(name = "跳转url")
    private String jumpUrl;

    /** 语言的key */
    @ApiModelProperty("语言的key")
    @TableField("lan_key")
    @Excel(name = "语言的key")
    private String lanKey;

    /** 排序 */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /** 状态(0 禁用, 1 启用) */
    @ApiModelProperty("状态(0 禁用, 1 启用)")
    @TableField("status")
    @Excel(name = "状态(0 禁用, 1 启用)",readConverterExp="0=禁用,1,启用")
    private Integer status;

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

    /** 删除标志(0删除  1存在) */
    @ApiModelProperty("删除标志(0删除  1存在)")
    @TableField("has_del")
    @Excel(name = "删除标志(0删除  1存在)",readConverterExp="0=0删除,1,存在")
    private Integer hasDel;


}
