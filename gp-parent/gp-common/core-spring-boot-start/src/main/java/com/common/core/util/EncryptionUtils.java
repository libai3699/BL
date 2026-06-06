package com.common.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtils {

    private static final String AES_SECRET_KEY = "YourSecretKey"; // 16, 24, or 32 bytes

    public static String extendKey(String key, int length) {
        StringBuilder sb = new StringBuilder(length);
        sb.append(key);
        while (sb.length() < length) {
            sb.append("0");
        }
        return sb.toString();
    }

    public static String encrypt(String data, String secretKey) {
//        String encryptedBytes = data;
        String returnStr = data;
        try {
            SecretKeySpec key = new SecretKeySpec(extendKey(secretKey, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = new byte[0];
            encryptedBytes = cipher.doFinal(data.getBytes());
            returnStr = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.out.println(e);
        }

        return returnStr;
    }

    public static String decrypt(String encryptedData, String secretKey) {
        String returnStr = encryptedData;
        try {
            SecretKeySpec key = new SecretKeySpec(extendKey(secretKey, 16).getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            returnStr = new String(decryptedBytes);
        } catch (Exception e) {

        }
        return new String(returnStr);
    }

    public static void main(String[] args) throws Exception {
        String originalData = "3B388B7F3068408B895CCBE4518A4428";
        System.out.println("Original Data: " + originalData.length());
        String encryptedData = encrypt(originalData, AES_SECRET_KEY);
        System.out.println("Encrypted Data: " + encryptedData);
        String decryptedData = decrypt(encryptedData, AES_SECRET_KEY);
        System.out.println("Decrypted Data: " + decryptedData);
    }


}
