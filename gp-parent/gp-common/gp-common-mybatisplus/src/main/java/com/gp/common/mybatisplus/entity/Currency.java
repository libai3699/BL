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
 * 币种对象 t_currency
 *
 * @author axing
 * @date 2024-01-12
 */
@ApiModel("币种")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_currency")
public class Currency extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TCurrency";

    /** 币种id */
    @ApiModelProperty("币种id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "币种id")
    private Integer id;

    /** 币种 */
    @ApiModelProperty("币种")
    @TableField("currency")
    @Excel(name = "币种")
    private String currency;

    /** 币种图片 */
    @ApiModelProperty("币种图标")
    @TableField("icon")
    @Excel(name = "币种图标")
    private String icon;

    /** 币种图片 */
    @ApiModelProperty("币种图标")
    @TableField("black_icon")
    @Excel(name = "币种图标")
    private String blackIcon;

    /** mask币种id */
    @ApiModelProperty("mask币种id")
    @TableField("item_id")
    @Excel(name = "mask币种id")
    private Integer itemId;

    /** 币名称 */
    @ApiModelProperty("币名称")
    @TableField("item_name")
    @Excel(name = "mask币种名称")
    private String itemName;

    /** mask链名称 */
    @ApiModelProperty("mask链名称")
    @TableField("chain_tag")
    @Excel(name = "mask链名称")
    private String chainTag;

    /** 状态（0关闭 1开启） */
    @ApiModelProperty("状态（0关闭 1开启）")
    @TableField("status")
    @Excel(name = "状态")
    private Integer status;

    /** 排序 */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;

    @ApiModelProperty("钱包信息")
    @TableField(exist = false)
    private UserWallet userWallet;

    @ApiModelProperty("位数")
    @TableField("digit")
    @Excel(name = "位数")
    private Integer digit;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;
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
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;


}
