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
 * 上级用户修改记录对象 t_user_super_operation_record
 *
 * @author axing
 * @date 2025-08-19
 */
@ApiModel("上级用户修改记录")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_super_operation_record")
public class UserSuperOperationRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserSuperOperationRecord";

    /** ID */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
     private Long id;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /** 原上级用户ID */
    @ApiModelProperty("原上级用户ID")
    @TableField("original_user_id")
    @Excel(name = "原上级用户ID")
    private Long originalUserId;

    /** 原上级用户飞机名称 */
    @ApiModelProperty("原上级用户飞机名称")
    @TableField("original_user_tg_name")
    @Excel(name = "原上级用户飞机名称")
    private String originalUserTgName;

    /** 新上级用户ID */
    @ApiModelProperty("新上级用户ID")
    @TableField("new_user_id")
    @Excel(name = "新上级用户ID")
    private Long newUserId;

    /** 新上级用户飞机名称 */
    @ApiModelProperty("新上级用户飞机名称")
    @TableField("new_user_tg_name")
    @Excel(name = "新上级用户飞机名称")
    private String newUserTgName;

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
