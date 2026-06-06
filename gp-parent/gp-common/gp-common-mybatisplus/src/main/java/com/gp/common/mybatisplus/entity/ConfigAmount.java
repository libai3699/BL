package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 金额配置表
 */
@ApiModel(description = "金额配置表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_config_amount")
public class ConfigAmount extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TConfigAmount";

    /** 配置ID */
    @ApiModelProperty("配置ID")
    @TableId(value = "config_id", type = IdType.AUTO)
    @Excel(name = "配置ID", cellType = Excel.ColumnType.NUMERIC)
    private Integer configId;

    /** 配置名称 */
    @ApiModelProperty("配置名称")
    @TableField("config_name")
    @Excel(name = "配置名称")
    private String configName;

    /** 配置key */
    @ApiModelProperty("配置key")
    @TableField("config_key")
    @Excel(name = "配置key")
    private String configKey;

    /** 配置的值 */
    @ApiModelProperty("配置的值")
    @TableField("config_value")
    @Excel(name = "配置的值")
    private String configValue;
    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;
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
    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    /** 商户是否显示 0展示 1不展示 */
    @ApiModelProperty("商户是否显示 1展示 0不展示")
    @TableField("is_show")
    @Excel(name = "商户是否显示 1展示 0不展示")
    private Long isShow;
}
