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
 * 附加按钮对象 t_attach_button
 *
 * @author axing
 * @date 2025-06-09
 */
@ApiModel("附加按钮")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_attach_button")
public class AttachButton extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:AttachButton";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 内容 */
    @ApiModelProperty("内容")
    @TableField("name")
    @Excel(name = "内容")
    private String name;

    /** 路径 */
    @ApiModelProperty("路径")
    @TableField("url")
    @Excel(name = "路径")
    private String url;

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


}
