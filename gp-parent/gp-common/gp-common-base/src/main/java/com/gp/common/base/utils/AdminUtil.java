package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;
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
public class AdminUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String pattern = "(https?://[^/]+)";


    /**
     * 添加或者替换域名
     *
     * @param url   url
     * @return {@link String}
     */
    public static String updateAdmin(String admin, String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }else if(url.contains("http")){
            return url.replaceAll(pattern, admin);
        }else {
            return admin + url;
        }
    }


    /**
     * 删除域名
     *
     * @param url   url
     * @return {@link String}
     */
    public static String delAdmin(String url) {
        if (StringUtils.isNotBlank(url)){
            return url.replaceAll(pattern,"");
        }else {
            return null;
        }

    }


    /**
     * 添加url, 如果已经有就不操作
     *
     * @param admin
     * @param url   url
     * @return {@link String}
     */
    public static String perfectAdmin(String admin, String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }else if(url.contains("http")){
            return url;
        }else {
            return admin + url;
        }
    }

    public static String getAdmin(String url) {
        Pattern p = Pattern.compile(pattern);// 匹配的模式
        Matcher matcher = p.matcher(url);
        List<String> matchStrs = new ArrayList<>();

        while (matcher.find()) { //此处find（）每次被调用后，会偏移到下一个匹配
            matchStrs.add(matcher.group());//获取当前匹配的值
        }

        return matchStrs.get(0);
    }
}
