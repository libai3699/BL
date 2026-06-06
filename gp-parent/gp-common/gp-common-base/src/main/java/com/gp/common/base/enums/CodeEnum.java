package com.gp.common.base.enums;

import com.google.common.collect.Maps;
import com.gp.common.base.utils.MessagesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

/**
 * @author axing
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Slf4j
public enum CodeEnum {
    /**
     * 成功
     */
    OK(200, "bot.code.success"),
    /**
     * 失败
     */
    Error(500, "bot.code.error"),
    /**
     * 失败
     */
    Fail(500, "bot.code.fail"),
    /**
     * 未登录,请先登录
     */
    ERR_1(-1, "bot.code.invalidToken"),

    /**
     * 登录超时
     */
    ERR_2(-2, "bot.code.invalidToken"),
    ERR_3(-3, "bot.code.expiredToken"),
    ERR_4(-4, "bot.code.tokenDXX"),
    ERR_5(-5, "bot.code.tokenTXX"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS(-6, "bot.code.USER_NOT_EXISTS"),

    USER_PASSWORD_FAILED(-7, "bot.code.USER_PASSWORD_FAILED"),
    USER_EXISTS(-8, "bot.code.USER_EXISTS"),
    /**
     * 未一级认证
     */
    ERR_501(501, "bot.code.WYJRZ"),
    /**
     * 未设置密码
     */
    ERR_502(502, "bot.code.YHMMWSZ"),
    /**
     * 未与机器人交互
     */
    ERR_503(503, "bot.code.BOTTZ"),
    /**
     * 余额不足
     */
    ERR_MONEY_INSUFFICIENT(504, "bot.changeAmount.YEBZ"),
    /**
     * 余额不足
     */
    NO_OPEN(505, "bot.code.noOpen"),
    ;

    private Integer code;
    private String message;

    private static Map<Integer, CodeEnum> codeEnumMap;

    static {
        codeEnumMap = Maps.newHashMap();
        for (CodeEnum upPlatformEnum : values()) {
            codeEnumMap.put(upPlatformEnum.getCode(), upPlatformEnum);
        }
    }
    public static CodeEnum getCodeEnum(Integer code) {
        return codeEnumMap.get(code);
    }
    public static String getMessage(Integer code) {
        log.info("LocaleContextHolder.getLocale().toString(),{}", LocaleContextHolder.getLocale().toString());
        CodeEnum[] values = values();
        for (CodeEnum codeEnum : values) {
            if (codeEnum.getCode().equals(code)) {
                String language = LocaleContextHolder.getLocale().toString();
                MessagesUtils.setLang(language);
                return MessagesUtils.get(codeEnum.getMessage());
            }
        }
        return null;
    }

}
