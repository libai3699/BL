package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.*;
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

/**
 * 【请填写功能名称】对象 t_pay_channel
 *
 * @author axing
 * @date 2026-01-09
 */
@ApiModel("【通道】")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_pay_channel")
public class PayChannel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayChannel";

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 通道名称
     */
    @ApiModelProperty("通道名称")
    @TableField("name")
    @Excel(name = "通道名称")
    private String name;

    /**
     * 通道类型(1 充值, 2 提现)
     */
    @ApiModelProperty("通道类型(1 充值, 2 提现)")
    @TableField("type")
    @Excel(name = "通道类型", readConverterExp = "1=充值,2=提现")
    private Integer type;

    /**
     * 支付方式编码
     */
    @ApiModelProperty("支付方式编码")
    @TableField("pay_method")
    @Excel(name = "支付方式编码")
    private String payMethod;

    /**
     * 支付属性 0内联链接 1外跳链接
     */
    @ApiModelProperty("支付属性 0 外跳链接 1内跳链接")
    @TableField("pay_attr")
    @Excel(name = "支付属性", readConverterExp = "0=内联链接,1=外跳链接")
    private Integer payAttr;

    /**
     * 与本平台货币的汇率
     */
    @ApiModelProperty("与本平台货币的汇率")
    @TableField("rate")
    @Excel(name = "与本平台货币的汇率", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rate;

    /**
     * 失败次数
     */
    @ApiModelProperty("失败次数")
    @TableField("fail_num")
    @Excel(name = "失败次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer failNum;

    /**
     * 成功次数
     */
    @ApiModelProperty("成功次数")
    @TableField("success_num")
    @Excel(name = "成功次数", cellType = Excel.ColumnType.NUMERIC)
    private Integer successNum;

    /**
     * 累计成功金额
     */
    @ApiModelProperty("累计成功金额")
    @TableField("total_success_money")
    @Excel(name = "累计成功金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalSuccessMoney;

    /**
     * 充值最低
     */
    @ApiModelProperty("充值最低")
    @TableField("recharge_min")
    @Excel(name = "充值最低", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rechargeMin;

    /**
     * 充值最高
     */
    @ApiModelProperty("充值最高")
    @TableField("recharge_max")
    @Excel(name = "充值最高", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal rechargeMax;

    /**
     * 状态(1启用0停用)
     */
    @ApiModelProperty("状态(1启用0停用)")
    @TableField("status")
    @Excel(name = "状态", readConverterExp = "0=停用,1=启用")
    private Long status;

    /**
     * 是否允许回调(默认1 允许 0不允许)
     */
    @ApiModelProperty("是否允许回调(默认1 允许 0不允许)")
    @TableField("is_can_callback")
    @Excel(name = "是否允许回调", readConverterExp = "0=不允许,1=允许")
    private Long isCanCallback;

    /**
     * 排序号
     */
    @ApiModelProperty("排序号")
    @TableField("indexes")
    @Excel(name = "排序号", cellType = Excel.ColumnType.NUMERIC)
    private Integer indexes;

    /**
     * 开放层级最小等级
     */
    @ApiModelProperty("开放层级最小等级")
    @TableField("open_level")
    @Excel(name = "开放层级最小等级", cellType = Excel.ColumnType.NUMERIC)
    private Integer openLevel;

    /**
     * 开放层级最大等级
     */
    @ApiModelProperty("开放层级最大等级")
    @TableField("open_level_max")
    @Excel(name = "开放层级最大等级", cellType = Excel.ColumnType.NUMERIC)
    private Integer openLevelMax;

    /**
     * 支付平台编号(支付商户ID)
     */
    @ApiModelProperty("支付商户ID")
    @TableField("pay_platform_id")
    @Excel(name = "支付平台编号")
    private Long payPlatformId;

    /**
     * 支付类型编号(支付类型配置ID)
     */
    @ApiModelProperty("支付类型id")
    @TableField("pay_type_id")
    @Excel(name = "支付类型编号")
    private Long payTypeId;

    /**
     * 类型编码
     */
    @ApiModelProperty("支付类型编码")
    @TableField(exist = false)
    private String code;

    /**
     * 优惠比例
     */
    @ApiModelProperty("优惠比例")
    @TableField("discount_bill")
    @Excel(name = "优惠比例", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal discountBill;

    /**
     * 快捷金额
     */
    @ApiModelProperty("快捷金额")
    @TableField("quick_amount")
    @Excel(name = "快捷金额")
    private String quickAmount;

    /**
     * 输入类型 (自定义金额+快捷金额1 仅快捷金额0)
     */
    @ApiModelProperty("输入类型 (自定义金额+快捷金额1 仅快捷金额0)")
    @TableField("input_type")
    @Excel(name = "输入类型", readConverterExp = "0=仅快捷金额,1=自定义金额+快捷金额")
    private Integer inputType;

    /**
     * 备注提示
     */
    @ApiModelProperty("备注提示")
    @TableField(value = "remark", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "备注提示")
    private String remark;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("creator")
    @Excel(name = "创建人")
    private String creator;

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
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("updator")
    @Excel(name = "修改人")
    private String updator;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /**
     * 通道费率
     */
    @ApiModelProperty("通道费率")
    @TableField("pay_rate")
    @Excel(name = "通道费率", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal payRate;

}
