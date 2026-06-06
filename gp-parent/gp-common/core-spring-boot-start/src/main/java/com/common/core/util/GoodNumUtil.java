package com.common.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2021/8/4 13:07
 */
public class GoodNumUtil {
  

        //6位和7位数正则
        public static List<String>  Regex6() {
             List<String>   levitPatterns = new ArrayList<String>();
           

            // 重复号码，镜子号码
            levitPatterns.add("^(<a>\\d)(\\d)(\\d)\\1\\2\\3$");
            levitPatterns.add("^(\\d)(\\d)(\\d)\\3\\2\\1$");
             //AABB
            levitPatterns.add("^\\d*(\\d)\\1(\\d)\\2\\d*$");
            // AAABBB
            levitPatterns.add("^\\d*(\\d)\\1\\1(\\d)\\2\\2\\d*$");
            // ABABAB
            levitPatterns.add("^(\\d)(\\d)\\1\\2\\1\\2\\1\\2$");
            // ABCABC
            levitPatterns.add("^(\\d)(\\d)(\\d)\\1\\2\\3$");
            // ABBABB
            levitPatterns.add("^(\\d)(\\d)\\2\\1\\2\\2$");
            // AABAAB
            levitPatterns.add("^(\\d)\\1(\\d)\\1\\1\\2$");
            // ABCDABC
            levitPatterns.add("\\b(\\d\\d\\d)\\d(\\1)\\b");

            // 3位以上 重复
            levitPatterns.add("^(\\d)+(?!\\1)(\\d)\\2{2}(?!\\2)\\d*$");
            // 4位以上 位递增或者递减
            levitPatterns.add("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3,}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3,})\\d");
            return levitPatterns;
        }


    //4位和5位数正则
    public static List<String>  Regex5() {
        List<String>   levitPatterns = new ArrayList<String>();


        // 重复号码，镜子号码
        levitPatterns.add("^(\\d)(\\d)\\1\\2$");
        levitPatterns.add("^(\\d)(\\d)\\2\\1$");
        // AABB
        levitPatterns.add("^\\d*(\\d)\\1(\\d)\\2\\d*$");
        // ABAB
        levitPatterns.add("^\\d*(\\d)\\1\\1(\\d)\\2\\2\\d*$");
        // ABCAB
        levitPatterns.add("\\b(\\d\\d)\\d(\\1)\\b");

        // 3位以上 重复
        levitPatterns.add("^(\\d)+(?!\\1)(\\d)\\2{2}(?!\\2)\\d*$");
        // 3位以上 位递增或者递减
        levitPatterns.add("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2,}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2,})\\d");
        return levitPatterns;
    }


    }
