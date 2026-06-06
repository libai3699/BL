package com.gp.common.mybatisplus.until;

import cn.hutool.core.util.StrUtil;
import com.gp.common.mybatisplus.pay.constant.PayConst;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/27/027 17:49
 */
public class CoinAddrUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static boolean checkAddr(String addr, Integer currencyId) {
        //如果是usdt 或者是trx
        if (currencyId.equals(PayConst.currency_id) || currencyId.equals(PayConst.currency_id_trx)) {
            return StrUtil.isNotBlank(addr) && StrUtil.startWith(addr, "T") && addr.length() == 34;
        }
        //如果是eth 0x207556614179f17605a3341a5692d61a3b68073d
//        if(currencyId.equals(PayConst.currency_id_ETH)){
//
//            return StrUtil.isNotBlank(addr) && StrUtil.startWith(addr, "0")&& addr.length() == 42;
//        }

        //如果btc 0x17697ddef08ce651149a9e1e79f4b85e30da8c60
//        if(currencyId.equals(PayConst.currency_id_BTC)){
//            return StrUtil.isNotBlank(addr) && StrUtil.startWith(addr, "0x") && addr.length() == 42;
//        }
        return true;
    }

    public static boolean checkUSDTAddr(String addr) {
        return StrUtil.isNotBlank(addr) && StrUtil.startWith(addr, "T") && addr.length() == 34;
    }


}
