package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ConfigRiskSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;
    public void dealSign(ConfigRisk one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    public String getParam(ConfigRisk one) {
        return  one.getConfigKey()
                +"&"+one.getConfigName()
                +"&"+one.getConfigValue()
                +"&"+one.getSignTime();
    }
    public void checkSign(ConfigRisk e) {
            if(!SignUtil.checkSign(getParam(e),mNacosParam.getSignSecretKey(),e.getSign())){
                throw new ConfigException(MessagesUtils.get("bot.config.error"),e.getConfigKey());
            }
        }
    public Boolean checkSignFlag(ConfigRisk e) {
            if (!SignUtil.checkSign(getParam(e), mNacosParam.getSignSecretKey(), e.getSign())) {
                telegramUtil.dealWarnMsg("这个配置有问题配置key=" + "产品Id"+CecuUtil.getDbCode() + "，" + e.getConfigKey());
                return false;

            }
        return true;
    }
}
