package com.gp.common.mybatisplus.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotConfig {
    private Long id;
    private String gameName;
    private String gameBotUsername;
    private String gameBotToken;
    /**
     * 飞投推广域名
     */
    private String gameBotUrl;
    private String gameGame;
    private String webApp;
    private String frontAdmin;
    /**
     * 网页推广域名
     */
    private String frontAdminH5;
    private String webhookUrl;
    private String groupWebhookUrl;
    private String groupWebhookChatIds;
    private Boolean isProductWH;
    @Value("${isH5Open:true}")
    private Boolean isH5Open;

    //播报
    private ReportBotConfig report;
    //风控
    private RiskBotConfig risk;
    //财务
    private FinanceBotConfig finance;
    /**
     * 网页推广域名
     */
    @Value("${shareRatio:0.4}")
    private String shareRatio;
}
