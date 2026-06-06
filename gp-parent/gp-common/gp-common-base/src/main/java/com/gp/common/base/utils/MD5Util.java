package com.gp.common.base.utils;

import cn.hutool.crypto.SecureUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * MD5 加密工具
 */
public class MD5Util {
    /**
     * MD5 32位加密
     *
     * @return String
     */
    public MD5Util() {
    }

    public static String getStr(String value) {
        return SecureUtil.md5(value);
    }
    public static String getSalt() {
        String salt = RandomStringUtils.randomAlphanumeric(4);
        return salt;
    }

    public static String encryptSalt(String passWord,String salt) {
        String saltedPassword = salt + passWord;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            // 返回加密后的密码
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String encrypt(String source) {
        return encodeMd5(source.getBytes());
    }


    private static String encodeMd5(byte[] source) {
        try {
            return encodeHex(MessageDigest.getInstance("MD5").digest(source));
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalStateException(var2.getMessage(), var2);
        }
    }

    private static String encodeHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 255) < 16) {
                buffer.append("0");
            }

            buffer.append(Long.toString((long) (bytes[i] & 255), 16));
        }

        return buffer.toString();
    }
    public static void main(String[] args) {
        System.out.println(getStr("123456"));
    }


}
