package com.gp.common.utils;

import com.alibaba.fastjson.JSON;
import com.common.core.constant.HttpStatus;
import com.gp.common.exception.CustomException;
import com.gp.framework.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class SecurityUtils {

    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            log.info("获取用户账户异常e {}", JSON.toJSONString(e));
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CustomException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }
}
