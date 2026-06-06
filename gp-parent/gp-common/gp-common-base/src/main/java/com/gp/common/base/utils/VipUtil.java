package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;
import com.gp.common.base.constant.EmojiCons;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author axing
 * @version 1.0
 * @date 2022/12/29 22:11
 */
public class VipUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String pattern = "(https?://[^/]+)";


    /**
     * 添加或者替换域名
     *
     * @param vip   vip
     * @return {@link String}
     */
    public static String getVipEmoji(Integer vip) {
        if (vip == 1){
            return EmojiCons.LV1;
        }else if (vip == 2){
            return EmojiCons.LV2;
        }else if (vip == 3){
            return EmojiCons.LV3;
        }else if (vip == 4){
            return EmojiCons.LV4;
        }else if (vip == 5){
            return EmojiCons.LV5;
        }else {
            return EmojiCons.LV1;
        }
    }


}
