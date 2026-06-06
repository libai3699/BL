package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class PriceRechargeSign {

    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(PriceRecharge one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(PriceRecharge one) {
        return  BigDecimalUtils.trim(one.getPrice())
                + "&" + BigDecimalUtils.trim(one.getBonus())
                + "&" + one.getSignTime();
    }

    public void checkSign(PriceRecharge one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个数据有问题,id=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getId());
        }
    }

    public Boolean checkSignFlag(PriceRecharge one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            telegramUtil.dealWarnMsg("这个数据有问题,checkClassName="+this.getClass().getSimpleName()+",id="+one.getId() + "产品Id"+CecuUtil.getDbCode() );
            return false;
        }
        return true;
    }

    //加密

}
