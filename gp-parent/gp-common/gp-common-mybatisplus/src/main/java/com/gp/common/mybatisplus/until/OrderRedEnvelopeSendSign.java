package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeSend;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class OrderRedEnvelopeSendSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public void dealSign(OrderRedEnvelopeSend one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }
    //加上剩余余额
    private String getParam(OrderRedEnvelopeSend one) {
        if(one.getUserId()==null){
            one.setUserId(0L);
        }
        if(one.getUserTgId()==null){
            one.setUserTgId(0L);
        }

        return  one.getOrderNo()
                +"&"+ one.getCurrencyId()
                +"&"+ BigDecimalUtils.trim(one.getAmount())
                +"&"+one.getUserId()
                +"&"+one.getUserTgId()
                +"&"+one.getStatus()
                +"&"+ BigDecimalUtils.trim(one.getLastAmount())
                +"&"+one.getSignTime();
    }


    public void checkSign(OrderRedEnvelopeSend one) {

            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个红包发送订单有问题订单号=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getOrderNo());
            }

    }
    public Boolean checkSignFlag(OrderRedEnvelopeSend one) {

            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                telegramUtil.dealWarnMsg("这个红包发送订单有问题订单号=" + "产品Id"+CecuUtil.getDbCode() + "，" +one.getOrderNo());
                return false;
            }

        return true;
    }
}
