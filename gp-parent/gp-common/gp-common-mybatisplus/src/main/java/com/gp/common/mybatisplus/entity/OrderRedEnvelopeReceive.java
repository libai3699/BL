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

/**
 * 红包接收对象 t_order_red_envelope_receive
 *
 * @author axing
 * @date 2024-12-26
 */
@ApiModel("红包接收")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_order_red_envelope_receive")
public class OrderRedEnvelopeReceive extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OrderRedEnvelopeReceive";

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
    @ApiModelProperty("红包方式(1 私人红包,2 群红包,3 新人红包)")
    @TableField("red_type")
    @Excel(name = "红包方式", readConverterExp = "1=私人红包,2=群红包,3=新人红包")
    private Integer redType;

    /**
     * 红包类型(1 普通红包,2 等额红包)
     */
    @ApiModelProperty("红包类型(1 普通红包,2 等额红包)")
    @TableField("type")
    @Excel(name = "红包类型", readConverterExp = "1=普通红包,2=等额红包")
    private Integer type;

    /**
     * 关联的发送订单号
     */
    @ApiModelProperty("关联的发送订单号")
    @TableField("link_order_no")
    @Excel(name = "关联的发送订单号")
    private String linkOrderNo;

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
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    @TableField("user_name")
    @Excel(name = "用户名称")
    private String userName;

    /**
     * 用户tgID
     */
    @ApiModelProperty("用户tgID")
    @TableField("user_tg_id")
    @Excel(name = "用户tgID")
    private Long userTgId;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    @TableField("amount")
    @Excel(name = "金额")
    private BigDecimal amount;

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

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;
}
