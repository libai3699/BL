package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
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
import java.util.Map;

/**
 * 用户支付信息对象 t_user_pay_method
 *
 * @author axing
 * @date 2026-01-29
 */
@ApiModel("用户支付信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_pay_method", autoResultMap = true)
public class UserPayMethod extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserPayMethod";

    /** 主键id */
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id")
     private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 支付类型id */
    @ApiModelProperty("支付类型id")
    @TableField("pay_type_id")
    @Excel(name = "支付类型id")
    private Integer payTypeId;

    /** 支付类型编码 */
    @ApiModelProperty("支付类型编码")
    @TableField("pay_type_code")
    @Excel(name = "支付类型编码")
    private String payTypeCode;

    /** 使用类型(1 支付, 2 提现) */
    @ApiModelProperty("使用类型(1 支付, 2 提现)")
    @TableField("use_type")
    @Excel(name = "使用类型(1 支付, 2 提现)")
    private Integer useType;

    /** 支付信息 */
    @ApiModelProperty("支付信息")
    @Excel(name = "支付信息")
    @TableField(value = "bind_data", typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> bindData;

    /** 是否默认支付方式(0 非默认, 1 默认) */
    @ApiModelProperty("是否默认支付方式(0 非默认, 1 默认)")
    @TableField("is_default")
    @Excel(name = "是否默认支付方式(0 非默认, 1 默认)")
    private Integer isDefault;

    /** 排序(如果有多个的话,按照这个排序) */
    @ApiModelProperty("排序(如果有多个的话,按照这个排序)")
    @TableField("sort")
    @Excel(name = "排序(如果有多个的话,按照这个排序)")
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


}
