package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
import java.util.List;

/**
 * 提现订单表
 */
@ApiModel(description = "提现订单表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_order_withdraw")
public class OrderWithdraw extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TOrderWithdraw";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 0mpay钱包 1 冷钱包
     */
    @ApiModelProperty("0mpay钱包 1 冷钱包")
    @TableField("type")
    @Excel(name = "钱包", readConverterExp = "1=冷钱包,0=mpay钱包")
    private Integer type;

    @ApiModelProperty("支付商户编码")
    @TableField("merchant_code")
    @Excel(name = "支付商户编码")
    private String merchantCode;

    /**
     * 支付商户ID
     */
    @ApiModelProperty("支付商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 支付通道id
     */
    @ApiModelProperty("支付通道id")
    @TableField("pay_channel_id")
    private Long payChannelId;

    /**
     * 通道支付类型编号
     */
    @ApiModelProperty("通道支付类型编号")
    @TableField("pay_type_id")
    private Long payTypeId;

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
    @TableField(value = "currency_id")
    @ApiModelProperty(value = "币种id")
    private Integer currencyId;

    /**
     * 币种ID
     */
    @TableField(value = "item_id")
    @ApiModelProperty(value = "币种ID")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    private String chainTag;

    /**
     * 币加连
     */
    @TableField(value = "item_name")
    @ApiModelProperty(value = "币名称")
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
     * 股东ID
     */
    @ApiModelProperty("股东名称")
    @Excel(name = "股东名称")
    @TableField(exist = false)
    private String shareholderName;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;
    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("law_amount")
    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal lawAmount;

    /**
     * 地址类型(1 内部地址, 2 外部地址)
     */
    @ApiModelProperty("地址类型(1 内部地址, 2 外部地址)")
    @TableField("exchange_type")
    private Integer exchangeType;

    /**
     * 手续费
     */
    @ApiModelProperty("手续费")
    @TableField("fee")
    @Excel(name = "手续费", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal fee;

    /**
     * 实际到账金额
     */
    @ApiModelProperty("实际到账金额")
    @TableField("real_amount")
    @Excel(name = "实际到账金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal realAmount;

    /**
     * 来源地址
     */
    @ApiModelProperty("来源地址")
    @TableField("from_addr")
    @Excel(name = "来源地址")
    private String fromAddr;

    /**
     * 收款地址
     */
    @ApiModelProperty("收款地址")
    @TableField("to_addr")
    @Excel(name = "收款地址")
    private String toAddr;

    /**
     * 订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败)
     */
    @ApiModelProperty("订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败)")
    @TableField("order_status")
    @Excel(name = "订单状态", readConverterExp = "0=待审核,1=风险审核通过,2=财务通过,3=已拒绝,4=提现成功,5=提现失败,6=上游下单成功,7=上游下单失败")
    private Integer orderStatus;

    /**
     * 打款截图
     */
    @ApiModelProperty("打款截图")
    @TableField("screen_shot")
    @Excel(name = "打款截图")
    private String screenShot;

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
    @Excel(name = "审核人id", cellType = Excel.ColumnType.NUMERIC)
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
    private String upOrderInfo;

    /**
     * 上游回调信息
     */
    @ApiModelProperty("上游回调信息")
    @TableField("up_notify_info")
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

    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    @ApiModelProperty("到账时间数组")
    @TableField(exist = false)
    private String[] withdrawTimes;

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

    @ApiModelProperty("渠道IdList")
    @TableField(exist = false)
    private List<Long> channelIdList;
}
