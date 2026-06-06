package com.gp.common.mybatisplus.pay.mpay.param;

import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 商户将余额提现到指定TG用户 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantSendMoneyDto extends WalletBase {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商户appKey")
    private String appKey;

    @ApiModelProperty("商户订单号")
    private String orderNo;

    @ApiModelProperty("收款用户TG ID")
    private Long tgUserId;

    @ApiModelProperty("收款用户TG昵称")
    private String userTgName;

    @ApiModelProperty("收款用户TG用户名")
    private String userTgUsername;

    @ApiModelProperty("金额,小数位4位")
    private BigDecimal amount;

    @ApiModelProperty("币种id 6为usdt")
    private Integer currencyId;

    @ApiModelProperty("签名")
    private String sign;

    @ApiModelProperty("时间戳当前时间戳毫秒值13位")
    private Long timestamp;
}
