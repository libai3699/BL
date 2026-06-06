package com.gp.common.base.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 域名混淆加密工具类（Base64 + 偏移 + 翻转，兼容 JS atob 解码）
 */
public class DomainObfuscatorV2 {

    // 固定 Base64 字符集（用于偏移映射）
    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    // 前后端共享的复杂 Key（用于派生偏移）
    private static final String COMPLEX_KEY = "hJFOcaQn386MUd5Z";

    private static int[] getKeyOffsets(String key) {
        int[] offsets = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            offsets[i] = key.charAt(i) % 7;
        }
        return offsets;
    }

    /**
     * 加密（输出仍为 Base64 合法字符）
     */
    public static String encode(String raw) {
        String base64 = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        int[] keyOffsets = getKeyOffsets(COMPLEX_KEY);
        int keyLen = keyOffsets.length;

        StringBuilder sb = new StringBuilder();
        for (int i = base64.length() - 1; i >= 0; i--) {
            char c = base64.charAt(i);
            int index = BASE64_CHARS.indexOf(c);
            if (index == -1) throw new IllegalArgumentException("非法 Base64 字符: " + c);

            int shift = keyOffsets[i % keyLen];
            int newIndex = (index + shift) % BASE64_CHARS.length();
            sb.append(BASE64_CHARS.charAt(newIndex));
        }
        return sb.toString();
    }

    /**
     * 解密（用于后端验证或调试）
     */
    public static String decode(String encrypted) {
        int[] keyOffsets = getKeyOffsets(COMPLEX_KEY);
        int keyLen = keyOffsets.length;

        StringBuilder base64Builder = new StringBuilder();
        for (int i = encrypted.length() - 1; i >= 0; i--) {
            char c = encrypted.charAt(i);
            int index = BASE64_CHARS.indexOf(c);
            if (index == -1) throw new IllegalArgumentException("非法混淆字符: " + c);

            int shift = keyOffsets[(encrypted.length() - 1 - i) % keyLen];
            int originalIndex = (index - shift + BASE64_CHARS.length()) % BASE64_CHARS.length();
            base64Builder.append(BASE64_CHARS.charAt(originalIndex));
        }

        byte[] decodedBytes = Base64.getDecoder().decode(base64Builder.toString());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String origin = "https://gateway.jrtxa.com,https://gateway.rwtna.com,https://gateway.cxin8866.com";
        String encrypted = encode(origin);
        String decrypted = decode(encrypted);

        System.out.println("原始:   " + origin);
        System.out.println("加密:   " + encrypted);
        System.out.println("解密:   " + decrypted);
    }
}
