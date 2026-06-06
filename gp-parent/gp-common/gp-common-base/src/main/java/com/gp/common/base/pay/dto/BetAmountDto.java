package com.gp.common.base.pay.dto;

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
public class BetAmountDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 金额
     */
    @ApiModelProperty(value="选择的充值价格id")
    private Long id;
    /**
     * 金额
     */
    @ApiModelProperty(value="0是myPay 1是法币")
    private Integer type = 0;
    /**
     * 金额
     */
    @ApiModelProperty(value="金额")
    private BigDecimal amount;

    /**
     * 金额
     */
    @ApiModelProperty(value="语言 (   巴西  pt_BR  越南 vi_VN)")
    private String lanKey;

}
