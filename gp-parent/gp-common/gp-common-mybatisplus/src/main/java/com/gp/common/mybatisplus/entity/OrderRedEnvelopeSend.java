package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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

/**
 * 红包发送对象 t_order_red_envelope_send
 *
 * @author axing
 * @date 2024-12-26
 */
@ApiModel("红包发送")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_red_envelope_send")
public class OrderRedEnvelopeSend extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderRedEnvelopeSend";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 红包订单号
     */
    @ApiModelProperty("红包订单号")
    @TableField("order_no")
    @Excel(name = "红包订单号")
    private String orderNo;

    /**
     * 红包方式(1 私人红包,2 群红包,3 新人红包)
     */
    @ApiModelProperty("红包方式(1 私人红包,2 群红包,3 新人红包 4 超级红包)")
    @TableField("red_type")
    @Excel(name = "红包方式", readConverterExp = "1=私人红包,2=群红包,3=新人红包 4 超级红包")
    private Integer redType;

    /**
     * 红包类型(1 普通红包,2 等额红包)
     */
    @ApiModelProperty("红包类型(1 普通红包,2 等额红包)")
    @TableField("type")
    @Excel(name = "红包类型", readConverterExp = "1=普通红包,2=等额红包")
    private Integer type;

    /**
     * 红包封面
     */
    @ApiModelProperty("红包封面")
    @TableField("red_cover")
    @Excel(name = "红包封面")
    private String redCover;

    /**
     * 缩略图
     */
    @ApiModelProperty("缩略图")
    @TableField("thumbnail")
    @Excel(name = "缩略图")
    private String thumbnail;

    /**
     * 红包列表在redis中的标识
     */
    @ApiModelProperty("红包列表在redis中的标识")
    @TableField("redis_key")
    @Excel(name = "红包列表在redis中的标识")
    private String redisKey;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    @TableField("content")
    @Excel(name = "内容")
    private String content;

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
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 等级要求达到多少 */
    @ApiModelProperty("等级要求达到多少")
    @TableField("level")
    @Excel(name = "等级要求达到多少")
    private Integer level;

    /** 游戏类型code */
    @ApiModelProperty("游戏类型code")
    @TableField("type_code_bet")
    @Excel(name = "游戏类型code")
    private String typeCodeBet;

    /** 每日打码量多少 */
    @ApiModelProperty("每日打码量多少")
    @TableField("day_bet_amount")
    @Excel(name = "每日打码量多少")
    private BigDecimal dayBetAmount;

    /** 充值类型 1 每日 2 永久 3 周 4 月 */
    @ApiModelProperty("充值类型 1 每日 2 永久 3 周 4 月")
    @TableField("recharge_type")
    @Excel(name = "充值类型 1 每日 2 永久 3 周 4 月")
    private Integer rechargeType;

    /** 最小充值金额 */
    @ApiModelProperty("最小充值金额")
    @TableField("min_recharge_amount")
    @Excel(name = "最小充值金额")
    private BigDecimal minRechargeAmount;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 用户tgID
     */
    @ApiModelProperty("用户tgID")
    @TableField("user_tg_id")
    @Excel(name = "用户tgID")
    private Long userTgId;

    /**
     * 红包个数
     */
    @ApiModelProperty("红包个数")
    @TableField("num")
    @Excel(name = "红包个数")
    private Integer num;

    /**
     * 剩余红包个数
     */
    @ApiModelProperty("剩余红包个数")
    @TableField("last_num")
    @Excel(name = "剩余红包个数")
    private Integer lastNum;

    /**
     * 剩余金额
     */
    @ApiModelProperty("剩余金额")
    @TableField("last_amount")
    @Excel(name = "剩余金额")
    private BigDecimal lastAmount;

    /**
     * 提现打码量比例
     */
    @ApiModelProperty("提现打码量比例")
    @TableField("withdraw_bonus_ratio")
    @Excel(name = "提现打码量比例")
    private BigDecimal withdrawBonusRatio;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 红包订单状态(0 创建, 1 进行中, 2 已结束)
     */
    @ApiModelProperty("红包订单状态(0 创建, 1 进行中, 2 已结束)")
    @TableField("status")
    @Excel(name = "红包订单状态", readConverterExp = "0=创建,1=进行中,2=已结束")
    private Integer status;

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
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    @Excel(name = "结束时间")
    private Date endTime;
    @ApiModelProperty("结束时间数组")
    @TableField(exist = false)
    private String[] endTimes;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;
}
