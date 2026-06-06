package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UpAndDownScoreParam implements Serializable {

    /**
     * 币种id
     */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;

    /** 类型(1 上分, 2 下分) */
//    @ApiModelProperty("类型(1 上分, 2 下分)")
//    private Integer orderType;

    /**
     * 金额
     */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /**
     * 彩金(上分时使用)
     */
    @ApiModelProperty("彩金(上分时使用)")
    private BigDecimal bonusAmount;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人")
    private String createBy;

    /**
     * 下分类型(1.真实提现;2.扣除积分)
     */
    @ApiModelProperty("下分类型(1.真实提现;2.扣除积分)")
    private Integer lowerSubtype;

    /**
     * 交易图片
     */
    @ApiModelProperty("交易图片")
    private String tradeImg;

}
