package com.common.core.mail;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.common.core.sms.SmsConstant;
import com.common.core.util.RedisUtil;
import com.common.core.util.StrKit;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.MessagesUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Data
@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private JavaMailSender javaMailSender;

    private String sender;

    private RedisUtil redisUtil;
    @Resource
    private EmailSender emailSender;
    @Resource
    private MailAokSample mailAokSample;

    public boolean sendSimpleMailCode(String email,String userId,Integer time,String dbCode,MailConfigStart mailConfigStart) {
        if (StringUtils.isBlank(email)) {
            throw new BusinessException(MessagesUtils.get("bot.mail.YXBNWK"));
        }
        if (!Validator.isEmail(email)) {
            throw new BusinessException(MessagesUtils.get("bot.mail.QTXZQDYX"));
        }

        String key = String.format(SmsConstant.STRING_USER_MAIL_CODE,dbCode, email);
        String smsCode = redisUtil.strGet(key);
        long expire = redisUtil.getExpire(key);
        String s = convertSeconds(expire);
        if (StringUtils.isNotEmpty(smsCode)) {
            throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.mail.DQYYYTYAM"),s));
        }
        String redisKey = StrUtil.format(RedisKey.mail_user_send_count,dbCode, userId);
        String mailCode = StrKit.getRandonNum(SmsConstant.SMS_SIZE);
        redisUtil.strSet(key, mailCode, Duration.ofMinutes(time));
        MailSendDto mailSendDto = new MailSendDto();
        mailSendDto.setTo(email);
        mailSendDto.setSubject(MessagesUtils.get("bot.mail.YZMFS"));
//        mailSendDto.setContent(StrUtil.format(MessagesUtils.get("bot.mail.NDYZMS"), mailCode));
        mailSendDto.setContent(mailCode);
        this.sendSimpleMailMessge(mailSendDto,redisKey,time,mailConfigStart);
        return true;
    }


    public void sendSimpleMailMessge(MailSendDto dto, String redisKey, Integer time,MailConfigStart mailConfigStart) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(dto.getTo());
        message.setSubject(dto.getSubject());
        message.setText(dto.getContent());
        try {
            //javaMailSender.send(message);
            mailAokSample.sendEmail(message,mailConfigStart);
            logger.info("send success.................");
        } catch (Exception e) {
            logger.error("发送简单邮件时发生异常!", e);
        }

        redisUtil.incr(redisKey, 1);
        redisUtil.expire(redisKey, time * 60, TimeUnit.MINUTES);
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
