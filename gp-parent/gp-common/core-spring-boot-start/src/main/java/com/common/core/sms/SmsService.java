package com.common.core.sms;


import cn.hutool.core.util.StrUtil;
import com.common.core.prop.SmsAlProp;
import com.common.core.prop.SmsByteProp;
import com.common.core.prop.SmsTxProp;
import com.common.core.util.RedisUtil;
import com.common.core.util.SmsByteUtil;
import com.common.core.util.StrKit;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.MessagesUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Data
public class SmsService {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private RedisUtil redisUtil;

    private SmsAlProp smsAlProp;
    private SmsTxProp smsTxProp;
    private SmsByteProp smsByteProp;
    private com.byteplus.service.sms.SmsService smsByteService;


    public boolean sendSms(String phone,Long userId,Integer time) throws BusinessException {
        String key = String.format(SmsConstant.STRING_USER_PHONE_CODE, phone);
        //看看今天的发送次数
        String smsCode = redisUtil.strGet(key);
        if (StringUtils.isNotEmpty(smsCode)) {
            long expire = redisUtil.getExpire(key);
            String s = convertSeconds(expire);
            throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.phone.DQYYYTDX"),s));
        }
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException(MessagesUtils.get("bot.phone.SJHBNWK"));
        }
        smsCode = StrKit.getRandonNum(SmsConstant.SMS_SIZE);
        boolean result = false;
        if (SmsByteUtil.send(phone, smsCode, smsByteService, smsByteProp)) {
            result = true;
        }

        String redisKey = StrUtil.format(RedisKey.sms_user_send_count, userId);

        //发送成功信息
        if (result) {
            redisUtil.strSet(key, smsCode, Duration.ofMinutes(5));
            redisUtil.incr(redisKey, 1);
            redisUtil.expire(redisKey, time * 60, TimeUnit.MINUTES);
        } else {
            redisUtil.delete(key);
            throw new BusinessException(MessagesUtils.get("bot.phone.FSSBQSHCS"));
        }

        return true;
    }

    public boolean checkSmsCode(String phone, String smsCode)  {
        if (StrUtil.isBlank(phone)) {
            log.error("手机号不能为空 !");
            return false;
        }
        if (StrUtil.isBlank(smsCode)){
            log.error("验证码不能为空 !");
            return false;
        }
        String key = String.format(SmsConstant.STRING_USER_PHONE_CODE, phone);
        String _smsCode = redisUtil.strGet(key);
        if (StrKit.notBlank(_smsCode)) {
            if (_smsCode.equals(smsCode)) {
                //使用过之后清除
                redisUtil.delete(key);
                return true;
            } else {
                return false;
            }
        } else {
            log.error("未发送验证码,请点击发送后进行验证! !");
            return false;
        }
    }
    public String convertSeconds(long seconds) {
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" " + MessagesUtils.get("bot.time.day"));
        }
        if (hours > 0) {
            result.append(hours).append(" " + MessagesUtils.get("bot.time.hour"));
        }
        if (minutes > 0) {
            result.append(minutes).append(" " + MessagesUtils.get("bot.time.minute"));
        }
        if (remainingSeconds > 0) {
            result.append(remainingSeconds).append(" " + MessagesUtils.get("bot.time.second"));
        }

        return result.toString();
    }

}
