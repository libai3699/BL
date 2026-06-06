package com.gp.common.mybatisplus.nacos;

import lombok.Data;

@Data
public class MybatisBotConfig {
    private Long merchantId;
    //logo
    private String logo;
    //体育播报图
    private String soccerReport;
    //转盘图
    private String wheelPic;
    //活动抽奖图
    private String activityPic;
    //充值奖励图
    private String rechargePic;
    //提现图
    private String withdrawPic;
    //下分图
    private String downMoneyPic;

    //升级图
    private String levelUpPicV2;
    private String levelUpPicV3;
    private String levelUpPicV4;
    private String levelUpPicV5;
    private String levelUpPicV6;
    private String levelUpPicV7;
    private String levelUpPicV8;
    private String levelUpPicV9;
    private String levelUpPicV10;
    private String startLogo;
    //欧洲杯渠道
    private String europeanChannelId;
    //维护特殊人群
    private String specialUserId;
    //红包领取失败图
    private String mpayRedFailMsg;
    //红包领取失败图
    private String redUrl;




    //播报中奖
    private String winUrl;
    //播报充值不分
    private String rechargeUrl;
    //播报提现不分
    private String withdrawUrl;
    //分控通知的人

    private String sendRiskRen;
    //财务通知的人
    private String sendRen;
    //email配置
    private String emailAppKey;
    private String emailTemplateId;
    private String emailAlias;

    /**
     * 平台说明
     */
    private String platformDescription;

    /**
     * 邀请说明
     */
    private String inviteDescription;
}
