package com.gp.service.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.gp.common.mybatisplus.entity.TUser;
import lombok.Data;

import java.io.Serializable;


/**
 * @author axing
 * @version 1.0
 * @date 2022/7/18 13:27:12
 */
@Data
public class SaUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String USER = "USER";

    /**
     * 隐身状态, 0 未隐身, 1 隐身
     */
    public static final String YINSHEN_STATUS = "YINSHEN_STATUS";
    public static final String USER_ID = "USER_ID";
    public static final String ROLE = "ROLE";
    public static final String PERMISSION = "PERMISSION";
    public static final String MENU = "MENU";
    public static final String master = "master";
    public static final String userKeyFormat = "{}:tg:front:{}";


    /**
     * 登录
     *
     * @param tUser 成员
     * @return {@link String}
     */
    public static String login(TUser tUser,String productId) {
        //登录成功
        StpUtil.login(StrUtil.format(userKeyFormat,productId, tUser.getUserId()));
        //获取token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        //给他加上productId
        tUser.setProductId(productId);
        //存储用户信息
        SaUtil.setUser(tUser);
        SaUtil.setUserId(tUser.getUserId());

        //返回token信息
        return tokenInfo.getTokenValue();
    }

    /**
     * 退出
     *
     * @param tUser 成员
     * @return {@link String}
     */
    public static void logout(TUser tUser) {
        logout(tUser.getUserId());
    }


    /**
     * 退出
     *
     * @return {@link String}
     */
    public static void logout(Long userId) {
        //登录成功
        StpUtil.logout(StrUtil.format(userKeyFormat, userId));
    }
    /**
     * 退出
     *
     * @return {@link String}
     */
    public static void logout() {
        //登录成功
        logout(getUserId());
    }

    /**
     * 通过token获取用户id
     *
     * @param token token
     * @return {@link String}
     */
    public static String getUserId(String token) {
        //客服判断
        Object loginIdByToken = StpUtil.getLoginIdByToken(token);
        if (loginIdByToken != null) {
            String[] split = ((String) loginIdByToken).split(":");
            return split[split.length - 1];
        }else {
            return null;
        }
    }

    public static void setUserId(Long id) {
        StpUtil.getSession().set(USER_ID, id);
    }
    public static Long getUserId() {
        return (Long) StpUtil.getSession().get(USER_ID);
    }

    /**
     * @param tUser 修改缓存中用户信息
     */
    public static void setUser(TUser tUser) {
        StpUtil.getSession().set(USER, tUser);
    }
    public static TUser getUser() {
        return (TUser) StpUtil.getSession().get(USER);
    }


    public static boolean isLogin() {
      return   StpUtil.isLogin();
    }
}
