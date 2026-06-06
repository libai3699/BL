package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.mybatisplus.dto.ExtParam;
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
 * 商户对象 t_merchant_pay
 *
 * @author axing
 * @date 2025-05-22
 */
@ApiModel("商户")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_merchant_pay")
public class MerchantPay extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:MerchantPay";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户id 用于扩展 这个以后如果支出多商户的话
     */
    @ApiModelProperty("商户id 用于扩展 这个以后如果支出多商户的话")
    @TableField("merchant_id")
    @Excel(name = "商户id")
    private Long merchantId;

    /**
     * 商户名称
     */
    @ApiModelProperty("商户名称")
    @TableField("name")
    @Excel(name = "商户名称")
    private String name;

//    /**
//     * 支付类型变
//     */
//    @ApiModelProperty("支付类型编码")
//    @TableField(value = "pay_code", fill = FieldFill.INSERT_UPDATE)
//    @Excel(name = "支付类型编码")
//    private String payCode;

    /**
     * 支付code
     */
    @ApiModelProperty("支付code")
    @TableField("code")
    @Excel(name = "支付code")
    private String code;

    /**
     * 商户头像
     */
    @ApiModelProperty("商户头像")
    @TableField("avatar")
    //    @Excel(name = "商户头像")
    private String avatar;

    /**
     * 充值区间
     */
    @ApiModelProperty("充值区间")
    @TableField("recharge_range")
    @Excel(name = "充值区间")
    private String rechargeRange;

    /**
     * 提现区间
     */
    @ApiModelProperty("提现区间")
    @TableField("withdraw_range")
    @Excel(name = "提现区间")
    private String withdrawRange;

    /**
     * 与U的汇率
     */
    @ApiModelProperty("与U的汇率")
    @TableField("rate")
    @Excel(name = "与U的汇率")
    private BigDecimal rate;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency")
    @Excel(name = "币种")
    private String currency;

    /**
     * 商户状态 0 冻结 1 正常
     */
    @ApiModelProperty("商户状态 0 冻结 1 正常")
    @TableField("status")
    @Excel(name = "商户状态", readConverterExp = "0=冻结,1=正常")
    private Integer status;

    /**
     * 用于添加支付参数
     */
    @ApiModelProperty("用于添加支付参数")
    @TableField("param_str")
    @Excel(name = "用于添加支付参数")
    private String paramStr;

    /**
     * 回调地址
     */
    @ApiModelProperty("回调地址")
    @TableField("call_back_ip")
    @Excel(name = "回调地址")
    private String callBackIp;

    /**
     * 0 不显示 1 显示
     */
    @ApiModelProperty("是否充值")
    @TableField("is_rechange")
    @Excel(name = "是否充值", readConverterExp = "0=否,1=是")
    private Integer isRechange;

    /**
     * 是否财务通知 0 否,1 是
     */
    @ApiModelProperty("是否财务通知 0 否,1 是")
    @TableField("is_finance")
    @Excel(name = "是否财务通知", readConverterExp = "0=否,1=是")
    private Integer isFinance;

    /**
     * 0 不显示 1显示
     */
    @ApiModelProperty("是否提现")
    @TableField("is_withdraw")
    @Excel(name = "是否提现", readConverterExp = "0=否,1=是")
    private Integer isWithdraw;

    /**
     * 充值扩展参数(给前端来传)
     */
    @ApiModelProperty("充值扩展参数(给前端来传)")
    @TableField("ext_param_recharge")
    @Excel(name = "充值扩展参数(给前端来传)")
    private String extParamRecharge;

    @ApiModelProperty("充值扩展参数(给前端来传)")
    @TableField(exist = false)
    @Excel(name = "充值扩展参数(给前端来传)")
    private List<ExtParam> extParamRg;
    /**
     * 充值提示
     */
    @ApiModelProperty("充值提示")
    @TableField("recharge_notice")
    @Excel(name = "充值提示")
    private String rechargeNotice;

    /**
     * 提现扩展参数(给前端来传)
     */
    @ApiModelProperty("提现扩展参数(给前端来传)")
    @TableField("ext_param_withdraw")
    @Excel(name = "提现扩展参数(给前端来传)")
    private String extParamWithdraw;

    @ApiModelProperty("提现扩展参数(给前端来传)")
    @TableField(exist = false)
    @Excel(name = "提现扩展参数(给前端来传)")
    private List<ExtParam> extParamWd;
    /**
     * 提现提示
     */
    @ApiModelProperty("提现提示")
    @TableField("withdraw_notice")
    @Excel(name = "提现提示")
    private String withdrawNotice;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 排序
     */
    @ApiModelProperty("排序  ")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

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
     * 商户余额
     */
    @ApiModelProperty("商户余额")
    @TableField(exist = false)
    private String balance;
}
