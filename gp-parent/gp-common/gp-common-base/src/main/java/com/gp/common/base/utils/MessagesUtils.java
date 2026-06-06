package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 国际化工具类
 **/
@Slf4j
public class MessagesUtils {
    public static final Locale russia = new Locale("ru", "RU");//俄语
    public static final Locale Spain = new Locale("pt", "BR");//葡萄牙-巴西
    public static final Locale Viet = new Locale("vi", "VN");//越南语
    public static final Locale tr = new Locale("tr", "TR");//土耳其
    public static final Locale indiaLocale = new Locale("hi", "IN"); // 英语，印度
    public static final Locale th = new Locale("th", "TH"); // 泰文
    public static final Locale SIMPLIFIED_CHINESE = new Locale("zh", "CN"); // 简体中文
    public static final Locale TRADITIONAL_CHINESE = new Locale("zh", "TW"); // 繁体中文
    public static final Locale ENGLISH = new Locale("en", "US");// 英文
    public static final Locale KOREAN = new Locale("ko", "KR");// 韩文
    public static final Locale JAPANESE = new Locale("ja", "JP");// 日文
    public static final Locale Arabia = new Locale("ar", "SA");// 阿拉伯语

    /**
     * 获取单个国际化翻译值
     */
    public static String get(String msgKey) {
        // bot 币种单位已迁移到 Nacos (BotUnitProperties)，避免分支合并冲突
        if ("bot.money.unit".equals(msgKey)) {
            String v = BotUnitProperties.getMoneyUnit();
            if (StrUtil.isNotBlank(v)) {
                return v;
            }
        } else if ("bot.red.unit".equals(msgKey)) {
            String v = BotUnitProperties.getRedUnit();
            if (StrUtil.isNotBlank(v)) {
                return v;
            }
        }
        try {
            return SpringUtil.getBean(MessageSource.class).getMessage(msgKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            log.error(e.getMessage());
            return msgKey;
        }
    }

    public static void setLang(String lang) {
        Locale locale = Locale.getDefault();
        if (StrUtil.isBlank(lang)) {
            locale = Locale.CHINA;
        } else if (Locale.CHINA.toString().equals(lang)) {
            locale = Locale.CHINA;
        } else if (Locale.US.toString().equals(lang)) {
            locale = Locale.US;
        } else if (Locale.KOREA.toString().equals(lang)) {
            locale = Locale.KOREA;
        } else if (Spain.toString().equals(lang)) {
            locale = Spain;
        } else if (russia.toString().equals(lang)) {
            locale = russia;
        } else if (Viet.toString().equals(lang)) {
            locale = Viet;
        } else if (tr.toString().equals(lang)) {
            locale = tr;
        } else if (Locale.TAIWAN.toString().equals(lang)) {
            locale = Locale.TAIWAN;
        } else if (Locale.JAPAN.toString().equals(lang)) {
            locale = Locale.JAPAN;
        } else if (indiaLocale.toString().equals(lang)) {
            locale = indiaLocale;
        } else if (th.toString().equals(lang)) {
            locale = th;
        } else if (SIMPLIFIED_CHINESE.toString().equals(lang)) {
            locale = SIMPLIFIED_CHINESE;
        } else if (TRADITIONAL_CHINESE.toString().equals(lang)) {
            locale = TRADITIONAL_CHINESE;
        } else if (ENGLISH.toString().equals(lang)) {
            locale = ENGLISH;
        } else if (KOREAN.toString().equals(lang)) {
            locale = KOREAN;
        } else if (JAPANESE.toString().equals(lang)) {
            locale = JAPANESE;
        }else if (Arabia.toString().equals(lang)) {
            locale = Arabia;
        }
        LocaleContextHolder.setLocale(locale);
    }

    public static void main(String[] args) {
        System.out.println(MessagesUtils.TRADITIONAL_CHINESE.toString());
    }
}


