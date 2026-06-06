package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeReceive;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OrderRedEnvelopeReceiveSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public void dealSign(OrderRedEnvelopeReceive one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(OrderRedEnvelopeReceive one) {
        return  one.getOrderNo()
                +"&"+ one.getCurrencyId()
                +"&"+ BigDecimalUtils.trim(one.getAmount())
                +"&"+one.getUserId()
                +"&"+one.getSignTime();
    }
    public void checkSign(OrderRedEnvelopeReceive one) {

            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个红包接收订单有问题订单号=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getOrderNo());
            }

    }
    public Boolean checkSignFlag(OrderRedEnvelopeReceive one) {

            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                telegramUtil.dealWarnMsg("这个红包接收订单有问题订单号=" + "产品Id"+CecuUtil.getDbCode() + "，" +one.getOrderNo());
                return false;
            }

        return true;
    }
}
