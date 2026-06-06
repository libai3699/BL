package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
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
 * 用户钱包对象 t_user_wallet
 *
 * @author axing
 * @date 2024-05-20
 */
@ApiModel("用户钱包")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_wallet")
public class UserWallet extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserWallet";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 飞机id */
    @ApiModelProperty("飞机id")
    @TableField("user_tg_id")
    @Excel(name = "飞机id")
    private Long userTgId;

    /** 飞机名称 */
    @ApiModelProperty("飞机名称")
    @TableField("user_tg_name")
    @Excel(name = "飞机名称")
    private String userTgName;

    /** 飞机用户名 */
    @ApiModelProperty("飞机用户名")
    @TableField("user_tg_username")
    @Excel(name = "飞机用户名")
    private String userTgUsername;

    /**
     * 币种
     */
    @TableField(exist = false)
    @ApiModelProperty(value="币种")
    @Excel(name = "币种")
    private String currency;

    /**
     * 币种
     */
    @TableField(exist = false)
    @ApiModelProperty(value="币种图标")
    private String icon;

    @ApiModelProperty(value="黑色币种图标")
    @TableField(exist = false)
    private String blackIcon;

    /** 币种id */
    @ApiModelProperty("币种id")
    @TableField("currency_id")
    @Excel(name = "币种id")
    private Integer currencyId;

    /** 币id */
    @ApiModelProperty("币id")
    @TableField("item_id")
    @Excel(name = "币id")
    private Integer itemId;

    /** 链名称 */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /** 金额 */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 地址 */
    @ApiModelProperty("地址")
    @TableField("mask_addr")
    @Excel(name = "地址")
    private String maskAddr;
    @ApiModelProperty("盐")
    @TableField("salt")
    private String salt;

    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;

    /** 关联的订单号 */
    @ApiModelProperty("关联的订单号")
    @TableField("last_amount_id")
    @Excel(name = "关联的订单号id")
    private Long lastAmountId;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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

    @ApiModelProperty("位数")
    @TableField(exist = false)
    @Excel(name = "位数")
    private Integer digit;

    @ApiModelProperty("钱包信息")
    @TableField(exist = false)
    @Excel(name = "钱包信息")
    private UserWallet userWallet;

    /**
     * 靓号标识
     */
    @ApiModelProperty(value="靓号标识")
    @TableField(exist = false)
    private Boolean LHFlag = false;

}
