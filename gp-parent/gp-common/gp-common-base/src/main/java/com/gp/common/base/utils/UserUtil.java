package com.gp.common.base.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2021/10/19 16:46
 */
@Data
public class UserUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    //当前仅PG, 熊猫体育, 以后禁用
    //5位的
    public static String getGameUserId(Integer appId, Long userId) {
        return String.format("1%04d", appId) + userId;
    }

    //之后使用的
    //给用户ID位数限制的使用
    //spribe, EVO
    //3位的
    public static Integer getGameUserAppId(String gameUserId) {
        String appIdStr = gameUserId.substring(1, 5);
        return Integer.parseInt(appIdStr);
    }

    //5位的
    public static Long getUserId(String gameUserId) {
        return Long.parseLong(gameUserId.substring(5));
    }

    //之后使用的
    //给用户ID位数限制的使用
    //spribe, EVO
    //3位的
    public static String getGameUserIdPlus(Integer appId, Long userId) {
        return String.format("1%02d", appId) + userId;
    }

    //之后使用的
    //给用户ID位数限制的使用
    //spribe, EVO
    //3位的
    public static Integer getGameUserAppIdPlus(String gameUserId) {
        try {
            return Integer.parseInt(gameUserId.substring(1, 3));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
    //之后使用的
    //给用户ID位数限制的使用
    //spribe, EVO
    //3位的
    public static String getGameUserAppIdPlusStr(String gameUserId) {
        try {
            return Integer.parseInt(gameUserId.substring(1, 3)) + "";
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    //3位的
    public static Long getUserIdPlus(String gameUserId) {
        try {
            return Long.parseLong(gameUserId.substring(3));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String gameUserId = UserUtil.getGameUserIdPlus(1, 3L);
        System.out.println(gameUserId);
        System.out.println(UserUtil.getGameUserAppIdPlus(gameUserId));
        System.out.println(UserUtil.getUserIdPlus(gameUserId));
    }
}
