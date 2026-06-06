package com.gp.common.base.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.SneakyThrows;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class EncryptDecryptUtil {
    private static final String AES_ALGORITHM = "AES";

    @SneakyThrows
    public static String decryptAES(String data, String key) {
        AES aes = new AES(Mode.CBC, Padding.NoPadding,
                new SecretKeySpec(key.getBytes(), AES_ALGORITHM),
                new IvParameterSpec(key.getBytes()));
        byte[] result = aes.decrypt(Base64.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8);
    }

//	public static void main(String[] args) {
//		System.out.println(encryptAES("12345678", "pigxpigxpigxpigx"));
//	}


    @SneakyThrows
    public static String encryptAES(String data, String key) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding,
                new SecretKeySpec(key.getBytes(), AES_ALGORITHM),
                new IvParameterSpec(key.substring(0, 16).getBytes()));
        return aes.encryptBase64(data, StandardCharsets.UTF_8);
    }


    @SneakyThrows
    public static String encryptAES(String data, String key, String iv) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding,
                new SecretKeySpec(key.getBytes(), AES_ALGORITHM),
                new IvParameterSpec(iv.getBytes()));
        return aes.encryptBase64(data, StandardCharsets.UTF_8);
    }


}
