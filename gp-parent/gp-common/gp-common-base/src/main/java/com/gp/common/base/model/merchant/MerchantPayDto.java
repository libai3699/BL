package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MerchantPayDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    @NotBlank(message = "appKey不能为空")
    private  String appKey;

    @ApiModelProperty("商户订单号")
    @NotBlank(message = "商户订单号不能为空")
    private String merchantOrderNo;

    /** 附加参数 */
    @ApiModelProperty("附加参数")

    private String attch;
    @ApiModelProperty("备注")
    private String remark;
    /** 币种id */
    @ApiModelProperty("币种id 6位usdt")
    @NotBlank(message = "币种id不能为空")
    private Integer currencyId;

    @ApiModelProperty("金额,小数位4位(币的数量,指多少个)")
    @NotBlank(message = "金额不能为空")
    private BigDecimal amount;

    //签名
    @ApiModelProperty("签名")
    @NotBlank(message = "签名不能为空")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳毫秒值13位")
    @NotBlank(message = "当前时间戳不能为空")
    private  Long timestamp;

    @ApiModelProperty("支付的语言")
    private  String lanKey;

}
