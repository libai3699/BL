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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 首页弹窗对象 t_homepage_popup
 *
 * @author axing
 * @date 2024-10-21
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("首页弹窗")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_homepage_popup")
public class HomepagePopup extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:HomepagePopup";

    /** 主键ID */
    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
     private Long id;

    /** 标题图片地址 */
    @ApiModelProperty("标题图片地址")
    @TableField("title_img_url")
    @Excel(name = "标题图片地址")
    private String titleImgUrl;

    /** 内容 */
    @ApiModelProperty("内容")
    @TableField("content")
    @Excel(name = "内容")
    private String content;

    /** 类型(0首页) */
    @ApiModelProperty("类型(0首页)")
    @TableField("type")
    @Excel(name = "类型(0首页)")
    private Integer type;

    /** 0内部 1外部 */
    @ApiModelProperty("0内部 1外部")
    @TableField("jump_type")
    @Excel(name = "0内部 1外部")
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
    @Excel(name = "状态(0 禁用, 1 启用)")
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
    @Excel(name = "删除标志(0删除  1存在)")
    private Integer hasDel;

    /** 活动图片地址 */
    @ApiModelProperty("活动图片地址")
    @TableField("events_img_url")
    @Excel(name = "活动图片地址")
    private String eventsImgUrl;

    /** 标题 */
    @ApiModelProperty("标题")
    @TableField("title")
    @Excel(name = "标题")
    private String title;


}
