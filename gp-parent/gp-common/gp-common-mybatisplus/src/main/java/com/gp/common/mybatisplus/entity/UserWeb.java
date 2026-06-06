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
 * 用户网页登录账号对象 t_user_web
 *
 * @author axing
 * @date 2026-02-26
 */
@ApiModel("用户网页登录账号")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_web")
public class UserWeb extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserWeb";

    /** 主键ID */
    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
     private Long id;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /** 网页登录账号(最少6位,仅支持中英文) */
    @ApiModelProperty("网页登录账号(最少6位,仅支持中英文)")
    @TableField("account")
    @Excel(name = "网页登录账号(最少6位,仅支持中英文)")
    private String account;

    /** 网页登录密码(加密) */
    @ApiModelProperty("网页登录密码(加密)")
    @TableField("password")
    @Excel(name = "网页登录密码(加密)")
    private String password;

    /** 盐 */
    @ApiModelProperty("盐")
    @TableField("salt")
    private String salt;

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
