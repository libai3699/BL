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
 * 用户额外数据对象 t_user_ext
 *
 * @author axing
 * @date 2024-05-14
 */
@ApiModel("用户额外数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_ext")
public class UserExt extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserExt";

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

    /** 类型(1 转盘次数, 2,打码量 3 提现打码量, 4 未领取反水, 5 未领取返佣 6 彩金) */
    @ApiModelProperty("类型(1 转盘次数, 2,打码量 3 提现打码量, 4 未领取反水, 5 未领取返佣 6 彩金 7 累计彩金8 充值彩金 9转盘彩金 10 活动彩金  11 未领取代理工资 12未完成打码量)")
    @TableField("type")
    @Excel(name = "类型(1 转盘次数, 2,打码量 3 提现打码量, 4 未领取反水, 5 未领取返佣 6 彩金 7 累计彩金8 充值彩金 9转盘彩金 10 活动彩金  11 未领取代理工资 12未完成打码量")
    private Integer type;

    /** 数值 */
    @ApiModelProperty("数值")
    @TableField("amount")
    @Excel(name = "数值")
    private BigDecimal amount;

    /** 盐 */
    @ApiModelProperty("盐")
    @TableField("salt")
    @Excel(name = "盐")
    private String salt;

    /** 签名时间 */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间")
    private Long signTime;

    /** 签名 */
    @ApiModelProperty("签名")
    @TableField("sign")
    @Excel(name = "签名")
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

    /** 币种 */
    @ApiModelProperty("币种")
    @TableField("currency_id")
    @Excel(name = "币种")
    private Integer currencyId;

    /** mask币种id */
    @ApiModelProperty("mask币种id")
    @TableField("item_id")
    @Excel(name = "mask币种id")
    private Integer itemId;

    /** 链名称 */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /** mask币加链名称 */
    @ApiModelProperty("mask币加链名称")
    @TableField("item_name")
    @Excel(name = "mask币加链名称")
    private String itemName;


}
