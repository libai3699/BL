package com.gp.common.base.utils;//package com.gp.common.base.utils;
//
//import lombok.Data;
//import net.sourceforge.pinyin4j.PinyinHelper;
//import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
//import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.Serializable;
//
///**
// * @author fs
// * @version 1.0
// * @date 2022/4/10 16:36
// */
//@Data
//public class ChineseToPinyinUtil implements Serializable {
//
//    /**
//     * 获得汉语拼音首字母
//     *
//     * @param chines 汉字
//     * @return
//     */
//    public static String getAlpha(String chines) {
//        String pinyinName = "";
//        char[] nameChar = chines.toCharArray();
//        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
//        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        for (int i = 0; i < nameChar.length; i++) {
//            if (nameChar[i] > 128) {
//                try {
//                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
//                            nameChar[i], defaultFormat)[0].charAt(0);
//                } catch (BadHanyuPinyinOutputFormatCombination e) {
//                    e.printStackTrace();
//                }
//            } else {
//                pinyinName += nameChar[i];
//            }
//        }
//        return pinyinName;
//    }
//
//    /**
//     * 获得第一个汉字的首字母
//     *
//     * @param chines 汉字
//     * @return
//     */
//    public static String getFirstChineseInitials(String chines) {
//        String result = getAlpha(chines);
//        return result.substring(0, 1);
//    }
//
//    /**
//     * 将字符串中的中文转化为拼音,英文字符不变
//     *
//     * @param inputString 汉字
//     * @return
//     */
//    public static String getPingYin(String inputString) {
//        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
//        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        format.setVCharType(HanyuPinyinVCharType.WITH_V);
//        String output = "";
//        if (inputString != null && inputString.length() > 0
//                && !"null".equals(inputString)) {
//            char[] input = inputString.trim().toCharArray();
//            try {
//                for (int i = 0; i < input.length; i++) {
//                    if (Character.toString(input[i]).matches(
//                            "[\\u4E00-\\u9FA5]+")) {
//                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
//                                input[i], format);
//                        output += temp[0];
//                    } else
//                        output += Character.toString(input[i]);
//                }
//            } catch (BadHanyuPinyinOutputFormatCombination e) {
//                e.printStackTrace();
//            }
//        } else {
//            return "*";
//        }
//        return output;
//    }
//
//    /**
//     * 汉字转换为汉语拼音首字母，英文字符不变
//     *
//     * @param chines 汉字
//     * @return 拼音
//     */
//    public static String converterToFirstSpell(String chines) {
//        String pinyinName = "";
//        char[] nameChar = chines.toCharArray();
//        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
//        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//        for (int i = 0; i < nameChar.length; i++) {
//            if (nameChar[i] > 128) {
//                try {
//                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
//                            nameChar[i], defaultFormat)[0].charAt(0);
//                } catch (BadHanyuPinyinOutputFormatCombination e) {
//                    e.printStackTrace();
//                }
//            } else {
//                pinyinName += nameChar[i];
//            }
//        }
//        return pinyinName;
//    }
//
//    /**
//     * 将字符串转换成ASCII码
//     *
//     * @param src
//     * @return String
//     */
//    public static String getCnASCII(String src) {
//        if (StringUtils.isEmpty(src)) {
//            return null;
//        }
//        // 将字符串转换成字节序列
//        byte[] bytes = src.getBytes();
//        final StringBuffer buffer = new StringBuffer(bytes.length);
//        for (byte index : bytes) {
//            // 将每个字符转换成ASCII码
//            buffer.append(Integer.toHexString(index & 0xff));
//        }
//        return buffer.toString();
//    }
//
//    /**
//     * 判定输入的是否是汉字
//     *
//     * @param c 被校验的字符
//     * @return true代表是汉字
//     */
//    public static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
//            return true;
//        }
//        return false;
//    }
//
//    public static void main(String[] args) {
//        String a = getAlpha("士大夫十分");
//        String b = getFirstChineseInitials("法国热热");
//    }
//}
