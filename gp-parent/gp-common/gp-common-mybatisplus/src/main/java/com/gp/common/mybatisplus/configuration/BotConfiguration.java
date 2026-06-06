package com.gp.common.mybatisplus.configuration;

import cn.hutool.extra.spring.SpringUtil;
import com.gp.common.mybatisplus.nacos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class BotConfiguration {
    public static String financialStr = "financial";
    public static String reportStr = "report";
    public static String riskStr = "risk";

    @Resource
    private BotConfigProperties botConfigProperties;

    @PostConstruct
    public void getBotConfig() {
        registerBots(botConfigProperties.getBots());
    }

    private void registerBots(List<BotConfig> bots) {
        for (BotConfig bot : bots) {
            FinanceBotConfig finance = bot.getFinance();
            RiskBotConfig risk = bot.getRisk();
            ReportBotConfig report = bot.getReport();
            FinancialTelegramUtil financialTelegramUtil = new FinancialTelegramUtil(finance.getBotUsername(), finance.getBotToken(), finance.getChatId());
            RiskTelegramUtil riskTelegramUtil = new RiskTelegramUtil(risk.getBotUsername(), risk.getBotToken(), risk.getChatId());
            ReportTelegramUtil reportTelegramUtil = new ReportTelegramUtil(report.getBotUsername(), report.getBotToken(), report.getChatId());
            //将机器人注册到spring
            SpringUtil.registerBean(financialStr + bot.getId(), financialTelegramUtil);
            SpringUtil.registerBean(riskStr + bot.getId(), riskTelegramUtil);
            SpringUtil.registerBean(reportStr + bot.getId(), reportTelegramUtil);
        }
    }

    // 监听 Nacos 配置刷新事件
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // 延迟 1 分钟后执行刷新任务
        scheduler.schedule(() -> {
            doRefresh();
            scheduler.shutdown(); // 任务执行完成后关闭调度器
        }, 10, TimeUnit.SECONDS);
    }

    private void doRefresh() {
        List<BotConfig> bots = botConfigProperties.getBots();
        // 清理旧的 Bean
        for (BotConfig bot : bots) {
            try {
                SpringUtil.unregisterBean(financialStr + bot.getId());
            } catch (Exception e) {}
            try {
                SpringUtil.unregisterBean(riskStr + bot.getId());
            } catch (Exception e) {
            }
            try {
                SpringUtil.unregisterBean(reportStr + bot.getId());
            } catch (Exception e) {
            }
        }
        // 覆盖之前的 Bean
        registerBots(bots);
    }
//    @SneakyThrows
//    @Bean
//    public List<BotConfig> getBotConfig(BotConfigProperties botConfigProperties) {
//
//        List<BotConfig> bots = botConfigProperties.getBots();
//        for (BotConfig bot : bots) {
//            FinanceBotConfig finance = bot.getFinance();
//            RiskBotConfig risk = bot.getRisk();
//            ReportBotConfig report = bot.getReport();
//            FinancialTelegramUtil financialTelegramUtil = new FinancialTelegramUtil(finance.getBotUsername(), finance.getBotToken(), finance.getChatId());
//            RiskTelegramUtil riskTelegramUtil = new RiskTelegramUtil(risk.getBotUsername(), risk.getBotToken(), risk.getChatId());
//            ReportTelegramUtil reportTelegramUtil = new ReportTelegramUtil(report.getBotUsername(), report.getBotToken(), report.getChatId());
//            //将机器人注册到spring
//            SpringUtil.registerBean(financialStr + bot.getId(), financialTelegramUtil);
//            SpringUtil.registerBean(riskStr + bot.getId(), riskTelegramUtil);
//            SpringUtil.registerBean(reportStr + bot.getId(), reportTelegramUtil);
//        }
//        return bots;
//    }

    public static FinancialTelegramUtil getFinancialBot(String id) {
        return SpringUtil.getBean(financialStr + id, FinancialTelegramUtil.class);
    }

    public static RiskTelegramUtil getRiskBot(String id) {
        return SpringUtil.getBean(riskStr + id, RiskTelegramUtil.class);
    }

    public static ReportTelegramUtil getReportBot(String id) {
        return SpringUtil.getBean(reportStr + id, ReportTelegramUtil.class);
    }
}
