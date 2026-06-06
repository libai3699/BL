package com.gp.common.mybatisplus.pay.mpay.order;

/**
 * @author axing
 * @version 1.0
 * @date 2024/5/7/007 11:53
 */
public interface PayExtDto {

    /**
     * 账户名称
     */
    String accountName = "accountName";

    /**
     * 银行账号
     */
    String accountNo = "accountNo";

    /**
     * 开户行
     */
    String bankName = "bankName";

    /**
     * 银行编码
     */
    String bankCode = "bankCode";


}
