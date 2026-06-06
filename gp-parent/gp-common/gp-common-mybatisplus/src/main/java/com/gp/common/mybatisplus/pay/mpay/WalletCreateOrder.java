package com.gp.common.mybatisplus.pay.mpay;



import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class WalletCreateOrder extends Base {
    @ApiModelProperty("数据对象")
    private OrderInfo data;

    @NoArgsConstructor
    @Data
    public static class OrderInfo {


        /** 充值单号 */
        @ApiModelProperty("充值单号")
        private String orderNo;

        /** 币种id */
        @ApiModelProperty("币种id")
        private Integer currencyId;

        /** 币种名称 */
        @ApiModelProperty("币种名称")
        private String itemName;



        /** 金额 */
        @ApiModelProperty("金额")
        private BigDecimal amount;

        /** 订单状态(0 待支付, 1 已支付, 2 已取消) */
        @ApiModelProperty("订单状态(0 待支付, 1 已支付, 2 已取消)")
        private Integer orderStatus;

        /** 实际到账金额 */
        @ApiModelProperty("实际到账金额")
        private BigDecimal actualAmount;

        /** 商户订单号 */
        @ApiModelProperty("商户ID")
        private Long merchantId;



        /** 商户订单号 */
        @ApiModelProperty("商户订单号")
        private String merchantOrderNo;






        /** 附加参数 */
        @ApiModelProperty("附加参数")
        private String attch;
        @ApiModelProperty("备注")
        private String remark;

        @ApiModelProperty("支付的url")
        private String url;
        /** 支付时间 */
        @ApiModelProperty("支付时间")

        private Long payTime;

        /** 下单时间 */
        @ApiModelProperty("下单时间")
        private Long createTime;

    }
}
