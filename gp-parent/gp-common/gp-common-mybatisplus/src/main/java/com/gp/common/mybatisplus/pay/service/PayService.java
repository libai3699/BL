package com.gp.common.mybatisplus.pay.service;


import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddress;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;

import com.common.core.prop.ProxyProp;
import com.common.core.util.RedisUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/8/008 16:13
 */
@Data
@Slf4j
@Service
public class PayService {

    private RedisUtil redisUtil;
    public static ProxyProp proxyProp;


    public static String initUserAndGetPayToken(Long userId,String payParam, Integer itemId, String chainTag,String itemName) {
        CreateAddress createAddress = new CreateAddress();
        createAddress.setUserId(userId);
        createAddress.setPayParam(payParam);
        createAddress.setItemId(itemId);
        createAddress.setChainTag(chainTag);
        createAddress.setItemName(itemName);
        String address = PayWService.getPayService(PayMerchantCons.WALLET_PAY).createAddress(createAddress);
        return address;
    }






    /**
     * XPay 创建充值地址（兼容旧版本接口）
     * 注意：此方法保持向后兼容，但建议使用新版本方法
     *
     * @deprecated 建议使用 initUserAndGetPayTokenXPay(userId, payParam, protocolId, count, pid)
     */
    public static String initUserAndGetPayTokenXPay(Long userId, String payParam, Integer itemId,String dbCode) {
        CreateAddress createAddress = new CreateAddress();
        createAddress.setUserId(userId);
        createAddress.setPayParam(payParam);
        createAddress.setItemId(itemId);
        createAddress.setProtocolId(1); // 兼容旧版本，将 itemId 映射为 protocolId
        createAddress.setCount(1);
        createAddress.setPid(dbCode+"_"+userId);
        return PayWService.getPayService(PayMerchantCons.XPAY).createAddress(createAddress);
    }







}
