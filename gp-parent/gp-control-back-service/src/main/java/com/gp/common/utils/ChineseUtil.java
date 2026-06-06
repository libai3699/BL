package com.gp.common.utils;

import java.io.Serializable;
import java.util.regex.Pattern;

import lombok.Data;

/**
 * @author axing
 * @version 1.0
 * @date 2022/7/20 21:38:56
 */
@Data
public class ChineseUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 判断字符串中是否包含中文汉字
     *
     * @param content
     * @return true至少包含1个
     */
    public static boolean hasChinese(CharSequence content) {
        if (null == content) {
            return false;
        }
        String regex = "[\u2E80-\u2EFF\u2F00-\u2FDF\u31C0-\u31EF\u3400-\u4DBF\u4E00-\u9FFF\uF900-\uFAFF\uD840\uDC00-\uD869\uDEDF\uD869\uDF00-\uD86D\uDF3F\uD86D\uDF40-\uD86E\uDC1F\uD86E\uDC20-\uD873\uDEAF\uD87E\uDC00-\uD87E\uDE1F]+";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content).find();
    }

    /**
     * 判断字符串是否为中文汉字
     *
     * @param content
     * @return true都是汉字
     */
    public static boolean isChinese(CharSequence content) {
        if (null == content) {
            return false;
        }
        String regex = "[\u2E80-\u2EFF\u2F00-\u2FDF\u31C0-\u31EF\u3400-\u4DBF\u4E00-\u9FFF\uF900-\uFAFF\uD840\uDC00-\uD869\uDEDF\uD869\uDF00-\uD86D\uDF3F\uD86D\uDF40-\uD86E\uDC1F\uD86E\uDC20-\uD873\uDEAF\uD87E\uDC00-\uD87E\uDE1F]+";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content).matches();
    }

}
