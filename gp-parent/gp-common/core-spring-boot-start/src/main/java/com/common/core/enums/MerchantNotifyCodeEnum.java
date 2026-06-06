package com.common.core.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MerchantNotifyCodeEnum {
    /**
     *参数错误
     */
    success(200, "成功"),
    /**
     * 商户失效
     */
    merchant_invalidity(400, "商户不存在或者已失效"),
    /**
     *时间戳不正确
     */
    time_error(401, "时间戳不正确"),
    /**
     *签名不正确
     */
    sign_error(402, "签名不正确"),
    /**
     *币种不存在
     */
    currency_error(403, "币种不存在"),
    /**
     *订单号失效
     */
    order_no_error(404, "订单号失效"),
    /**
     *参数错误
     */
    param_error(405, "参数错误"),


    ;

    private Integer code;
    private String message;


    public static String getTypeName(Integer type) {
        MerchantNotifyCodeEnum[] values = values();
        for (MerchantNotifyCodeEnum amountChangeEnum : values) {
            if (amountChangeEnum.getCode().equals(type)) {
                return amountChangeEnum.getMessage();
            }
        }
        return null;
    }


}
