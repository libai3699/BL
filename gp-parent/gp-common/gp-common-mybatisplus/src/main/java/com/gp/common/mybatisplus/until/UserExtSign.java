package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.core.util.EncryptionUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.Game;
import com.gp.common.mybatisplus.entity.UserExt;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserExtSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(UserExt one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(UserExt one) {
           return  one.getUserId()
                    +"&"+one.getUserTgId()
                    +"&"+one.getType()
                    +"&"+one.getCurrencyId()
                    +"&"+one.getItemId()
                    +"&"+one.getChainTag()
                    +"&"+BigDecimalUtils.trim(one.getAmount())
                    +"&"+one.getSignTime();
    }
    public void checkSign(UserExt one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个用户附加数据据有问题,id=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getUserId());
        }

    }
    public Boolean checkSignFlag(UserExt one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            telegramUtil.dealWarnMsg("这个数据有问题,checkClassName="+this.getClass().getSimpleName()+",id=" + "产品Id"+CecuUtil.getDbCode() + "，" +one.getId());
            return false;
        }
        return true;
    }
    //加密


}
