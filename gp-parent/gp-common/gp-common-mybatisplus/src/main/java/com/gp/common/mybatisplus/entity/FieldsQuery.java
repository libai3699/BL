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
import lombok.EqualsAndHashCode;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;

/**
 * 用户绑定字段对象 t_fields_query
 *
 * @author axing
 * @date 2024-05-13
 */
@ApiModel("用户绑定字段")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_fields_query")
public class FieldsQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TFieldsQuery";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long id;

    /** 菜单路由key */
    @ApiModelProperty("菜单路由key")
    @TableField("`key`")
    @Excel(name = "菜单路由key")
    private String key;

    /** 查询字段 */
    @ApiModelProperty("查询字段")
    @TableField("`fields`")
    @Excel(name = "查询字段")
    private String fields;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

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


}
