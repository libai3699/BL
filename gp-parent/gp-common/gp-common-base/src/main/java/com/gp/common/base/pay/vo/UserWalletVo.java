package com.gp.common.base.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author axing
 * @version 1.0
 * @date 2024/5/7/007 11:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 币种
     */
    @ApiModelProperty(value="币种")
    private String currency;

    /**
     * 币种
     */
    @ApiModelProperty(value="币种图标")
    private String icon;

    @ApiModelProperty(value="黑色币种图标")
    private String blackIcon;

    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    /** 币id */
    @ApiModelProperty("币id")
    private Integer itemId;

    /** 链名称 */
    @ApiModelProperty("链名称")
    private String chainTag;
    /** 金额 */
    @ApiModelProperty("金额")
    private BigDecimal amount;

    /** 地址 */
    @ApiModelProperty("地址")
    private String maskAddr;

    /** 是否维护 */
    @ApiModelProperty("是否维护(0 否, 1是)")
    private Integer isWh;
}
