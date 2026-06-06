package com.gp.feign.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 红包发送对象 t_order_red_envelope_send
 *
 * @author axing
 * @date 2024-12-25
 */
@ApiModel("红包发送")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedEnvelope implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 红包订单号
     */
    @ApiModelProperty("红包订单号")
    private String orderNo;

    /**
     * 红包方式(1 私人红包,2 群红包,3 新人红包)
     */
    @ApiModelProperty("红包方式(1 私人红包,2 群红包,3 新人红包)")
    private Integer redType;

    /**
     * 红包类型(1 普通红包,2 等额红包)
     */
    @ApiModelProperty("红包类型(1 普通红包,2 等额红包)")
    private Integer type;

    /**
     * 红包封面
     */
    @ApiModelProperty("红包封面")
    private String redCover;

    /**
     * 缩略图
     */
    @ApiModelProperty("缩略图")
    private String thumbnail;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    private String content;

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    /**
     * 币种ID
     */
    @ApiModelProperty("币种ID")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty("链名称")
    private String chainTag;

    /**
     * 币加链名称
     */
    @ApiModelProperty("币加链名称")
    private String itemName;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 红包个数
     */
    @ApiModelProperty("红包个数")
    private Integer num;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * userTgId
     */
    @ApiModelProperty("userTgId")
    private Long userTgId;

    /**
     * lanKey
     */
    @ApiModelProperty("lanKey")
    private String lanKey;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private Date endTime;

}
