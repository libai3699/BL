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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 法币提现订单对象 t_order_law_withdraw
 *
 * @author axing
 * @date 2024-06-30
 */
@ApiModel("法币提现订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_order_law_withdraw", autoResultMap = true)
public class OrderLawWithdraw extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderLawWithdraw";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 提现单号
     */
    @ApiModelProperty("提现单号")
    @TableField("order_no")
    @Excel(name = "提现单号")
    private String orderNo;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    @TableField("currency_id")
    @Excel(name = "币种id")
    private Integer currencyId;

    /**
     * 币种ID
     */
    @ApiModelProperty("币种ID")
    @TableField("item_id")
    @Excel(name = "币种ID")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /**
     * 币加链名称
     */
    @ApiModelProperty("币加链名称")
    @TableField("item_name")
    @Excel(name = "币加链名称")
    private String itemName;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

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
     * tg用户Id
     */
    @ApiModelProperty("tg用户Id")
    @TableField("tg_user_id")
    @Excel(name = "tg用户Id")
    private Long tgUserId;
    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
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
     * 股东ID
     */
    @ApiModelProperty("股东名称")
    @Excel(name = "股东名称")
    @TableField(exist = false)
    private String shareholderName;

    /**
     * 国家
     */
    @ApiModelProperty("国家")
    @TableField("lan_key")
    @Excel(name = "国家")
    private String lanKey;
    /**
     * 支付商户id
     */
    @ApiModelProperty("支付商户id")
    @TableField("pay_merchant_id")
    @Excel(name = "支付商户id")
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
    @Excel(name = "支付通道id")
    private Long payChannelId;

    /**
     * 支付通道名称
     */
    @ApiModelProperty("支付通道名称")
    @TableField(exist = false)
    @Excel(name = "支付通道名称")
    private String payChannelName;

    /**
     * 支付类型id
     */
    @ApiModelProperty("支付类型id")
    @TableField("pay_type_id")
    @Excel(name = "支付类型id")
    private Long payTypeId;

    /**
     * 支付类型名称
     */
    @ApiModelProperty("支付类型名称")
    @TableField(exist = false)
    @Excel(name = "支付类型名称")
    private String payTypeName;

    /**
     * 支付编码
     */
    @ApiModelProperty("支付编码")
    @TableField("pay_code")
    @Excel(name = "支付编码")
    private String payCode;

    /**
     * 支付信息
     */
    @ApiModelProperty("提现账户信息")
    @Excel(name = "提现账户信息")
    @TableField(value = "bind_data", typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> bindData;

    /**
     * 账号信息 json
     */
    @ApiModelProperty("账号信息 json")
    @TableField("account")
    @Excel(name = "账号信息 json")
    private String account;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额")
    private BigDecimal amount;

    /**
     * 手续费
     */
    @ApiModelProperty("手续费")
    @TableField("fee")
    @Excel(name = "手续费")
    private BigDecimal fee;

    /**
     * 实际到账金额usdt
     */
    @ApiModelProperty("实际到账金额usdt")
    @TableField("real_amount")
    @Excel(name = "实际到账金额usdt")
    private BigDecimal realAmount;

    /**
     * 实际到账金额(法币)
     */
    @ApiModelProperty("实际到账金额(法币)")
    @TableField("real_law_amount")
    @Excel(name = "实际到账金额(法币)")
    private BigDecimal realLawAmount;

    /**
     * 比例(当时的比例)
     */
    @ApiModelProperty("比例(当时的比例)")
    @TableField("ratio")
    @Excel(name = "比例(当时的比例)")
    private BigDecimal ratio;

    /**
     * 来源地址
     */
    @ApiModelProperty("来源地址")
    @TableField("from_addr")
    @Excel(name = "来源地址")
    private String fromAddr;

    /**
     * 订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败)
     */
    @ApiModelProperty("订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败)")
    @TableField("order_status")
    @Excel(name = "订单状态", readConverterExp = "0=待审核,1=风险审核通过,2=财务通过,3=已拒绝,4=提现成功,5=提现失败,6=上游下单成功,7=上游下单失败")
    private Integer orderStatus;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 审核人id
     */
    @ApiModelProperty("审核人id")
    @TableField("check_user_id")
    @Excel(name = "审核人id")
    private Long checkUserId;

    /**
     * 审核人用户名
     */
    @ApiModelProperty("审核人用户名")
    @TableField("check_username")
    @Excel(name = "审核人用户名")
    private String checkUsername;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("check_time")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date checkTime;
    @ApiModelProperty("审核时间数组")
    @TableField(exist = false)
    private String[] checkTimes;

    /**
     * 上游订单号
     */
    @ApiModelProperty("上游订单号")
    @TableField("up_order_no")
    @Excel(name = "上游订单号")
    private String upOrderNo;

    /**
     * 上游下单信息
     */
    @ApiModelProperty("上游下单信息")
    @TableField("up_order_info")
    @Excel(name = "上游下单信息")
    private String upOrderInfo;

    /**
     * 上游回调信息
     */
    @ApiModelProperty("上游回调信息")
    @TableField("up_notify_info")
    @Excel(name = "上游回调信息")
    private String upNotifyInfo;

    /**
     * 到账时间
     */
    @ApiModelProperty("到账时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("withdraw_time")
    @Excel(name = "到账时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date withdrawTime;

    @ApiModelProperty("到账时间数组")
    @TableField(exist = false)
    private String[] withdrawTimes;

    /**
     * 签名时间
     */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间")
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
    @Excel(name = "下单时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("下单时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    /**
     * 列表合计行：当前筛选条件下订单总数
     */
    @ApiModelProperty("筛选条件下订单总数")
    @TableField(exist = false)
    private Long summaryOrderNum;

}
