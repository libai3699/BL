package com.gp.common.base.model.merchant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class MerchantSendMoneyDto {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("appId")
    @NotBlank(message = "appId不能为空")
    private  String appKey;

    @ApiModelProperty("订单号")
    @NotBlank(message = "订单号不能为空")
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
    /** 币种id */
    @ApiModelProperty("币种id 6位usdt")
    @NotBlank(message = "币种id不能为空")
    private Integer currencyId;
}
