package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.UserExt;
import com.gp.common.mybatisplus.entity.UserExtChange;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.service.UserExtChangeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserExtChangeSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(UserExtChange one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(UserExtChange one) {
           return  one.getUserId()
                    +"&"+one.getTgUserId()
                    +"&"+one.getExtType()
                    +"&"+one.getOrderNo()
                    +"&"+one.getAccountType()
                    +"&"+one.getOrderType()
                    +"&"+one.getType()
                    +"&"+BigDecimalUtils.trim(one.getAmount())
                    +"&"+BigDecimalUtils.trim(one.getNewAmount())
                    +"&"+BigDecimalUtils.trim(one.getOldAmount())
                    +"&"+one.getSignTime();
    }
    public void checkSign(UserExtChange one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个用户附加数据据有问题,id=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getUserId());
        }

    }
    public Boolean checkSignFlag(UserExtChange one) {
        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
            telegramUtil.dealWarnMsg("这个数据有问题,checkClassName="+this.getClass().getSimpleName()+",id=" + "产品Id"+CecuUtil.getDbCode() + "，" +one.getId());
            return false;
        }
        return true;
    }
    //加密


}
