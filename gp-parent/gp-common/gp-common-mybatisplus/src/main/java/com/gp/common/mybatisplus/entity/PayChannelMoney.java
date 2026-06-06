package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 【请填写功能名称】对象 t_pay_channel_money
 *
 * @author axing
 * @date 2026-01-09
 */
@ApiModel("【通道快捷金额】")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_pay_channel_money")
public class PayChannelMoney extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PayChannelMoney";

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 通道金额
     */
    @ApiModelProperty("通道金额")
    @TableField("money")
    @Excel(name = "通道金额", cellType = Excel.ColumnType.NUMERIC)
    private Long money;

    /**
     * 通道ID
     */
    @ApiModelProperty("通道ID")
    @TableField("channel_id")
    @Excel(name = "通道ID", cellType = Excel.ColumnType.NUMERIC)
    private Long channelId;

    /**
     * 通道费率
     */
    @ApiModelProperty("通道费率")
    @TableField("channel_pay_rate")
    @Excel(name = "通道费率", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal channelPayRate;

    /**
     * 支付类型CODE
     */
    @ApiModelProperty("支付类型CODE")
    @TableField("type_code")
    @Excel(name = "支付类型CODE")
    private String typeCode;

    /**
     * 最低层级
     */
    @ApiModelProperty("最低层级")
    @TableField("open_level_min")
    @Excel(name = "最低层级", cellType = Excel.ColumnType.NUMERIC)
    private Integer openLevelMin;

    /**
     * 最高层级
     */
    @ApiModelProperty("最高层级")
    @TableField("open_level_max")
    @Excel(name = "最高层级", cellType = Excel.ColumnType.NUMERIC)
    private Integer openLevelMax;

}
