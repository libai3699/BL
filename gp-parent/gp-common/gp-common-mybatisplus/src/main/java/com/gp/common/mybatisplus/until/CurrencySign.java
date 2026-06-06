package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CurrencySign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public String getParam(Currency one) {
        String str = one.getCurrency()
                + "&" + one.getId()
                + "&" + one.getChainTag()
                + "&" + one.getItemId()
                + "&" + one.getSignTime();
        return str;
    }

    public void dealSign(Currency one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    public Boolean checkSignFlag(Currency e) {

            if (!SignUtil.checkSign(getParam(e), mNacosParam.getSignSecretKey(), e.getSign())) {
                telegramUtil.dealWarnMsg("这个币种有问题币种id="  + "产品Id"+ CecuUtil.getDbCode() + "，" +e.getId());
                return false;

            }

        return true;
    }
}
