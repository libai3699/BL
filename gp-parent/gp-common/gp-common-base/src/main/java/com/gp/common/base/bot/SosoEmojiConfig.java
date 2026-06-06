package com.gp.common.base.bot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/17/017 19:06
 */
public class SosoEmojiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String 群组 = "\uD83D\uDC65";
    public static final String 频道 = "\uD83D\uDCE2";
    public static final String 视频 = "\uD83C\uDFAC";
    public static final String 音频 = "\uD83C\uDFB5";
    public static final String 图片 = "\uD83D\uDDBC";
    public static final String 文档 = "\uD83D\uDCC4";
    public static final String 链接 = "\uD83D\uDD17";


    public static final Map<String, Integer> emojiType = new HashMap<>();
    static {
        emojiType.put(群组, 0);
        emojiType.put(频道, 1);
        emojiType.put(视频, 2);
        emojiType.put(音频, 3);
        emojiType.put(图片, 4);
        emojiType.put(文档, 5);
        emojiType.put(链接, 6);
    }
}
