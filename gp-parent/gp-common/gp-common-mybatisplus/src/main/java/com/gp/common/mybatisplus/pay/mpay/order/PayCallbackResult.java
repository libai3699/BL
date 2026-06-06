package com.gp.common.mybatisplus.pay.mpay.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 支付回调处理结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayCallbackResult {

    /**
     * 系统内部订单号
     */
    private String orderNo;

    /**
     * 第三方支付商订单号
     */
    private String merchantOrderNo;

    /**
     * 订单状态 (1 为支付成功, 其他为支付中或失败)
     */
    private Integer orderStatus;

    /**
     * 实际支付金额
     */
    private BigDecimal amount;

    /**
     * 需要返回给第三方支付商的内容 (如 "success" 或 "fail")
     */
    private String responseMsg;

    /**
     * 业务是否处理成功 (签名校验通过且支付成功)
     */
    private boolean isSuccess;

    /**
     * 快捷创建成功结果
     */
    public static PayCallbackResult success(String orderNo, String responseMsg) {
        return PayCallbackResult.builder()
                .orderNo(orderNo)
                .responseMsg(responseMsg)
                .orderStatus(1)
                .isSuccess(true)
                .build();
    }

    /**
     * 快捷创建失败结果
     */
    public static PayCallbackResult failed(String responseMsg) {
        return PayCallbackResult.builder()
                .responseMsg(responseMsg)
                .isSuccess(false)
                .build();
    }
}
