package com.common.elasticsearch.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.emoji.EmojiUtil;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/16/016 15:43
 */
public class StringUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    //j将所有的换行符号和emoji符号都替换掉
    public static String getEffectiveStr(String str) {
        if (StrUtil.isBlank(str)) {
            return "";
        }else {
            return StrUtil.removeAllLineBreaks(EmojiUtil.removeAllEmojis(str));
        }
    }

}
