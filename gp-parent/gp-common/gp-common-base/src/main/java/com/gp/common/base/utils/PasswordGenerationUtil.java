package com.gp.common.base.utils;
import java.util.Random;

/**
 * 口令密码生成工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
public class PasswordGenerationUtil {

   public static String getRandomBylength(Integer length){
       StringBuilder sb = new StringBuilder(length);
       Random random = new Random();
       char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

       for (int i = 0; i < length; i++) {
           char c = chars[random.nextInt(chars.length)];
           sb.append(c);
       }

       String randomString = sb.toString();
       return  randomString;
   }

}
