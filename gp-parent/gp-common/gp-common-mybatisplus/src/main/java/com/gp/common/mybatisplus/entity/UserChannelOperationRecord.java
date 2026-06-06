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
 * 用户渠道修改记录对象 t_user_channel_operation_record
 *
 * @author axing
 * @date 2025-08-19
 */
@ApiModel("用户渠道修改记录")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_channel_operation_record")
public class UserChannelOperationRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserChannelOperationRecord";

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

    /** 原渠道ID */
    @ApiModelProperty("原渠道ID")
    @TableField("original_channel_id")
    @Excel(name = "原渠道ID")
    private Long originalChannelId;

    /** 原渠道名称 */
    @ApiModelProperty("原渠道名称")
    @TableField("original_channel_name")
    @Excel(name = "原渠道名称")
    private String originalChannelName;

    /** 原股东ID */
    @ApiModelProperty("原股东ID")
    @TableField("original_shareholder_id")
    @Excel(name = "原股东ID")
    private Long originalShareholderId;

    /** 原股东名称 */
    @ApiModelProperty("原股东名称")
    @TableField("original_shareholder_name")
    @Excel(name = "原股东名称")
    private String originalShareholderName;

    /** 新渠道ID */
    @ApiModelProperty("新渠道ID")
    @TableField("new_channel_id")
    @Excel(name = "新渠道ID")
    private Long newChannelId;

    /** 新渠道名称 */
    @ApiModelProperty("新渠道名称")
    @TableField("new_channel_name")
    @Excel(name = "新渠道名称")
    private String newChannelName;

    /** 新股东ID */
    @ApiModelProperty("新股东ID")
    @TableField("new_shareholder_id")
    @Excel(name = "新股东ID")
    private Long newShareholderId;

    /** 新股东名称 */
    @ApiModelProperty("新股东名称")
    @TableField("new_shareholder_name")
    @Excel(name = "新股东名称")
    private String newShareholderName;

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
