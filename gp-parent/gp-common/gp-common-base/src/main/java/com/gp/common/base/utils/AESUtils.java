package com.gp.common.base.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.util.Base64Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * AES 加解密工具类
 */
public class AESUtils {

    private static AES aes = null;

    private static final String keyStr = "mtS47wRvdE6vyCIalSpW+w==";

    static {
        aes = SecureUtil.aes(Base64Utils.decodeFromString(keyStr));
    }

    public static String getKeyStr() {
        // 生成 AES 密钥
        AES aes = SecureUtil.aes();

        // 获取密钥的字节数组
        byte[] key = aes.getSecretKey().getEncoded();

        // 将密钥转换为 Base64 字符串，方便存储和配置
        String keyStr = Base64Utils.encodeToString(key);

        // 输出 Base64 字符串
        System.out.println(keyStr);
        return keyStr;
    }

    /**
     * 加密
     *
     * @param date 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String encryptStr(String date) {
        return aes.encryptHex(date);
    }

    /**
     * 解密
     *
     * @param date 需要解密的字符串
     * @return 解密后的字符串
     */
    public static String decryptStr(String date) {
        return aes.decryptStr(date);
    }

    public static void main(String[] args) {
//        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
//        String keyType = Base64Utils.encodeToString(key);
//        System.out.println(keyType);
//        AES aes = SecureUtil.aes(Base64Utils.decodeFromString("EKOLQBmkFARZj1m9iGZQYg=="));
//        String path = "C:\\Users\\onday\\Desktop\\ss\\1.txt"; // 替换为你的文件路径
//        StringBuilder contentBuilder = new StringBuilder();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                contentBuilder.append(line).append("\n");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        String fileContent = contentBuilder.toString().trim();
       String fileContent =
               "3ab4855781dae7aed271adb1d234a1e71dbae0a98d526c622b283bd6c264799f96a7c4231bca37e404003c6cc1a3ab1e1f1440d6decc33d322ab5cb094ec486f3570cafb57127a34a101fba7f504f03009a536adfcc52a18e97d97d2a329be21ea3cf0b8b4c03b70beaefd8100efeb794ab6f3d6130ce7853aa21d601f58ce0ac880ac1542af23ad8ae86a80b4ca0f327072a7f486ea203d850229c2106817892fef650a72689abf8b9ea66556a3f9e8b690a1a01110ef6728284e18990b39abbad0274366328974fe1d5540bea05eb338ad8242fca30a45629adb99aa030f76b690a1a01110ef6728284e18990b39abed789c2bd2770c4424567abe5195fb8d";
        String result = SecureUtil.aes("EKOLQBmkFARZj1m9iGZQYg==".getBytes()).decryptStr(fileContent);  //前端
//                String result = SecureUtil.aes("wNbLVFk3YShA4zT8mCbcQA==".getBytes()).decryptStr(fileContent); //后台
//        System.out.println(s);
//        System.out.println(getKeyStr());

        System.out.println(result);
//        System.out.println(result);
//        System.out.println(result3);
    }

}
