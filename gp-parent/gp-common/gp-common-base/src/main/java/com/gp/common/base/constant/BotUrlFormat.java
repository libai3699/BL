package com.gp.common.base.constant;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author axing
 * @version 1.0
 * @date 2023/12/15/015 13:30
 */
public class BotUrlFormat implements Serializable {

    private static final long serialVersionUID = 1L;

    //群组用户使用
    public static final String chatUrlFormat = "<a href=\"https://t.me/{}\">{}</a>  (ID : <code>{} </code>)";
    public static final String chatUrlFormatCustomer = "<a href=\"https://t.me/{}\">{}</a>";
    //群组消息使用
    public static final String msgUrlFormat = "<a href=\"https://t.me/{}/{}\">{}</a>";
    //链接加标题
    public static final String adUrlFormat = "<a href=\"{}\">{}</a>";

    public static final String whatsAppUrl = "<a href=\"https://wa.me/{}\">{}</a>";


    public static String getUserName(String username, String name,Long userId){
        return StrUtil.isBlank(username) ? name : StrUtil.format(BotUrlFormat.chatUrlFormat, username, name,userId);
    }
    public static String getCustomerUserName(String username, String name){
        if(username.contains("http")){
            return StrUtil.isBlank(username) ? name : StrUtil.format(BotUrlFormat.adUrlFormat, username, name);
        }
        return StrUtil.isBlank(username) ? name : StrUtil.format(BotUrlFormat.chatUrlFormatCustomer, username, name);

    }

    public static String getCustomerWhatsApp(String username, String name){
        if(username.contains("http")){
            return StrUtil.isBlank(username) ? name : StrUtil.format(BotUrlFormat.adUrlFormat, username, name);
        }
        return StrUtil.isBlank(username) ? name : StrUtil.format(BotUrlFormat.whatsAppUrl, username, name);

    }
    public static String getLinkUrl(String link, String title){
        return StrUtil.format(BotUrlFormat.adUrlFormat, link, title);
    }

    public static String getHref(String htmlString){
        Pattern pattern = Pattern.compile("<a\\s+href=\"([^\"]+)\">");
        Matcher matcher = pattern.matcher(htmlString);
        String href = "";
        if (matcher.find()) {
             href = matcher.group(1);
        }
        return href;
    }
}
