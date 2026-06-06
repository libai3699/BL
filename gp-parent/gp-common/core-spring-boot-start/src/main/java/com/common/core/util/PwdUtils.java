package com.common.core.util;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: c
 * @Date: 2025/2/24 下午 02:24
 **/
public class PwdUtils {
    private static final Logger log = LoggerFactory.getLogger(PwdUtils.class);

    /**
     * 验证密码复杂度(8-16位，大写字母、小写字母、数字、特殊字符，至少包含2种)
     *
     * @param password 密码
     * @return true/false
     */
    public static boolean validatePassword(String password) {
        // 8-16位，大写字母、小写字母、数字、特殊字符，至少包含2种
        String pattern = "^(?=.*[A-Za-z\\d!@#$%^&*()_+]*([A-Za-z][!@#$%^&*()_+]|[!@#$%^&*()_+][A-Za-z]|\\d" +
                ".*[A-Za-z!@#$%^&*()_+]|[A-Za-z!@#$%^&*()_+].*\\d)).{8,16}$";
        return password.matches(pattern);
    }
    /**
     * 验证账号格式（6位以上中英文数字）
     *
     * @param account 账号
     * @return true/false
     */
    public static boolean validateAccount(String account) {
        // 检查是否为空
        if (StrUtil.isBlank(account)) {
            return false;
        }
        // 检查长度不能小于6位
        if (account.length() < 6) {
            return false;
        }
        // 验证只能包含中文、英文、数字
        return account.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
    }
    public static void main(String[] args) {
        String password = "hero123";
        if (validateAccount(password)) {
            log.info("复杂度验证通过");
        } else {
            log.info("复杂度验证不通过");
        }
    }
}
