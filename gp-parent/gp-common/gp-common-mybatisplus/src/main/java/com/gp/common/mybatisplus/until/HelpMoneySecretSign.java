package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.core.util.EncryptionUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.GameType;
import com.gp.common.mybatisplus.entity.OrderHelpMoney;
import com.gp.common.mybatisplus.mq.HelpMoneyEntity;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;

@Component
public class HelpMoneySecretSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(OrderHelpMoney one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(OrderHelpMoney one) {
        return  one.getUserId()
                + "&" + one.getTgUserId()
                + "&" + one.getDayStr()
                + "&" + one.getGameTypeCode()

                + "&" + BigDecimalUtils.trim(one.getBetMoney().setScale(6, RoundingMode.DOWN))
                + "&" + BigDecimalUtils.trim(one.getWinMoney().setScale(6, RoundingMode.DOWN))
                + "&" + BigDecimalUtils.trim(one.getCalMoney().setScale(6, RoundingMode.DOWN))
                + "&" + BigDecimalUtils.trim(one.getCalRatio().setScale(6, RoundingMode.DOWN))
                + "&" + BigDecimalUtils.trim(one.getReceiveMoney().setScale(6, RoundingMode.DOWN))
                + "&" + one.getIsReceive()
                + "&" + one.getSignTime();
    }

    public void checkSign(OrderHelpMoney one) {
//        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
//            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个数据有问题,id=" + "产品Id"+ CecuUtil.getDbCode() + "，" +one.getId());
//        }
    }

    public Boolean checkSignFlag(OrderHelpMoney one) {
//        if(!SignUtil.checkSign(this.getParam(one),mNacosParam.getSignSecretKey(),one.getSign())){
//            telegramUtil.dealWarnMsg("这个数据有问题,checkClassName=" + "产品Id"+CecuUtil.getDbCode() + "，" +this.getClass().getSimpleName()+",id="+one.getId());
//            return false;
//        }
        return true;
    }

    //加密



}
