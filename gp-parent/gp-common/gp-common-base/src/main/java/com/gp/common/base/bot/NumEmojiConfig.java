package com.gp.common.base.bot;

import cn.hutool.core.collection.CollUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/17/017 19:06
 */
public class NumEmojiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String zero = "0️⃣";
    public static final String one = "1️⃣";
    public static final String two = "2️⃣";
    public static final String three = "3️⃣";
    public static final String four = "4️⃣";
    public static final String five = "5️⃣";
    public static final String six = "6️⃣";
    public static final String seven = "7️⃣";
    public static final String eight = "8️⃣";
    public static final String nine = "9️⃣";
    public static final String ten = "\uD83D\uDD1F";

    public static final List<Integer> nums = CollUtil.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);


    public static final Map<Integer, String> emojiType = new HashMap<>();
    static {
        emojiType.put(0, zero);
        emojiType.put(1, one);
        emojiType.put(2, two);
        emojiType.put(3, three);
        emojiType.put(4, four);
        emojiType.put(5, five);
        emojiType.put(6, six);
        emojiType.put(7, seven);
        emojiType.put(8, eight);
        emojiType.put(9, nine);
        emojiType.put(10, ten);
    }
}
