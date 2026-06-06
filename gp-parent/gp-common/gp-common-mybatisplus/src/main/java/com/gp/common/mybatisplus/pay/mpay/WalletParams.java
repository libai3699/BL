package com.gp.common.mybatisplus.pay.mpay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单返回的详情
 */
@Data
public class WalletParams {
    /** 名称 */
    @ApiModelProperty("名称")
    private String key;

    /** 名称 */
    @ApiModelProperty("值")
    private String value;
    /** 请求域名 */
    @ApiModelProperty("name")
    private String name;


}
