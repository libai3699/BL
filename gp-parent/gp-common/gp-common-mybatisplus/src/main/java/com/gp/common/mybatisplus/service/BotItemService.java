package com.gp.common.mybatisplus.service;

import com.alibaba.fastjson.JSON;
import com.common.datasource.config.CecuConfig;
import com.common.datasource.util.CecuUtil;
import com.gp.common.mybatisplus.nacos.BotConfig;
import com.gp.common.mybatisplus.nacos.BotConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotItemService {

    private final BotConfigProperties botConfigProperties;

    @Autowired
    public BotItemService(BotConfigProperties botConfigProperties) {
        this.botConfigProperties = botConfigProperties;
    }
    public List<BotConfig> getBot() {
        return botConfigProperties.getBots();
    }
    public BotConfig getBotById(Long id) {
        return botConfigProperties.getBots().stream()
                .filter(bot -> bot.getId().equals(id) )
                .findFirst()
                .orElse(null); // 或者抛出异常，根据需要处理
    }

    public  BotConfig getNowBot() {
        return botConfigProperties.getBots().stream()
                .filter(bot -> bot.getId().equals(Long.parseLong(CecuUtil.getDbCode())) )
                .findFirst()
                .orElse(null); // 或者抛出异常，根据需要处理
    }

    public BotConfig getBotByUrl(String url) {
        return botConfigProperties.getBots().stream()
                .filter(bot -> bot.getWebhookUrl().equals(url.trim()))
                .findFirst()
                .orElse(null); // 或者抛出异常，根据需要处理
    }
    public BotConfig getGroupBotByUrl(String url) {
        return botConfigProperties.getBots().stream()
                .filter(bot -> bot.getGroupWebhookUrl().equals(url.trim()))
                .findFirst()
                .orElse(null); // 或者抛出异常，根据需要处理
    }
}
