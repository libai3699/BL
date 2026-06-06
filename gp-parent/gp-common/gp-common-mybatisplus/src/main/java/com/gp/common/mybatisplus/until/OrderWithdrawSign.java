package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.OrderWithdraw;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OrderWithdrawSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(OrderWithdraw one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(OrderWithdraw one) {
        return one.getOrderNo()
                + "&" + one.getCurrencyId()
                + "&" + one.getToAddr()
                + "&" + one.getFromAddr()
                + "&" + one.getUserId()
                + "&" + one.getTgUserId()
                + "&" + one.getOrderStatus()
                + "&" + one.getChannelId()
                + "&" + one.getShareholderId()
                + "&" + BigDecimalUtils.trim(one.getAmount())
                + "&" + BigDecimalUtils.trim(one.getRealAmount())
                + "&" + one.getSignTime();
    }

    public void checkSign(OrderWithdraw one) {

        if (!SignUtil.checkSign(this.getParam(one), mNacosParam.getSignSecretKey(), one.getSign())) {
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),
                    "这个提现订单有问题订单号=" + "产品Id" + CecuUtil.getDbCode() + "，" + one.getOrderNo());
        }

    }

    public Boolean checkSignFlag(OrderWithdraw one) {

//        if (!SignUtil.checkSign(this.getParam(one), mNacosParam.getSignSecretKey(), one.getSign())) {
//            telegramUtil.dealWarnMsg("这个提现订单有问题订单号=" + "产品Id" + CecuUtil.getDbCode() + "，" + one.getOrderNo());
//            return false;
//        }

        return true;
    }
}
