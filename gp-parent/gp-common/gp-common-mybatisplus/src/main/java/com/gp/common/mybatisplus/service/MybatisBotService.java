package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.google.errorprone.annotations.Var;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.ActivityAwardReceive;
import com.gp.common.mybatisplus.entity.ConfigAmount;
import com.gp.common.mybatisplus.entity.Merchant;
import com.gp.common.mybatisplus.mapper.ActivityAwardReceiveMapper;
import com.gp.common.mybatisplus.nacos.MybatisBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动奖励领取Service业务层处理
 *
 * @author axing
 * @date 2024-06-12
 */
@Service
@Slf4j
public class MybatisBotService {
    @Resource
    RedisUtil redisUtil;
    @Resource
    CecuUtil cecuUtil;
    @Resource
    MerchantService merchantService;
    @Resource
    private ErrorTelegramUtil errorTelegramUtil;
    @PostConstruct
    public void init() {
        InitMybatisBotConfig();
    }

    public void InitMybatisBotConfig() {
            changeMerchantConfig();
    }


    public void changeMerchantConfig() {
        String dbCode = CecuUtil.getDbCode();
        cecuUtil.cutDbByName("yh_ft_control");
        List<Merchant> merchantList = merchantService.list(new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, CommonConstant.OPEN));
        List<MybatisBotConfig> mybatisBotConfigs = BeanUtils.copyListPropertiesIgnoreNull(merchantList, MybatisBotConfig.class);

        mybatisBotConfigs.stream().forEach(e -> {
            String key = StrUtil.format(RedisKey.MERCHANT_CONFIG, e.getMerchantId());
            redisUtil.set(key, e);
        });
        //清除切库信息,恢复默认数据库
        CecuUtil.cleanDbInfo();
        cecuUtil.cutDbByCode(dbCode);
    }

    public MybatisBotConfig getMybatisBotConfig() {
        MybatisBotConfig mybatisBotConfig = new MybatisBotConfig();
        mybatisBotConfig.setLogo("/ftDev/common/150.png");
        mybatisBotConfig.setSoccerReport("/ftDev/icon/soccer.png");
        mybatisBotConfig.setWheelPic("/ftDev/common/1793897265377370112.png");
        mybatisBotConfig.setActivityPic("/ftDev/common/1793897273833086976.png");
        mybatisBotConfig.setRechargePic("/ftDev/common/1793925878802325504.png");
        mybatisBotConfig.setWithdrawPic("/ftDev/common/1801913890465247232.png");
        mybatisBotConfig.setDownMoneyPic("/ftDev/common/1801913890465247232.png");
        mybatisBotConfig.setLevelUpPicV2("/ftDev/common/1796883821237317632.png");
        mybatisBotConfig.setLevelUpPicV3("/ftDev/common/1796883826899628032.png");
        mybatisBotConfig.setLevelUpPicV4("/ftDev/common/1796876873171415040.png");
        mybatisBotConfig.setLevelUpPicV5("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setLevelUpPicV6("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setLevelUpPicV7("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setLevelUpPicV8("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setLevelUpPicV9("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setLevelUpPicV10("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setStartLogo("/ftDev/common/1796876878993108992.png");
        mybatisBotConfig.setEuropeanChannelId("0");
        mybatisBotConfig.setSpecialUserId("1");
        mybatisBotConfig.setMpayRedFailMsg("领取失败，您还不是EGame的用户，请您先进入EGame后再领取该红包。");
        mybatisBotConfig.setWinUrl("/ftDev/common/1801913802808487936.png");
        mybatisBotConfig.setRechargeUrl("/ftDev/common/1801913826103652352.png");
        mybatisBotConfig.setWithdrawUrl("/ftDev/common/1801913890465247232.png");
        mybatisBotConfig.setSendRiskRen("axing");
        mybatisBotConfig.setSendRen("axing");
        mybatisBotConfig.setEmailAppKey("317f0457e5c3f206b8584aec83cac87b");
        mybatisBotConfig.setEmailTemplateId("E_103357661858");
        mybatisBotConfig.setEmailAlias("EGAME");
        mybatisBotConfig.setRedUrl("");

        String dbCode = CecuUtil.getDbCode();
        String findKey = StrUtil.format(RedisKey.MERCHANT_CONFIG,dbCode);
        Object o = redisUtil.get(findKey);
        if(o!=null){
            return (MybatisBotConfig)o;
        }else {
            String consoleLog = new StringBuffer()
                    .append("商户配置信息缺失,请检查数据库配置产品id是"+dbCode)
                    .toString();
            errorTelegramUtil.dealErrorMsg(consoleLog);
            return mybatisBotConfig;
        }


    }


}
