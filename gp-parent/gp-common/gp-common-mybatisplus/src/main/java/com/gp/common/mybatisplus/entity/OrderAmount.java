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
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 充值订单对象 t_order_amount
 *
 * @author axing
 * @date 2024-06-28
 */
@ApiModel("充值订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_amount")
public class OrderAmount extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderAmount";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 充值单号
     */
    @ApiModelProperty("充值单号")
    @TableField("order_no")
    @Excel(name = "充值单号")
    private String orderNo;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    @TableField("currency_id")
    private Integer currencyId;

    /**
     * 币种ID
     */
    @ApiModelProperty("币种ID")
    @TableField("item_id")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    private String chainTag;

    /**
     * 币加链名称
     */
    @ApiModelProperty("币加链名称")
    @TableField("item_name")
    private String itemName;

    /**
     * 交易hash
     */
    @ApiModelProperty("交易hash")
    @TableField("tx_id")
    @Excel(name = "交易hash")
    private String txId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /**
     * tg用户Id
     */
    @ApiModelProperty("tg用户Id")
    @TableField("tg_user_id")
    @Excel(name = "tg用户Id", cellType = Excel.ColumnType.NUMERIC)
    private Long tgUserId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    @TableField(exist = false)
    @Excel(name = "用户名称")
    private String username;

    /**
     * 用户商户标签（来自 t_user_merchant_tag.merchant_tag_codes，逗号分隔）
     */
    @ApiModelProperty("用户商户标签")
    @TableField(exist = false)
    @Excel(name = "商户标签")
    private String merchantTagCodes;

    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    //    @TableField(exist = false)
    @TableField("channel_id")
    @Excel(name = "渠道id")
    private Long channelId;

    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名称")
    @TableField(exist = false)
    @Excel(name = "渠道名称")
    private String channelName;
    /**
     * 股东ID
     */
    @ApiModelProperty("股东ID")
    @TableField("shareholder_id")
    @Excel(name = "股东ID")
    private Long shareholderId;

    /**
     * 股东名称
     */
    @ApiModelProperty("股东名称")
    @Excel(name = "股东名称")
    @TableField(exist = false)
    private String shareholderName;

    /**
     * 法币金额
     */
    @ApiModelProperty("法币金额")
    @TableField("law_amount")
    @Excel(name = "法币金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal lawAmount;
    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    /**
     * 彩金
     */
    @ApiModelProperty("彩金")
    @TableField("bonus_amount")
    @Excel(name = "彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal bonusAmount;

    /**
     * 提现打码量
     */
    @ApiModelProperty("提现打码量")
    @TableField("withdraw_statement")
    @Excel(name = "提现打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal withdrawStatement;
    /**
     * 总金额
     */
    @ApiModelProperty("总金额")
    @TableField("total_amount")
    @Excel(name = "总金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAmount;

    /**
     * 地址类型(1内部地址, 2外部地址)
     */
    @ApiModelProperty("地址类型(1内部地址, 2外部地址)")
    @TableField("exchange_type")
    private Integer exchangeType;

    /**
     * 来源地址
     */
    @ApiModelProperty("来源地址")
    @TableField("from_address")
    @Excel(name = "来源地址")
    private String fromAddress;

    /**
     * 目标地址
     */
    @ApiModelProperty("目标地址")
    @TableField("to_address")
    @Excel(name = "目标地址")
    private String toAddress;

    /**
     * 订单状态(0 待支付, 1 已支付, 2 已取消)
     */
    @ApiModelProperty("订单状态(0 待支付, 1 已支付, 2 已取消)")
    @TableField("order_status")
    @Excel(name = "订单状态", readConverterExp = "0=待支付,1=已支付,2=已取消")
    private Integer orderStatus;

    /**
     * 上游订单号
     */
    @ApiModelProperty("上游订单号")
    @TableField("up_order_no")
    @Excel(name = "上游订单号")
    private String upOrderNo;

    /**
     * 上游订单信息
     */
    @ApiModelProperty("上游订单信息")
    @TableField("up_pay_info")
    private String upPayInfo;

    /**
     * 第三方支付跳转链接（待支付订单可再次跳转支付）
     */
    @ApiModelProperty("支付链接")
    @TableField("pay_url")
    private String payUrl;

    /**
     * 支付方式(0 直冲 1 mpay 2.pix 3 pix越南盾)
     */
    @ApiModelProperty("支付方式(0=直冲,1=MPay,2=PixPay,3=越南盾,4=upay,5=pay1818) ")
    @TableField("type")
    @Excel(name = "支付方式", readConverterExp = "0=直冲,1=MPay,2=PixPay,3=越南盾,4=upay,5=pay1818,6=恒聚财-印度卢比,7=恒聚财-波币,8=恒聚财-汇旺,9=恒聚财-U支付")
    private Integer type;

    @ApiModelProperty("支付商户编码")
    @TableField("merchant_code")
    @Excel(name = "支付商户编码")
    private String merchantCode;

    /**
     * 支付商户ID
     */
    @ApiModelProperty("支付商户ID")
    @TableField("pay_merchant_id")
    private Long payMerchantId;

    /**
     * 支付商户名称
     */
    @ApiModelProperty("支付商户名称")
    @TableField(exist = false)
    @Excel(name = "支付商户名称")
    private String payMerchantName;

    /**
     * 支付通道id
     */
    @ApiModelProperty("支付通道id")
    @TableField("pay_channel_id")
    private Long payChannelId;

    /**
     * 支付通道名称
     */
    @ApiModelProperty("支付通道名称")
    @TableField(exist = false)
    @Excel(name = "支付通道名称")
    private String payChannelName;

    /**
     * 通道支付类型编号
     */
    @ApiModelProperty("支付类型id")
    @TableField("pay_type_id")
    private Long payTypeId;

    /**
     * 支付类型名称
     */
    @ApiModelProperty("支付类型名称")
    @TableField(exist = false)
    @Excel(name = "支付类型名称")
    private String payTypeName;

    /**
     * 支付时间
     */
    @ApiModelProperty("支付时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("pay_time")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    @ApiModelProperty("支付时间数组")
    @TableField(exist = false)
    private String[] payTimes;

    /**
     * 签名时间
     */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Long signTime;

    /**
     * 签名
     */
    @ApiModelProperty("签名")
    @TableField("sign")
    @Excel(name = "签名")
    private String sign;

    /**
     * 下单时间
     */
    @ApiModelProperty("下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "下单时间")
    private Date createTime;
    @ApiModelProperty("下单时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    @ApiModelProperty("渠道IdList")
    @TableField(exist = false)
    private List<Long> channelIdList;

}
