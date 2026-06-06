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
 * 人工上下分订单对象 t_order_person
 *
 * @author axing
 * @date 2024-05-18
 */
@ApiModel("人工上下分订单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_person")
public class OrderPerson extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderPerson";

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
     * 订单类型。库表 {@code order_type} 仅两种：1 上分、2 下分，不存在 3。
     * 列表/导出查询时若传 3，仅表示「筛选 bonus_amount 非空且非 0」，由 Service 转成内部条件，不会当作真实类型写入数据库。
     */
    @ApiModelProperty(value = "类型：库中仅 1 上分、2 下分。列表查询传 3=只看待赠送彩金订单（虚拟条件，非库类型）", example = "1")
    @TableField("order_type")
    @Excel(name = "类型", readConverterExp = "1=上分,2=下分")
    private Integer orderType;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    /**
     * 彩金(上分时使用)
     */
    @ApiModelProperty("彩金(上分时使用)")
    @TableField("bonus_amount")
    @Excel(name = "彩金(上分时使用)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal bonusAmount;

    /**
     * 地址类型(1内部地址, 2外部地址)
     */
    @ApiModelProperty("地址类型(1内部地址, 2外部地址)")
    @TableField("exchange_type")
    private Integer exchangeType;

    /**
     * 交易图片
     */
    @ApiModelProperty("交易图片")
    @TableField("trade_img")
    @Excel(name = "交易图片")
    private String tradeImg;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 目标地址
     */
    @ApiModelProperty("目标地址")
    @TableField("address")
    @Excel(name = "目标地址")
    private String address;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 最小金额
     */
    @ApiModelProperty("最小金额")
    @TableField(exist = false)
    private BigDecimal leasAmounts;

    /**
     * 最大金额
     */
    @ApiModelProperty("最大金额")
    @TableField(exist = false)
    private BigDecimal maxAmounts;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /**
     * 下分类型(1.真实提现;2.扣除积分)
     */
    @ApiModelProperty("下分类型(1.真实提现;2.扣除积分)")
    @TableField("lower_subtype")
    @Excel(name = "下分类型", readConverterExp = "1=真实提现,2=扣除积分")
    private Integer lowerSubtype;


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
//    @TableField(exist = false)
    @TableField("shareholder_id")
    @Excel(name = "股东ID")
    private Long shareholderId;


    /**
     * 股东名称
     */
    @ApiModelProperty("股东名称")
    @TableField(exist = false)
    @Excel(name = "股东名称")
    private String shareholderName;

    @ApiModelProperty("渠道IdList")
    @TableField(exist = false)
    private List<Long> channelIdList;

    /**
     * 列表查询用：true 表示仅保留 bonus_amount 非空且非 0。前端传虚拟 orderType=3 时由 OrderPersonController 置位，非表字段。
     */
    @ApiModelProperty(hidden = true)
    @TableField(exist = false)
    private Boolean queryNonZeroBonus;

    /**
     * 搜索类型(1-彩金(amount=0) 2-充值(amount!=0))
     */
    @ApiModelProperty("搜索类型(1-彩金 2-充值)")
    @TableField(exist = false)
    private Integer searchType;

    /**
     * 列表合计行：当前筛选条件下全量订单笔数（与分页 list 条件一致，非本页条数）
     */
    @ApiModelProperty("订单总数")
    @TableField(exist = false)
    private Long summaryOrderNum;

    /**
     * 列表合计行：当前筛选条件下金额合计
     */
    @ApiModelProperty("订单金额合计")
    @TableField(exist = false)
    private BigDecimal summaryAmountTotal;

    /**
     * 列表合计行：当前筛选条件下彩金合计
     */
    @ApiModelProperty("订单彩金合计")
    @TableField(exist = false)
    private BigDecimal summaryBonusTotal;

}
