package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UpAndDownMerchantScoreParam implements Serializable {

    /**
     * 商户id
     */
    @ApiModelProperty("商户id")
    private Long merchantId;

    /**
     * 商户编码
     */
    @ApiModelProperty("商户编码")
    private String code;

    /**
     * 积分(usdt)
     */
    @ApiModelProperty("积分(usdt)")
    @NotNull(message = "积分不能为空")
    private BigDecimal amount;

    /**
     * 订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单 4.商户给用户上下分)
     */
    @ApiModelProperty("订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单 4.商户给用户上下分,5, 手动上分,6,充值上分,7,转盘抽取,8,活动奖励,9,反水,10,返奖 11 红包)")
    private Integer orderType;

    /**
     * 地址类型(1 内部地址, 2 外部地址)
     */
    @ApiModelProperty("地址类型(1 内部地址, 2 外部地址)")
    private Integer exchangeType;

    /**
     * 帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现 5 商户给用户人工上分, 6 商户给用户人工下分)
     */
    @ApiModelProperty("帐变类型(1 人工上分, 2 人工下分, 3 用户充值, 4 用户提现 5 商户给用户人工上分, 6 商户给用户人工下分,7 手动上分,8,充值上分,9,转盘抽取,10,活动奖励,11,反水,12,返奖 13 红包 )")
    private Integer type;

    /**
     * 操作人
     */
    private String username;
}
