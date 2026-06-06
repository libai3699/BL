package com.gp.common.base.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Bot 币种单位配置 — 从 Nacos (application-${profile}.yml) 读取，
 * 避免把币种硬编码到 classpath:i18n/bot-unit.properties 导致多分支合并冲突。
 *
 * <pre>
 * bot:
 *   money:
 *     unit: VND
 *   red:
 *     unit: VND
 * </pre>
 */
@Component
public class BotUnitProperties {

    private static volatile String moneyUnit = "";
    private static volatile String redUnit = "";

    @Value("${bot.money.unit:}")
    private String moneyUnitCfg;

    @Value("${bot.red.unit:}")
    private String redUnitCfg;

    @PostConstruct
    public void init() {
        moneyUnit = moneyUnitCfg == null ? "" : moneyUnitCfg;
        redUnit = redUnitCfg == null ? "" : redUnitCfg;
    }

    public static String getMoneyUnit() {
        return moneyUnit;
    }

    public static String getRedUnit() {
        return redUnit;
    }
}
