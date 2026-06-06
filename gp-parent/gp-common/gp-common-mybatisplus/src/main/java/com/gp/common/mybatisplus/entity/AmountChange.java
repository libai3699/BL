package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
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

/**
 * 用户账变表
 */
@ApiModel(description = "用户账变表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_amount_change")
public class AmountChange extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TAmountChange";

    /**
     * 商户账变id
     */
    @ApiModelProperty("商户账变id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "商户账变id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /**
     * tg用户ID
     */
    @ApiModelProperty("tg用户ID")
    @TableField("tg_user_id")
    @Excel(name = "tg用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long tgUserId;


    /**
     * 用户上级ID
     */
    @ApiModelProperty("用户上级ID")
    @TableField("super_user_id")
    @Excel(name = "用户上级ID", cellType = Excel.ColumnType.NUMERIC)
    private Long superUserId;


    /**
     * 用户渠道ID
     */
    @ApiModelProperty("渠道ID")
    @TableField("channel_id")
    @Excel(name = "渠道ID", cellType = Excel.ColumnType.NUMERIC)
    private Long channelId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    @TableField(exist = false)
    @Excel(name = "用户名称")
    private String username;

    /**
     * 关联的订单号
     */
    @ApiModelProperty(value = "币种图标")
    @TableField(exist = false)
    private String icon;

    /**
     * currencyId
     */
    @TableField(value = "currency_id")
    @ApiModelProperty(value = "currencyId")
    @Excel(name = "币种ID")
    private Integer currencyId;

    /**
     * 币种
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "币种")
    @Excel(name = "币种")
    private String currencyName;

    /**
     * 币种ID
     */
    @TableField(value = "item_id")
    @ApiModelProperty(value = "mask币种ID")
    @Excel(name = "mask币种ID")
    private Integer itemId;

    /**
     * 链名称
     */
    @TableField(value = "chain_tag")
    @ApiModelProperty(value = "链名称")
    @Excel(name = "链名称")
    private String chainTag;

    /**
     * 币加连
     */
    @TableField(value = "item_name")
    @ApiModelProperty(value = "币名称")
    @Excel(name = "币名称")
    private String itemName;

    /**
     * 关联的订单号
     */
    @ApiModelProperty("关联的订单号")
    @TableField("order_no")
    @Excel(name = "关联的订单号")
    private String orderNo;

    /**
     * 订单类型(1=人工上下分订单,2=充值订单,3=提现订单,4=游戏订单,5=反水订单,6=彩金订单,7=转盘奖励,8=任务奖励,9=佣金奖励,10=新人奖励,11=红包奖励)
     * {@link OrderTypeEnum}1.升级奖励
     * 2.周奖励
     * 3.月奖励
     * 4.还有就是这个产品模式，充值奖励和亏损奖励
     */
    @ApiModelProperty("订单类型")
    @TableField("order_type")
    @Excel(name = "订单类型", readConverterExp = "1人工上下分订单2充值订单3提现订单4游戏订单5反水订单6彩金订单7转盘奖励8任务奖励9佣金奖励10新人奖励11红包奖励" +
            "12转账订单13升级奖励14周奖励15月奖励16充值奖励17亏损奖励")
    private Integer orderType;

    /**
     * 地址类型(1 内部地址, 2 外部地址)
     */
    @ApiModelProperty("地址类型(1 内部地址, 2 外部地址)")
    @TableField("exchange_type")
    @Excel(name = "地址类型", readConverterExp = "1=内部地址,2=外部地址")
    private Integer exchangeType;

    /**
     * 收支类型(1 收入, 2 支出)
     */
    @ApiModelProperty("收支类型(1收入, 2支出)")
    @TableField("account_type")
    @Excel(name = "收支类型", readConverterExp = "1=收入,2=支出")
    private Integer accountType;

    /**
     * 帐变类型(1=人工上分,2=人工下分,3=用户充值,4=用户提现,5=提现拒绝,6=提现失败,7=审核拒绝,8=游戏投注,9=游戏返奖,
     * 10=游戏取消,11=反水奖励,12=彩金奖励,13=转盘奖励,14=任务奖励,15=佣金奖励,16=新人奖励,17=红包领取)
     * {@link AmountChangeTypeEnum}
     */
    @ApiModelProperty("帐变类型(1.人工上分 2 人工下分3用户充值 4用户提现 5 提现拒绝 6提现失败 7.审核拒绝 8 游戏投注9.游戏返奖 10 .游戏取消,投注退回11反水奖励 12 彩金奖励 13 转盘奖励 14 任务奖励 15 佣金奖励 16 新人奖励17 红包领取 18 钱包转入 19 钱包转出20 代理工资领取) 21赠送彩金 22接收彩  )")
    @TableField("type")
    @Excel(name = "帐变类型", readConverterExp = "1=人工上分,2=人工下分,3=用户充值,4=用户提现,5=提现拒绝,6=提现失败,7=审核拒绝,8=游戏投注,9=游戏返奖,10=游戏取消,投注退回,11=反水奖励,12=彩金奖励,13=转盘奖励,14=任务奖励,15=佣金奖励,16=新人奖励,17=红包领取,18=钱包转入,19=钱包转出,20=代理工资领取,21=赠送彩金" +
            ",22=接收彩金")
    private Integer type;

    /**
     * 变更金额
     */
    @ApiModelProperty("变更金额")
    @TableField("amount")
    @Excel(name = "变更金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    /**
     * 变更前金额
     */
    @ApiModelProperty("变更前金额")
    @TableField("old_amount")
    @Excel(name = "变更前金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal oldAmount;

    /**
     * 变更后金额
     */
    @ApiModelProperty("变更后金额")
    @TableField("new_amount")
    @Excel(name = "变更后金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal newAmount;

    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    @TableField("operator")
    @Excel(name = "操作人")
    private String operator;

    /**
     * 变更时间
     */
    @ApiModelProperty("变更时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "变更时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    @ApiModelProperty("变更时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    @ApiModelProperty("是否是查询当天数据 0.当天 1.非当天")
    @TableField(exist = false)
    private Integer todayFag = 0;

    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @TableField(exist = false)
    private Long updateVersion;
}
