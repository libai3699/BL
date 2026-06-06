package com.gp.common.mybatisplus.pay.mpay.order;


import com.gp.common.mybatisplus.pay.mpay.Base;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单返回的详情
 */
@Data
public class WithdrawResultDo extends Base {

    public Result data;
    @Data
    @NoArgsConstructor
    public static class Result {
        /**
         * 上游提现订单号
         */
        @ApiModelProperty("上游提现单号")
        private String orderNo;

        @ApiModelProperty("上游返回信息")
        private String upInfo;
        /**
         * 上游提现订单号
         */
        @ApiModelProperty("上游提现单号")
        private String upOrderNo;
        /**
         * 币种id
         */
        @ApiModelProperty("币种id")
        private Integer currencyId;

        @ApiModelProperty("金额")
        private BigDecimal amount;
        /**
         * 提现时间
         */
        @ApiModelProperty("提现时间")
        private Long createTime;
    }



}
