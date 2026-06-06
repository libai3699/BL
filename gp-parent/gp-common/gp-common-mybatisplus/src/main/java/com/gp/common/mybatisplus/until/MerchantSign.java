package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.Merchant;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class MerchantSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public void dealSign(Merchant one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(Merchant one) {
        BigDecimal amount = one.getAmount() != null ? one.getAmount() : BigDecimal.ZERO;
        return  one.getCode()
                +"&"+one.getDomainName()
                +"&"+ BigDecimalUtils.trim(amount)
                +"&"+one.getSignTime();
    }
    public void checkSign(Merchant one) {
            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个商户有问题 编码=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getCode());
            }
    }
    public Boolean checkSignFlag(Merchant one) {
            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                telegramUtil.dealWarnMsg("这个商户有问题 编码=" + "产品Id"+CecuUtil.getDbCode() + "，" +one.getCode());
                return false;
            }

        return true;
    }
}
