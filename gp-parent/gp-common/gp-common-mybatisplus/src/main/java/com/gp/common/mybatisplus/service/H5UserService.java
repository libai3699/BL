package com.gp.common.mybatisplus.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.NewUserOpenConstant;
import com.common.core.enums.UserSourceEnum;
import com.common.core.exception.ServiceExceptionUtil;
import com.common.core.sms.SmsConstant;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.mapper.UserMapper;
import com.gp.common.mybatisplus.until.UserSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class H5UserService extends ServiceImpl<UserMapper, TUser> {


    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private ConfigRiskService configRiskService;

    @Resource
    private UserService userService;
    @Resource
    private UserWebService userWebService;
    @Resource
    private UserSign userSign;


    public UserWeb webLogin(String account, String passWord) {
        UserWeb user = this.getUserByAccount(account);
        if (user == null){
            throw ServiceExceptionUtil.exception(CodeEnum.USER_NOT_EXISTS);
        }
        //验证web账号密码
        dealWebLogin(account, passWord, user);
        return user;
    }


    /**
     * 账号密码登录
     *
     * @param account
     * @param passWord
     * @return
     */
    public TUser h5Login(String account, String passWord) {
        //判断下次数
        TUser user = this.getUserByEmail(account);
        if (user == null || !Objects.equals(user.getSource(), UserSourceEnum.H5.getType())) {
            throw ServiceExceptionUtil.exception(CodeEnum.USER_NOT_EXISTS);
        }


        dealLogin(account, passWord, user);
        return user;
    }


    public void resetPassword(String account, String code, String newPassword) {
        //加一个刷ip的
        String realIpAddr = IpUtil.getRealIpAddr();
        String dbCode = CecuUtil.getDbCode();
        String resetErrorKey = StrUtil.format(RedisKey.resetPasswordNumError, dbCode, account);
        String resetIpNum = StrUtil.format(RedisKey.registerNumKeyIp, dbCode, realIpAddr);
        dealHandlerNUm(resetErrorKey);
        dealHandlerNUm(resetIpNum);
        //看看验证码对不对
        if (StringUtils.isAnyBlank(account, code)) {
            Assert.isFalse(true, MessagesUtils.get("bot.common.CZPF"));
        }
        String key = String.format(SmsConstant.STRING_USER_MAIL_CODE, dbCode, account);
        String smsCode = redisUtil.strGet(key);
        //先看看验证吗是否正确
        if (!code.equals(smsCode)) {
            redisUtil.incr(resetErrorKey, 1);
            redisUtil.expire(resetErrorKey, 1 * 60, TimeUnit.MINUTES);
            Assert.isFalse(true, MessagesUtils.get("bot.mail.YZMCW"));
        } else {
            redisUtil.del(key);
            //去重新修改用户的账户密码
            //开始去a查询账号是否存在
            TUser user = this.getUserByEmail(account);
            if (user != null) {
                String salt = user.getSalt();
                TUser tUser = new TUser();
                tUser.setUserId(user.getUserId());
                tUser.setUserTgId(user.getUserTgId());
                tUser.setPassword(user.getPayPassword());
                tUser.setUserType(user.getUserType());
                tUser.setPayPassword(user.getPayPassword());
                tUser.setSalt(salt);
                userService.setPassword(tUser, newPassword);
                userSign.dealUserSign(tUser);
                userService.updateById(tUser);
                redisUtil.incr(resetIpNum, 1);
                redisUtil.expire(resetIpNum, 1 * 60, TimeUnit.MINUTES);
            } else {
                throw ServiceExceptionUtil.exception(CodeEnum.USER_NOT_EXISTS);
            }
        }
    }

    public void dealHandlerNUm(String registerNumKey) {
        String registerNum = redisUtil.strGet(registerNumKey);
        //同一个ip 能注册5次吧
        if (StrUtil.isNotBlank(registerNum)) {
            Integer num = Integer.parseInt(registerNum);
            int numError = Integer.parseInt(configRiskService.register_times());
            if (num >= numError) {
                throw new BusinessException(MessagesUtils.get("bot.common.CZPF"));
            }
        }
    }


    public TUser getUserByEmail(String account) {
        LambdaQueryWrapper<TUser> q = new LambdaQueryWrapper<>();
        q.eq(TUser::getEmail, account);
        return this.getOne(q);
    }
    public UserWeb getUserByAccount(String account) {
        LambdaQueryWrapper<UserWeb> q = new LambdaQueryWrapper<>();
        q.eq(UserWeb::getAccount, account);
        return userWebService.getOne(q);
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
    /**
     * 得到签名参数
     * @param
     * @return
     */

    /**
     * type = 0 密码错误 1 显示久密码错误
     */
    public void dealLogin(String redisJoin, String passWord, TUser user)  {
        String dbCode = CecuUtil.getDbCode();
        String needCompareValue = user.getPassword();
        String inputValue = MD5Util.encryptSalt(passWord, user.getSalt());
        String redisKey = RedisKey.loginErrorNumKey;
        String key = StrUtil.format(redisKey, dbCode, redisJoin);
        String o = redisUtil.strGet(key);
        int configInt = 5;
        if (StrUtil.isNotBlank(o)) {
            int num = Integer.parseInt(o);
            if (num >= configInt) {
                long expire = redisUtil.getExpire(key);
                String s = convertSeconds(expire);
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
            }
        }
        boolean b = false;
        if(NewUserOpenConstant.OPEN.equals(dbCode)){
            try {
                b =  DjangoPasswordVerifier.verifyPassword(passWord, user.getPassword());
            } catch (Exception e) {
                b = false;
            }
        }
        if (b || inputValue.equals(needCompareValue)) {
            redisUtil.del(key);
            return;
        }
        //错误 都错了才可以
        //错误次数自增
        long numError = redisUtil.incr(key, 1);
        //获取禁止时间 30分钟吧
        //错误之后的封锁时长
        int time = Integer.parseInt(configRiskService.ban_password_error_time());
        redisUtil.expire(key, time * 60, TimeUnit.MINUTES);
        String str = MessagesUtils.get("bot.user.MMCW");
        //一旦超过3次提示他 不能还有几次提示封号  你已经输入了错误几次,如果超过5次将冻结1个小时
        if (numError < 3) {
            Assert.isFalse(true, str + MessagesUtils.get("bot.user.CW"));
        } else if (numError < configInt) {
            String s = convertSeconds(time * 60 * 60);
            Assert.isFalse(true, StringUtils.format(MessagesUtils.get("bot.user.SCMM"), MessagesUtils.get(str), configInt, numError, s));
        } else {
            long expire = redisUtil.getExpire(key);
            String s = convertSeconds(expire);
            throw new BusinessException(str + MessagesUtils.get("bot.user.CW") + " " + StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
        }
        throw ServiceExceptionUtil.exception(CodeEnum.USER_PASSWORD_FAILED);
    }
    /**
     * type = 0 密码错误 1 显示久密码错误
     */
    public void dealWebLogin(String redisJoin, String passWord, UserWeb user)  {
        String dbCode = CecuUtil.getDbCode();
        String needCompareValue = user.getPassword();
        String inputValue = MD5Util.encryptSalt(passWord, user.getSalt());
        String redisKey = RedisKey.loginWebErrorNumKey;
        String key = StrUtil.format(redisKey, dbCode, redisJoin);
        String o = redisUtil.strGet(key);
        int configInt = 5;
        if (StrUtil.isNotBlank(o)) {
            int num = Integer.parseInt(o);
            if (num >= configInt) {
                long expire = redisUtil.getExpire(key);
                String s = convertSeconds(expire);
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
            }
        }
        if (inputValue.equals(needCompareValue)) {
            redisUtil.del(key);
            return;
        }
        //错误 都错了才可以
        //错误次数自增
        long numError = redisUtil.incr(key, 1);
        //获取禁止时间 30分钟吧
        //错误之后的封锁时长
        int time = Integer.parseInt(configRiskService.ban_password_error_time());
        redisUtil.expire(key, time * 60, TimeUnit.MINUTES);
        String str = MessagesUtils.get("bot.user.MMCW");
        //一旦超过3次提示他 不能还有几次提示封号  你已经输入了错误几次,如果超过5次将冻结1个小时
        if (numError < 3) {
            Assert.isFalse(true, str + MessagesUtils.get("bot.user.CW"));
        } else if (numError < configInt) {
            String s = convertSeconds(time * 60 * 60);
            Assert.isFalse(true, StringUtils.format(MessagesUtils.get("bot.user.SCMM"), MessagesUtils.get(str), configInt, numError, s));
        } else {
            long expire = redisUtil.getExpire(key);
            String s = convertSeconds(expire);
            throw new BusinessException(str + MessagesUtils.get("bot.user.CW") + " " + StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
        }
        throw ServiceExceptionUtil.exception(CodeEnum.USER_PASSWORD_FAILED);
    }
    public void updatePassword( TUser user, String oldPassword,  String newPassword) {
        String dbCode = CecuUtil.getDbCode();
        String needCompareValue = user.getPassword();
        String inputValue = MD5Util.encryptSalt(oldPassword, user.getSalt());
        boolean b = false;
        if(NewUserOpenConstant.OPEN.equals(dbCode)){
            try {
                b =  DjangoPasswordVerifier.verifyPassword(oldPassword, user.getPassword());
            } catch (Exception e) {
                b = false;
            }
        }
        if (b || inputValue.equals(needCompareValue)) {
            String salt = user.getSalt();
            TUser tUser = new TUser();
            tUser.setSalt(salt);
            tUser.setUserId(user.getUserId());
            tUser.setUserTgId(user.getUserTgId());
            tUser.setPayPassword(user.getPayPassword());
            tUser.setUserType(user.getUserType());
            userService.setPassword(tUser, newPassword);
            userSign.dealUserSign(tUser);
            userService.updateById(tUser);
        }else {
            Assert.isFalse(true, MessagesUtils.get("bot.user.MMCW"));
        }
    }
}
