package com.gp.common.mybatisplus.until;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.util.EncryptionUtils;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.exception.WalletException;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.entity.AmountChange;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.service.AmountChangeService;
import com.gp.common.mybatisplus.service.CurrencyService;
import com.gp.common.mybatisplus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserSign {
    @Resource
    private MNacosParam mNacosParam;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private AmountChangeSign amountChangeSign;
    //钱包 start

    public void checkWalletSign(Long userId, UserWallet userWallet) {
        //在这里去做签名会好一点
        String signSecretKey = mNacosParam.getSignSecretKey();
        String sign = userWallet.getSign();
        //上一次的签名 签名内容  id user_id user_tg_id  currency_id  item_id chan_id amount mask_addr sign_time //避免他这之前的直接封号了
            if(!SignUtil.checkSign(this.getParam(userWallet),userWallet.getSalt()+signSecretKey,sign)){
                String dbCode = CecuUtil.getDbCode();
                //存入redis  存一年 让他之后再也进不来
                String redisKeyOne = StrUtil.format(RedisKey.WGID,dbCode, userWallet.getUserId());
                redisUtil.set(redisKeyOne, redisKeyOne, 365, TimeUnit.DAYS);

                String realIpAddr = IpUtil.getRealIpAddr();
                if (StrUtil.isNotBlank(realIpAddr)) {
                    String redisKeyTwo = StrUtil.format(RedisKey.WGIP,dbCode, realIpAddr);
                    redisUtil.set(redisKeyTwo, redisKeyTwo, 365, TimeUnit.DAYS);
                }
                throw new WalletException(MessagesUtils.get("bot.wallet.QBBCG"),userId.toString());
//                Assert.isFalse(true, MessagesUtils.get("bot.wallet.QBBCG"));

        }

    }

    public void checkUserSign(TUser tUser) {
        //在这里去做签名会好一点
        String signSecretKey = mNacosParam.getSignSecretKey();
        String sign = tUser.getSign();
        if(!SignUtil.checkSign(this.getParamUser(tUser),signSecretKey,sign)){
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个人签名有问题userId=" + "产品Id"+CecuUtil.getDbCode() + "，" +tUser.getUserId());
        }
    }
    public void dealWalletSign(UserWallet newFromUser) {
        //首先看看盐是否存在
        String salt = newFromUser.getSalt();

        String signSecretKey = mNacosParam.getSignSecretKey();

        if(StringUtils.isEmpty(salt)){
            newFromUser.setSalt(MD5Util.getSalt());
        }else {
            newFromUser.setSalt(salt);
        }
        newFromUser.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParam(newFromUser), newFromUser.getSalt()+signSecretKey);
        newFromUser.setSign(sign);
    }
    public String getParam(UserWallet userWallet) {
        return  userWallet.getUserId()+"&"+userWallet.getUserTgId()+"&"+userWallet.getCurrencyId()+"&"+userWallet.getItemId()+"&"+
                userWallet.getChainTag()+"&"+ BigDecimalUtils.trim(userWallet.getAmount())+"&"+userWallet.getMaskAddr()+"&"+userWallet.getLastAmountId()+"&"+userWallet.getSignTime();
    }
    //钱包 End
    //账遍 start
    public void dealAmountChangeSign(AmountChange one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(amountChangeSign.getParam(one), signSecretKey);
        one.setSign(sign);
    }

    public void checkAmountChangeSign(Long userId, BigDecimal lastAmount, AmountChange lastAmountChange,Long amountChangeId) {
        return;
        //如果已经传了最后一个账变,就不用查询了
//        if (lastAmountChange == null) {
//            if(amountChangeId!=null){
//                lastAmountChange = amountChangeService.getById(amountChangeId);
//            }
//        }
//
//        //已经获取了,就判断用户之前是不是存在账变
//        if(lastAmountChange!=null){
//            BigDecimal newAmount = lastAmountChange.getNewAmount();
//            if (newAmount.compareTo(lastAmount)!=0){
//                throw new WalletException(MessagesUtils.get("bot.wallet.QBBCG"),userId.toString());
//            }
//            amountChangeSign.checkAmountChangeSign(lastAmountChange);
//        //没有账变的情况 应该是0
//        }else {
//            //hyh
////            if(lastAmount.compareTo(BigDecimal.ZERO)!=0){
////                throw new WalletException(MessagesUtils.get("bot.wallet.QBBCG"),userId.toString());
////            }
//        }
    }



    //账遍 end
    //用户和币种 start
    public void checkUserCurrency(TUser user, Integer currencyId) {
        String sign = user.getSign();
        String signSecretKey = mNacosParam.getSignSecretKey();
            if(!SignUtil.checkSign(this.getParamUser(user),signSecretKey,sign)){
                throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个人签名有问题userId=" + "产品Id"+CecuUtil.getDbCode() + "，" +user.getUserId());
//                Assert.isFalse(true, MessagesUtils.get("bot.wallet.QBBCG"));
            }
            //币种校验关闭
//        Currency currency = currencyService.getById(currencyId);
//        String currencySignStr = currency.getSign();
//            if(!SignUtil.checkSign(currencySign.getParam(currency),signSecretKey,currencySignStr)){
//                throw new ConfigPriceException(MessagesUtils.get("bot.config.error"),"这个币种有问题币种id="+currencyId);
//            }

    }

    public void dealUserSign(TUser one) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        one.setSignTime(System.currentTimeMillis());
        String sign = SignUtil.getSign(getParamUser(one), signSecretKey);
        one.setSign(sign);
    }


    public String getParamUser(TUser user) {
        return  user.getUserId()+"&"+user.getUserTgId()+"&"+user.getPayPassword()+"&"+user.getUserType()+"&"+user.getSignTime();
    }

    //用户和币种 end
}
