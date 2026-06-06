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
 * 银行卡对象 t_bank
 *
 * @author axing
 * @date 2025-06-19
 */
@ApiModel("银行卡")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_bank")
public class Bank extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Bank";

    /** ID */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** tg用户Id */
    @ApiModelProperty("tg用户Id")
    @TableField("tg_user_id")
    @Excel(name = "tg用户Id")
    private Long tgUserId;

    /** 银行卡号 */
    @ApiModelProperty("银行卡号")
    @TableField("card_number")
    @Excel(name = "银行卡号")
    private String cardNumber;

    /** 开户行 */
    @ApiModelProperty("开户行")
    @TableField("bank_name")
    @Excel(name = "开户行")
    private String bankName;

    /** 持卡人姓名 */
    @ApiModelProperty("持卡人姓名")
    @TableField("account_name")
    @Excel(name = "持卡人姓名")
    private String accountName;

    /** 0不是默认 1 默认 */
    @ApiModelProperty("0不是默认 1 默认")
    @TableField("is_default")
    @Excel(name = "0不是默认 1 默认")
    private Integer isDefault;

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


}
