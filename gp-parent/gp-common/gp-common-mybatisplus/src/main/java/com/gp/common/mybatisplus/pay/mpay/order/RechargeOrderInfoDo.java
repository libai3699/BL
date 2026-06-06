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
public class RechargeOrderInfoDo extends Base {

    @JSONField(name = "data")
    private RechargeOrderInfo data;

    @NoArgsConstructor
    @Data
    public static class RechargeOrderInfo {

        /**
         * 金额
         */
        @ApiModelProperty("金额")
        private BigDecimal amount;
        @ApiModelProperty("itemId")
        private Integer itemId;
        @ApiModelProperty("链名称")
        private String chainTag;
        @ApiModelProperty("txid")
        private String txid;
        //来源地址
        @ApiModelProperty("来源地址")
        private String fromAddress;
        //目标地址
        @ApiModelProperty("目标地址")
        private String toAddress;
        @ApiModelProperty("上游单号")
        private String upOrderNo;

        @ApiModelProperty("创建时间")
        private Long createTime;
    }
}
