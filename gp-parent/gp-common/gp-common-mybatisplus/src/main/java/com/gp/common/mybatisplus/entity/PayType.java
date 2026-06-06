package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
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
import java.util.Map;

/**
 * 支付类型对象 t_pay_type
 *
 * @author axing
 * @date 2025-06-12
 */
@ApiModel("支付类型")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_pay_type", autoResultMap = true)
public class PayType extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayType";

    /** 支付类型id */
    @ApiModelProperty("支付类型id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "支付类型id")
     private Integer id;

    /** 类型编码 */
    @ApiModelProperty("类型编码")
    @TableField("code")
    @Excel(name = "类型编码")
    private String code;

    /** 类型名称 */
    @ApiModelProperty("类型名称")
    @TableField("name")
    @Excel(name = "类型名称")
    private String name;

    /** 类型头像 */
    @ApiModelProperty("类型头像")
    @TableField("avatar")
    @Excel(name = "类型头像")
    private String avatar;

    /** 币种 */
    @ApiModelProperty("币种")
    @TableField("currency")
    @Excel(name = "币种")
    private String currency;

    /** 与本平台默认货币的汇率 */
    @ApiModelProperty("与本平台默认货币的汇率")
    @TableField("exchange_rate")
    @Excel(name = "与本平台默认货币的汇率")
    private BigDecimal exchangeRate;

    /** 提现是否需要绑定卡 0 不需要, 1需要 */
    @ApiModelProperty("提现是否需要绑定卡 0 不需要, 1需要")
    @TableField("bind_type")
    @Excel(name = "提现是否需要绑定卡 0 不需要, 1需要")
    private Integer bindType;

    /** 充值是否需要绑定卡 0 不需要, 1需要 */
    @ApiModelProperty("充值是否需要绑定卡 0 不需要, 1需要")
    @TableField("recharge_bind_type")
    @Excel(name = "充值是否需要绑定卡 0 不需要, 1需要")
    private Integer rechargeBindType;

    /** 提现绑定信息(前台那里添加银行卡的参数) */
    @ApiModelProperty("提现绑定信息(前台那里添加银行卡的参数)")
    @TableField(value = "bind_format", typeHandler = FastjsonTypeHandler.class)
    @Excel(name = "提现绑定信息(前台那里添加银行卡的参数)")
    private Map<String, Object> bindFormat;

    /** 充值绑定信息(前台那里添加银行卡的参数) */
    @ApiModelProperty("充值绑定信息(前台那里添加银行卡的参数)")
    @TableField(value = "recharge_bind_format", typeHandler = FastjsonTypeHandler.class)
    @Excel(name = "充值绑定信息(前台那里添加银行卡的参数)")
    private Map<String, Object> rechargeBindFormat;

    /** 状态 0 冻结 1 正常 */
    @ApiModelProperty("状态 0 冻结 1 正常")
    @TableField("status")
    @Excel(name = "状态 0 冻结 1 正常")
    private Integer status;

    /** 是否推荐(1是0否) */
    @ApiModelProperty("是否推荐(1是0否)")
    @TableField("is_recommend")
    @Excel(name = "是否推荐(1是0否)")
    private Integer isRecommend;

    /** 开放等级 */
    @ApiModelProperty("开放等级")
    @TableField("open_level")
    @Excel(name = "开放等级")
    private Integer openLevel;

    /** 赠送比例 */
    @ApiModelProperty("赠送比例")
    @TableField("gift_ratio")
    @Excel(name = "赠送比例")
    private BigDecimal giftRatio;

    /** 0 不显示 1 显示 */
    @ApiModelProperty("充值是否显示0 不显示 1 显示")
    @TableField("is_rechange")
    @Excel(name = "充值是否显示")
    private Integer isRechange;

    /** 充值扩展参数(给前端来传) */
    @ApiModelProperty("充值扩展参数(给前端来传)")
    @TableField("ext_param_recharge")
    @Excel(name = "充值扩展参数(给前端来传)")
    private String extParamRecharge;

    /** 充值提示 */
    @ApiModelProperty("充值提示")
    @TableField("recharge_notice")
    @Excel(name = "充值提示")
    private String rechargeNotice;

    /** 0 不显示 1 显示 */
    @ApiModelProperty("提现是否显示0 不显示 1 显示")
    @TableField("is_withdraw")
    @Excel(name = "提现是否显示")
    private Integer isWithdraw;

    /** 提现扩展参数(给前端来传) */
    @ApiModelProperty("提现扩展参数(给前端来传)")
    @TableField("ext_param_withdraw")
    @Excel(name = "提现扩展参数(给前端来传)")
    private String extParamWithdraw;

    /** 提现提示 */
    @ApiModelProperty("提现提示")
    @TableField("withdraw_notice")
    @Excel(name = "提现提示")
    private String withdrawNotice;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /** 排序 */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

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

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;


}
