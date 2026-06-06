package com.gp.common.mybatisplus.pay.mpay;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author axing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletCreateOrderResult {

    @ApiModelProperty("上游订单号")
    private String upOrderNo;

    @ApiModelProperty("支付地址")
    private String url;

    @ApiModelProperty("上游返回信息")
    private String upInfo;

    @ApiModelProperty("是不是表单")
    private boolean form = false;

}
