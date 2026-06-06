package com.gp.common.mybatisplus.pay.mpay.param;

import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantWithdrawDto extends WalletBase {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appKey")
    private  String appKey;
    //签名
    @ApiModelProperty("签名")
    private  String sign;

    //时间戳
    @ApiModelProperty("当前时间戳")
    private  Long timestamp;
    @ApiModelProperty("提现订单号")
    private String orderNo;
    /** 金额 */
    @ApiModelProperty("飞机id ")
    private Long tgUserId;
    /** 金额 */
    @ApiModelProperty("飞机昵称")
    private String userTgName;
    /** 金额 */
    @ApiModelProperty("飞机用户名")
    private String userTgUsername;

    /** 币种id */
    @ApiModelProperty("币种id")
    private Integer currencyId;

    @ApiModelProperty("金额")
    private BigDecimal amount;


    @ApiModelProperty("提现地址")
    private String address;
}
