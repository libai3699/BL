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
 * 商户订单对象 t_order_merchant
 *
 * @author axing
 * @date 2024-08-11
 */
@ApiModel("商户订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_merchant")
public class OrderMerchant extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderMerchant";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long id;

    /** 充值单号 */
    @ApiModelProperty("充值单号")
    @TableField("order_no")
    @Excel(name = "充值单号")
    private String orderNo;

    /** 币种id */
    @ApiModelProperty("币种id")
    @TableField("currency_id")
    @Excel(name = "币种id")
    private Integer currencyId;

    /** 币种ID */
    @ApiModelProperty("币种ID")
    @TableField("item_id")
    @Excel(name = "币种ID")
    private Integer itemId;

    /** 链名称 */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /** 币加链名称 */
    @ApiModelProperty("币加链名称")
    @TableField("item_name")
    @Excel(name = "币加链名称")
    private String itemName;

    /** 商户id */
    @ApiModelProperty("商户id")
    @TableField("merchant_id")
    @Excel(name = "商户id")
    private Long merchantId;

    /** 商户名称 */
    @ApiModelProperty("商户名称")
    @TableField("merchant_name")
    @Excel(name = "商户名称")
    private String merchantName;

    /** 订单类型(1 上分, 2 下分) */
    @ApiModelProperty("订单类型(1 上分, 2 下分)")
    @TableField("order_type")
    @Excel(name = "订单类型(1 上分, 2 下分)")
    private Integer orderType;

    /** 金额 */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 地址类型(1内部地址, 2外部地址) */
    @ApiModelProperty("地址类型(1内部地址, 2外部地址)")
    @TableField("exchange_type")
    @Excel(name = "地址类型(1内部地址, 2外部地址)")
    private Integer exchangeType;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /** 目标地址 */
    @ApiModelProperty("目标地址")
    @TableField("address")
    @Excel(name = "目标地址")
    private String address;

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
