package com.gp.common.base.bot;

import com.gp.common.base.utils.MD5Util;

/**
 * tg唯一值获取工具类
 * @author axing
 */
public class TgUniqueUtil {

    public static final String keyPre = "key_";

    //群组频道的唯一值
    public static String getChatUniqueKey(Long chatId) {
        return MD5Util.getStr(chatId.toString());
    }
    //外链唯一值
    public static String getOutMsg(String content) {
        return MD5Util.getStr(content);
    }
    //附加值唯一值, 例如: 关键词
    public static String getAppendMsg(String pre, Long id) {
        return MD5Util.getStr(pre + id);
    }
    //消息类型唯一值
    public static String getMsgUniqueKey(Long chatId, String messageId) {
        String key = chatId.toString() + "@" + messageId;
        return MD5Util.getStr(key);
    }

}
