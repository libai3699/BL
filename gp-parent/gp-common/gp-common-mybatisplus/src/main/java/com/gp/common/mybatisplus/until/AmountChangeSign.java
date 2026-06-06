package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.WalletException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.AmountChange;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class AmountChangeSign {

    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;



    public void checkAmountChangeSign(AmountChange amountChange) {
        //查询这个币总最后一条记录
        if(amountChange!=null){
                String signSecretKey = mNacosParam.getSignSecretKey();
                String param =  getParam(amountChange);
                String sign = SignUtil.getSign(param, signSecretKey);
                if(!sign.equals(amountChange.getSign())){
                    Long userId = amountChange.getUserId();
                    throw new WalletException(MessagesUtils.get("bot.wallet.QBBCG"),userId.toString());
                }

        }
    }
    public void dealSign(AmountChange one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }
    public Boolean checkAmountChangeSignFlag(AmountChange one) {
        //查询这个币总最后一条记录
            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                telegramUtil.dealWarnMsg("这个账变有问题订单号=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getOrderNo());
                return false;
            }
        return true;
    }
    public String getParam(AmountChange amountChange) {
        return amountChange.getUserId()+"&"+amountChange.getTgUserId()+"&"+amountChange.getCurrencyId()+"&"+amountChange.getItemId()+"&"+amountChange.getChainTag()+"&"+amountChange.getItemName()+"&"+amountChange.getOrderNo()+"&"+amountChange.getOrderType()+"&"+amountChange.getExchangeType()+"&"+
                BigDecimalUtils.trim(amountChange.getAmount())+"&"+BigDecimalUtils.trim(amountChange.getOldAmount())+"&"+BigDecimalUtils.trim(amountChange.getNewAmount());
    }
}
