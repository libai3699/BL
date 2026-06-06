package com.gp.common.base.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class DjangoPasswordVerifier {

    /**
     * 验证明文密码是否匹配 Django 存储的加密密码
     * @param plainPassword 明文密码
     * @param djangoHash Django 存储的密码字段，如：pbkdf2_sha256$260000$salt$hash
     * @return 是否匹配
     * @throws Exception 发生算法错误
     */
    public static boolean verifyPassword(String plainPassword, String djangoHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = djangoHash.split("\\$");
        if (parts.length != 4 || !parts[0].equals("pbkdf2_sha256")) {
//            throw new IllegalArgumentException("Unsupported or invalid Django hash format");
            //非老的加密格式
            return false;
        }

        int iterations = Integer.parseInt(parts[1]);
        String salt = parts[2];
        String hash = parts[3];

        String computedHash = pbkdf2Sha256(plainPassword, salt, iterations, 32);
        return computedHash.equals(hash);
    }

    /**
     * PBKDF2-HMAC-SHA256 加密
     * @param password 明文密码
     * @param salt 盐
     * @param iterations 迭代次数
     * @param keyLength 期望长度（以字节为单位，Django默认是32字节）
     * @return Base64 编码后的加密结果
     * @throws Exception 发生加密错误
     */
    public static String pbkdf2Sha256(String password, String salt, int iterations, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt.getBytes(StandardCharsets.UTF_8),
                iterations,
                keyLength * 8 // 转换成位数
        );
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    // 示例用法
    public static void main(String[] args) throws Exception {
        String plainPassword = "hello1";
        String djangoHash = "pbkdf2_sha256$870000$MIJUNGUmWrEOlcuH9g8HOv$6F/mJHOnuDBnvomDhItMya0adg+PQH0OzG37yPJJl8Y=";

        boolean matched = verifyPassword(plainPassword, djangoHash);
        System.out.println("密码匹配: " + matched);
    }
}
