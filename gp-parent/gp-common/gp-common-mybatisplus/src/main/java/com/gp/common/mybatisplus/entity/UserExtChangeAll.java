//package com.gp.common.mybatisplus.entity;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.format.annotation.DateTimeFormat;
//import com.gp.common.base.excel.annotation.Excel;
//import com.gp.common.mybatisplus.base.BaseEntity;
//
///**
// * 用户账变对象 t_user_ext_change_all
// *
// * @author axing
// * @date 2024-10-19
// */
//@ApiModel("用户账变")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@TableName("t_user_ext_change_all")
//public class UserExtChangeAll extends BaseEntity
//{
//    private static final long serialVersionUID = 1L;
//    public static final String REDIS_KEY = "msg:UserExtChangeAll";
//
//    /** 用户额外属性账变id */
//    @ApiModelProperty("用户额外属性账变id")
//    @TableId(value = "id", type = IdType.AUTO)
//    @Excel(name = "用户额外属性账变id")
//     private Long id;
//
//    /** 用户额外属性类型 */
//    @ApiModelProperty("用户额外属性类型")
//    @TableField("ext_type")
//    @Excel(name = "用户额外属性类型")
//    private Integer extType;
//
//    /** 用户ID */
//    @ApiModelProperty("用户ID")
//    @TableField("user_id")
//    @Excel(name = "用户ID")
//    private Long userId;
//
//    /** tg用户ID */
//    @ApiModelProperty("tg用户ID")
//    @TableField("tg_user_id")
//    @Excel(name = "tg用户ID")
//    private Long tgUserId;
//
//    /** 关联的订单号 */
//    @ApiModelProperty("关联的订单号")
//    @TableField("order_no")
//    @Excel(name = "关联的订单号")
//    private String orderNo;
//
//    /** 订单类型(1 注单反水返佣, 2 反水返佣领取) */
//    @ApiModelProperty("订单类型(1 注单反水返佣, 2 反水返佣领取)")
//    @TableField("order_type")
//    @Excel(name = "订单类型(1 注单反水返佣, 2 反水返佣领取)")
//    private Integer orderType;
//
//    /** 收支类型(1 收入, 2 支出) */
//    @ApiModelProperty("收支类型(1 收入, 2 支出)")
//    @TableField("account_type")
//    @Excel(name = "收支类型(1 收入, 2 支出)")
//    private Integer accountType;
//
//    /** 帐变类型(1 用户返水, 2 上级返佣, 3 反水领取, 4 返佣领取) */
//    @ApiModelProperty("帐变类型(1 用户返水, 2 上级返佣, 3 反水领取, 4 返佣领取)")
//    @TableField("type")
//    @Excel(name = "帐变类型(1 用户返水, 2 上级返佣, 3 反水领取, 4 返佣领取)")
//    private Integer type;
//
//    /** 变更金额 */
//    @ApiModelProperty("变更金额")
//    @TableField("amount")
//    @Excel(name = "变更金额")
//    private BigDecimal amount;
//
//    /** 变更前金额 */
//    @ApiModelProperty("变更前金额")
//    @TableField("old_amount")
//    @Excel(name = "变更前金额")
//    private BigDecimal oldAmount;
//
//    /** 变更后金额 */
//    @ApiModelProperty("变更后金额")
//    @TableField("new_amount")
//    @Excel(name = "变更后金额")
//    private BigDecimal newAmount;
//
//    /** 备注 */
//    @ApiModelProperty("备注")
//    @TableField("remark")
//    @Excel(name = "备注")
//    private String remark;
//
//    /** 操作人 */
//    @ApiModelProperty("操作人")
//    @TableField("operator")
//    @Excel(name = "操作人")
//    private String operator;
//
//    /** 签名时间 */
//    @ApiModelProperty("签名时间")
//    @TableField("sign_time")
//    @Excel(name = "签名时间")
//    private Long signTime;
//
//    /** 签名 */
//    @ApiModelProperty("签名")
//    @TableField("sign")
//    @Excel(name = "签名")
//    private String sign;
//
//    /** 变更时间 */
//    @ApiModelProperty("变更时间")
//    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @TableField("create_time")
//    @Excel(name = "变更时间")
//    private Date createTime;
//    @ApiModelProperty("变更时间数组")
//    @TableField(exist = false)
//    private String[] createTimes;
//
//
//}
