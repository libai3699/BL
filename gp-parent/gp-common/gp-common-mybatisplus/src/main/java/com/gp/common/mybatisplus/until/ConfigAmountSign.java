package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.ConfigAmount;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class ConfigAmountSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public  void  dealSign(ConfigAmount one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }
    public String getParam(ConfigAmount one) {
        return  one.getConfigKey()
                +"&"+one.getConfigName()
                +"&"+one.getConfigValue()
                +"&"+one.getSignTime();
    }
    public void checkSign(ConfigAmount one) {
            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                throw new ConfigException(MessagesUtils.get("bot.config.error"),one.getConfigKey());
            }

    }
    public Boolean checkSignFlag(ConfigAmount one) {
            if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
                telegramUtil.dealWarnMsg("这个配置有问题配置key=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getConfigKey());
                return false;

            }

        return true;
    }
}
