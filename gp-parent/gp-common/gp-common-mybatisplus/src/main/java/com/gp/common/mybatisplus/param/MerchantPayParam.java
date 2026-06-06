package com.gp.common.mybatisplus.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MerchantPayParam {

    /**
     * 支付商户ID
     */
    @ApiModelProperty("支付商户ID")
    private Long merchantId;

    /**
     * 支付通道id
     */
    @ApiModelProperty("支付通道id")
    private Long payChannelId;

    /**
     * 通道支付类型编号
     */
    @ApiModelProperty("通道支付类型编号")
    private Long payTypeId;

    /**
     * 下单时间数组
     */
    @ApiModelProperty("下单时间数组")
    private String[] createTimes;

    /**
     * 到账时间数组
     */
    @ApiModelProperty("到账时间数组")
    private String[] withdrawTimes;

    /**
     * 支付时间数组
     */
    @ApiModelProperty("支付时间数组")
    private String[] payTimes;

    /**
     * 币种id
     */
    @ApiModelProperty(value = "币种id")
    private Integer currencyId;
}
