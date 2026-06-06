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
 * 红包封面对象 t_red_envelope_cover
 *
 * @author axing
 * @date 2024-12-26
 */
@ApiModel("红包封面")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_red_envelope_cover")
public class RedEnvelopeCover extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:RedEnvelopeCover";

    /** 封面id */
    @ApiModelProperty("封面id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "封面id")
     private Integer id;

    /** 封面名称 */
    @ApiModelProperty("封面名称")
    @TableField("cover_name")
    @Excel(name = "封面名称")
    private String coverName;

    /** 封面地址 */
    @ApiModelProperty("封面地址")
    @TableField("cover_addr")
    @Excel(name = "封面地址")
    private String coverAddr;

    /** 缩略图 */
    @ApiModelProperty("缩略图")
    @TableField("thumbnail")
    @Excel(name = "缩略图")
    private String thumbnail;

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

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    /** 删除标志(0删除  1存在) */
    @ApiModelProperty("删除标志(0删除  1存在)")
    @TableField("has_del")
    @Excel(name = "删除标志(0删除  1存在)")
    private Integer hasDel;


}
