package com.gp.common.mybatisplus.pay.mpay.param;


import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantPayDto extends WalletBase {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;

    //参数
    @ApiModelProperty("商户订单号")
    private String merchantOrderNo;

    /** 附加参数 */
    @ApiModelProperty("附加参数")
    private String attch;
    @ApiModelProperty("备注")
    private String remark;
    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    @ApiModelProperty("金额")
    private BigDecimal amount;


    @ApiModelProperty("支付的语言")
    private  String lanKey;
}
