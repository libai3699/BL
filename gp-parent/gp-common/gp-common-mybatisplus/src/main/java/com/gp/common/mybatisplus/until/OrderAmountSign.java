package com.gp.common.mybatisplus.until;

import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SignUtil;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OrderAmountSign {
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private TelegramUtil telegramUtil;

    public void dealSign(OrderAmount one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(one), signSecretKey);
        one.setSign(sign);
    }

    private String getParam(OrderAmount one) {
        return one.getOrderNo()
                + "&" + one.getCurrencyId()
                + "&" + one.getOrderNo()
                + "&" + one.getFromAddress()
                + "&" + one.getToAddress()
                + "&" + one.getTgUserId()
                + "&" + one.getOrderStatus()
                + "&" + one.getUpOrderNo()
                + "&" + one.getChannelId()
                + "&" + one.getShareholderId()
                + "&" + BigDecimalUtils.trim(one.getAmount())
                + "&" + BigDecimalUtils.trim(one.getBonusAmount())
                + "&" + BigDecimalUtils.trim(one.getWithdrawStatement())
                + "&" + BigDecimalUtils.trim(one.getTotalAmount())
                + "&" + one.getSignTime();
    }

    public void checkSign(OrderAmount one) {
        if (!SignUtil.checkSign(this.getParam(one), mNacosParam.getSignSecretKey(), one.getSign())) {
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),
                    "充值订单有问题订单号=" + "产品Id" + CecuUtil.getDbCode() + "，" + one.getOrderNo());
        }
    }

    public Boolean checkSignFlag(OrderAmount one) {
//        if (!SignUtil.checkSign(this.getParam(one), mNacosParam.getSignSecretKey(), one.getSign())) {
//            telegramUtil.dealWarnMsg("这个充值有问题订单号=" + "产品Id" + CecuUtil.getDbCode() + "，" + one.getOrderNo());
//            return false;
//        }

        return true;
    }
}
