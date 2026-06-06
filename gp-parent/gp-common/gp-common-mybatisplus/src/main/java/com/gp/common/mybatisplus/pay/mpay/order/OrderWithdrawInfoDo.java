package com.gp.common.mybatisplus.pay.mpay.order;


import com.alibaba.fastjson2.annotation.JSONField;
import com.gp.common.mybatisplus.pay.mpay.Base;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单返回的详情
 */
@Data
public class OrderWithdrawInfoDo extends Base {

    @JSONField(name = "data")
    private OrderInfo data;

    @NoArgsConstructor
    @Data
    public static class OrderInfo {


        /**
         * 充值单号
         */
        @ApiModelProperty("单号")
        private String orderNo;

        /**
         * 币种id
         */
        @ApiModelProperty("币种id")
        private Integer currencyId;




        /**
         * 金额
         */
        @ApiModelProperty("金额")
        private BigDecimal amount;

        /**
         * 订单状态(0 待支付, 1 已支付, 2 已取消)
         */
        @ApiModelProperty("订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败)")
        private Integer orderStatus;

        /**
         * 实际到账金额
         */
        @ApiModelProperty("实际到账金额")
        private BigDecimal actualAmount;



        /**
         * 商户订单号
         */
        @ApiModelProperty("商户订单号")
        private String merchantOrderNo;


        /**
         * 附加参数
         */
        @ApiModelProperty("附加参数")
        private String attch;
        @ApiModelProperty("备注")
        private String remark;



        /**
         * 下单时间
         */
        @ApiModelProperty("下单时间")

        private Long createTime;
    }
}
