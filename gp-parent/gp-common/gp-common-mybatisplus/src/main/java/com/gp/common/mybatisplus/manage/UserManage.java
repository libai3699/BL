package com.gp.common.mybatisplus.manage;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.enums.UserSourceEnum;
import com.common.core.util.EncryptionUtils;
import com.common.core.util.StringUtils;
import com.common.core.util.TxExecutor;
import com.gp.common.base.utils.MD5Util;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.Language;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.service.ChannelService;
import com.gp.common.mybatisplus.service.ConfigRiskService;
import com.gp.common.mybatisplus.service.LanguageService;
import com.gp.common.mybatisplus.service.UserService;
import com.gp.common.mybatisplus.until.UserSign;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
@Slf4j
@Component
public class UserManage {

    @Resource
    private UserService userService;
    @Resource
    private ChannelService channelService;
    @Resource
    private UserSign userSign;
    @Resource
    private Executor threadPoolTaskExecutor;
    @Resource
    private LanguageService languageService;
    @Resource
    private ConfigRiskService configRiskService;



    public TUser addSignAndSave(Long tgUserId, String useTgName, String useTgUsername, String pic, UserSourceEnum userSourceEnum, Long channelId, Long upUserId, String email, String password, String lanKey) {
       return TxExecutor.run(() -> {
            TUser tUser = new TUser();
            tUser.setUserTgId(tgUserId);
            tUser.setUserTgName(Jsoup.clean(useTgName, Safelist.basic()));
            tUser.setUserTgUsername(useTgUsername);
            tUser.setEmail(email);
            tUser.setLanKey(lanKey);
            //需要设置下vip默认
            tUser.setLevel(1);
            //默认关注等级是0
           tUser.setFollowStatus(0);
            //设置默认英文这里如果有默认语言的话在这里设置下
            ;
            String lanKeyDefault = languageService.getDefaultLanguage();
            if(StrUtil.isNotEmpty(lanKey)){
                lanKeyDefault = lanKey;
            }
            tUser.setLanKey(lanKeyDefault);
            tUser.setUserAvatar(pic);
            tUser.setIsNew(1);
            //兼容一下如果密码不是为空的话
            if (StringUtils.isNotEmpty(password)) {
                userService.setPassword(tUser, password);
            }
            //看看渠道存在不
            if (!Objects.isNull(channelId)) {
                log.info("channelId存在 {}",channelId);
                Channel channel = channelService.getById(channelId);
                if (channel != null) {
                    tUser.setChannelId(channelId);
                    tUser.setShareholderId(channel.getShareholderId());
                }

            } else {
                //看看有没有配置默认渠道
                Long defaultUpChannelId = configRiskService.defaultUpChannelId();
                log.info("channelId不存在 {}",defaultUpChannelId);

                if (!Objects.isNull(defaultUpChannelId)) {
                    Channel channel = channelService.getById(defaultUpChannelId);
                    if (channel != null) {
                        tUser.setChannelId(defaultUpChannelId);
                        tUser.setShareholderId(channel.getShareholderId());
                    }
                }
            }
            //看看渠道是否存在
            if (!Objects.isNull(upUserId)) {
                log.info("upUserId存在 {}",upUserId);
                TUser upUser = userService.getById(upUserId);
                if (upUser != null) {
                    log.info("upUser channelId {}",upUser.getChannelId());
                    tUser.setChannelId(upUser.getChannelId());
                    tUser.setSuperUserId(upUserId);
                    tUser.setSuperUserTgId(upUser.getUserTgId());
                    Channel channel = channelService.getById(upUser.getChannelId());
                    if (channel != null) {
                        tUser.setShareholderId(channel.getShareholderId());
                    }
                    if (null != upUser.getPPath() && !"".equals(upUser.getPPath())) {
                        tUser.setPPath(upUser.getPPath() + "," + upUser.getUserId());
                    } else {
                        tUser.setPPath(upUser.getUserId() + "");
                    }
                }

            }

            if (userSourceEnum != null) {
                tUser.setSource(userSourceEnum.getType());
            }
            //默认用户类型 1
            tUser.setUserType(1);
            userService.save(tUser);
            userSign.dealUserSign(tUser);
            userService.updateById(tUser);

            return tUser;

        });
    }



    public TUser addSignAndSave2(Long tgUserId, String useTgName, String useTgUsername, String pic, UserSourceEnum userSourceEnum, Long channelId, Long upUserId, String email, String password, String lanKey, String salt, String payPassword) {
        return TxExecutor.run(() -> {
            TUser tUser = new TUser();
            tUser.setUserTgId(tgUserId);
            tUser.setUserTgName(Jsoup.clean(useTgName, Safelist.basic()));
            tUser.setUserTgUsername(useTgUsername);
            tUser.setEmail(email);
            tUser.setLanKey(lanKey);
            tUser.setLevel(1);
            //设置默认英文这里如果有默认语言的话在这里设置下
            tUser.setLanKey(languageService.getDefaultLanguage());
            tUser.setIsNew(1);
            tUser.setSalt(salt);
            tUser.setPayPassword(payPassword);

            //兼容一下如果密码不是为空的话
            if (StringUtils.isNotEmpty(password)) {
                tUser.setPassword(password);
            }

            if (userSourceEnum != null) {
                tUser.setSource(userSourceEnum.getType());
            }
            //默认用户类型 1
            tUser.setUserType(1);
            userService.save(tUser);
            userSign.dealUserSign(tUser);
            userService.updateById(tUser);

            return tUser;
        });
    }
}
