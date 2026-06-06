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
 * 领取返水返佣记录对象 t_order_receive_rebate
 *
 * @author axing
 * @date 2024-05-17
 */
@ApiModel("领取返水返佣记录")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_receive_rebate")
public class OrderReceiveRebate extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderReceiveRebate";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 领取返水订单号或领取返佣订单号
     */
    @ApiModelProperty("领取返水订单号或领取返佣订单号")
    @TableField("order_no")
    @Excel(name = "领取返水订单号或领取返佣订单号")
    private String orderNo;

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
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency_id")
    @Excel(name = "币种")
    private Integer currencyId;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField(exist = false)
    @Excel(name = "币种")
    private String currencyName;

    /**
     * mask币种id
     */
    @ApiModelProperty("mask币种id")
    @TableField("item_id")
    @Excel(name = "mask币种id")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    @TableField("chain_tag")
    @Excel(name = "链名称")
    private String chainTag;

    /**
     * mask币加链名称
     */
    @ApiModelProperty("mask币加链名称")
    @TableField("item_name")
    @Excel(name = "mask币加链名称")
    private String itemName;

    /**
     * 返水金额或返佣金额
     */
    @ApiModelProperty("返水金额或返佣金额")
    @TableField("amount")
    @Excel(name = "返水金额或返佣金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amount;

    /**
     * 记录类型,1:返水;2:返佣;
     */
    @ApiModelProperty("记录类型,1:返水;2:返佣;3=代理工资")
    @TableField("record_type")
    @Excel(name = "记录类型", readConverterExp = "1=返水,2=返佣,3=代理工资")
    private Integer recordType;

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

}
