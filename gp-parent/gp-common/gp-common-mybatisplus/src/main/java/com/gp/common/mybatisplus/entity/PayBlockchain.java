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

/**
 * 冷钱包对象 t_pay_blockchain
 *
 * @author axing
 * @date 2026-01-29
 */
@ApiModel("冷钱包")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_pay_blockchain")
public class PayBlockchain extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayBlockchain";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    /**
     * 标签
     */
    @ApiModelProperty("标签")
    @TableField("icon")
    @Excel(name = "标签")
    private String icon;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain")
    @Excel(name = "链名称")
    private String chain;

    /**
     * 币名称
     */
    @ApiModelProperty("币名称")
    @TableField("item")
    @Excel(name = "币名称")
    private String item;

    /**
     * 平台汇率
     */
    @ApiModelProperty("平台汇率")
    @TableField("platform_rate")
    @Excel(name = "平台汇率")
    private BigDecimal platformRate;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    @TableField("addr")
    @Excel(name = "地址")
    private String addr;

    /**
     * 充值提示
     */
    @ApiModelProperty("充值提示")
    @TableField("notice")
    @Excel(name = "充值提示")
    private String notice;

    /**
     * 提现提示
     */
    @ApiModelProperty("提现提示")
    @TableField("withdraw_notice")
    @Excel(name = "提现提示")
    private String withdrawNotice;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /**
     * 是否充值(0 否, 1 是)
     */
    @ApiModelProperty("是否充值(0 否, 1 是)")
    @TableField("is_recharge")
    @Excel(name = "是否充值", readConverterExp = "0=否,1=是")
    private Integer isRecharge;

    /**
     * 是否提现(0 否, 1 是)
     */
    @ApiModelProperty("是否提现(0 否, 1 是)")
    @TableField("is_withdraw")
    @Excel(name = "是否提现", readConverterExp = "0=否,1=是")
    private Integer isWithdraw;

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
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

}
