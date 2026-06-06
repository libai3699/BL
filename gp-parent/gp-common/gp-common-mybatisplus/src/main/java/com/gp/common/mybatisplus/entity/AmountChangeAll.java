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
// * 用户账变对象 t_amount_change_all
// *
// * @author axing
// * @date 2024-10-19
// */
//@ApiModel("用户账变")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@TableName("t_amount_change_all")
//public class AmountChangeAll extends BaseEntity
//{
//    private static final long serialVersionUID = 1L;
//    public static final String REDIS_KEY = "msg:AmountChangeAll";
//
//    /** 商户账变id */
//    @ApiModelProperty("商户账变id")
//    @TableId(value = "id", type = IdType.AUTO)
//    @Excel(name = "商户账变id")
//     private Long id;
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
//    /** 币种id */
//    @ApiModelProperty("币种id")
//    @TableField("currency_id")
//    @Excel(name = "币种id")
//    private Integer currencyId;
//
//    /** 币种 */
//    @ApiModelProperty("币种")
//    @TableField("item_id")
//    @Excel(name = "币种")
//    private Integer itemId;
//
//    /** 链名称 */
//    @ApiModelProperty("链名称")
//    @TableField("chain_tag")
//    @Excel(name = "链名称")
//    private String chainTag;
//
//    /** 币加链名称 */
//    @ApiModelProperty("币加链名称")
//    @TableField("item_name")
//    @Excel(name = "币加链名称")
//    private String itemName;
//
//    /** 关联的订单号 */
//    @ApiModelProperty("关联的订单号")
//    @TableField("order_no")
//    @Excel(name = "关联的订单号")
//    private String orderNo;
//
//    /** 订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单, 4 转账订单, 5 红包发送订单, 6 红包接收订单 7充值会员订单 8 充值话费订单 9,购买靓号订单10 商户订单11 闪兑订单12 购买理财13赎回理财14购买理财审核拒绝 15购买理财审核成功16 商户提现) */
//    @ApiModelProperty("订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单, 4 转账订单, 5 红包发送订单, 6 红包接收订单 7充值会员订单 8 充值话费订单 9,购买靓号订单10 商户订单11 闪兑订单12 购买理财13赎回理财14购买理财审核拒绝 15购买理财审核成功16 商户提现)")
//    @TableField("order_type")
//    @Excel(name = "订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单, 4 转账订单, 5 红包发送订单, 6 红包接收订单 7充值会员订单 8 充值话费订单 9,购买靓号订单10 商户订单11 闪兑订单12 购买理财13赎回理财14购买理财审核拒绝 15购买理财审核成功16 商户提现)")
//    private Integer orderType;
//
//    /** 地址类型(1 内部地址, 2 外部地址) */
//    @ApiModelProperty("地址类型(1 内部地址, 2 外部地址)")
//    @TableField("exchange_type")
//    @Excel(name = "地址类型(1 内部地址, 2 外部地址)")
//    private Integer exchangeType;
//
//    /** 收支类型(1 收入, 2 支出) */
//    @ApiModelProperty("收支类型(1 收入, 2 支出)")
//    @TableField("account_type")
//    @Excel(name = "收支类型(1 收入, 2 支出)")
//    private Integer accountType;
//
//    /** 帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现, 5 金额转账, 6 红包发送, 7 红包接收, 8 审核拒绝, 9 提现失败, 10 红包未领取完退回, 11 老钱包迁移到新钱包 12 充值会员订单, 13充值话费订单, 14 充值会员回退, 15 充值话费回退 16 购买靓号 17 商户付款18 闪兑 19购买理财 20 赎回理财 21购买理财审核拒绝 22 购买理财审核成功 23 商户提现) */
//    @ApiModelProperty("帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现, 5 金额转账, 6 红包发送, 7 红包接收, 8 审核拒绝, 9 提现失败, 10 红包未领取完退回, 11 老钱包迁移到新钱包 12 充值会员订单, 13充值话费订单, 14 充值会员回退, 15 充值话费回退 16 购买靓号 17 商户付款18 闪兑 19购买理财 20 赎回理财 21购买理财审核拒绝 22 购买理财审核成功 23 商户提现)")
//    @TableField("type")
//    @Excel(name = "帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现, 5 金额转账, 6 红包发送, 7 红包接收, 8 审核拒绝, 9 提现失败, 10 红包未领取完退回, 11 老钱包迁移到新钱包 12 充值会员订单, 13充值话费订单, 14 充值会员回退, 15 充值话费回退 16 购买靓号 17 商户付款18 闪兑 19购买理财 20 赎回理财 21购买理财审核拒绝 22 购买理财审核成功 23 商户提现)")
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
