package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("商户余额查询结果")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantBalanceVO {

    /**
     * 商户ID
     */
    @ApiModelProperty("商户ID")
    private Long merchantId;

    /**
     * 商户名称
     */
    @ApiModelProperty("商户名称")
    private String merchantName;

    /**
     * 商户编码
     */
    @ApiModelProperty("商户编码")
    private String code;

    /**
     * 商户余额
     */
    @ApiModelProperty("商户余额")
    private String balance;
}
