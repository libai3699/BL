package com.gp.common.base.pay.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPayRechargeDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "价格列表id")
    private Long id;
    @ApiModelProperty(value = "自定义价格")
    private BigDecimal amount;
}
