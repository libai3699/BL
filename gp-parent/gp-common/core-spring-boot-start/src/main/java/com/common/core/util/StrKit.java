package com.common.core.util;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类StrKit.
 */
public class StrKit {
	private static final String[] STR_ARRAY = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
			"P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	private static String CHN_NUMBER[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
	private static String CHN_UNIT[] = {"", "十", "百", "千"}; // 权位
	private static String CHN_UNIT_SECTION[] = {"", "万", "亿", "万亿"}; // 节权位
	
	
	
	public static void main(String[] args) {
		System.out.println(numToChinese("9"));
		System.out.println(numToChinese("10"));
		System.out.println(numToChinese("11"));
		System.out.println(numToChinese("19"));
		System.out.println(numToChinese("29"));
		System.out.println(numToChinese("100809"));
	}
	
	/**包含中文**/
	private static Pattern CONTAIN_CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
	/**手机号**/
	private static Pattern MOBILE_PATTERN = Pattern.compile("^[1][0-9]{10}$");
	/***emoji 表情过滤表达式*/
	private static Pattern EMOJI_PATTERN = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
	
	/**
	 * 过滤emoji表情
	 * @param source
	 * @return
	 */
	public static String filterEmoji(String source) {
		if (source != null) {
			Matcher emojiMatcher = EMOJI_PATTERN.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll("");
				return source;
			}
			return source;
		}
		return source;
	}
	
	/**
	 * 阿拉伯数字转换成中文数字,注意不处理带小数点的
	 *
	 * @return
	 */
	public static String numToChinese(String number) {
		int num = 0;
		if (notBlank(number)) {
			try {
				num = Integer.valueOf(number);
			} catch (Exception e) {
				return number;
			}
		}
		StringBuffer returnStr = new StringBuffer();
		Boolean needZero = false;
		int pos = 0; // 节权位的位置
		if (num == 0) {
			// 如果num为0，进行特殊处理。
			returnStr.insert(0, CHN_NUMBER[0]);
		}
		boolean needReplace = (num > 9 && num < 20);
		while (num > 0) {
			int section = num % 10000;
			if (needZero) {
				returnStr.insert(0, CHN_NUMBER[0]);
			}
			String sectionToChn = sectionNumToChn(section);
			// 判断是否需要节权位
			sectionToChn += (section != 0) ? CHN_UNIT_SECTION[pos] : CHN_UNIT_SECTION[0];
			returnStr.insert(0, sectionToChn);
			needZero = ((section < 1000 && section > 0) ? true : false); // 判断section中的千位上是不是为零，若为零应该添加一个零。
			pos++;
			num = num / 10000;
		}
		if (needReplace) {
			return returnStr.toString().replace("一十", "十");
		}
		return returnStr.toString();
	}
	/**
	 * 将四位的section转换为中文数字
	 * 
	 * @param section
	 * @return
	 */
	public static String sectionNumToChn(int section) {
		StringBuffer returnStr = new StringBuffer();
		int unitPos = 0; // 节权位的位置编号，0-3依次为个十百千;
		Boolean zero = true;
		while (section > 0) {
			int v = (section % 10);
			if (v == 0) {
				if ((section == 0) || !zero) {
					zero = true; /* 需要补0，zero的作用是确保对连续的多个0，只补一个中文零 */
					returnStr.insert(0, CHN_NUMBER[v]);
				}
			} else {
				zero = false; // 至少有一个数字不是0
				StringBuffer tempStr = new StringBuffer(CHN_NUMBER[v]);// 数字v所对应的中文数字
				tempStr.append(CHN_UNIT[unitPos]); // 数字v所对应的中文权位
				returnStr.insert(0, tempStr);
			}
			unitPos++; // 移位
			section = section / 10;
		}
		return returnStr.toString();
	}
	/**
	 * 隐藏手机号中间位
	 * 
	 * @param userTel
	 * @return
	 */
	public static String hideTel(String userTel) {
		int hideNum = 4;
		if (userTel.length() < 11) {
			hideNum = 3;
		}
		return userTel.replaceAll("(\\d{3})\\d{" + hideNum + "}(\\d{4})", "$1****$2");
	}
	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	/**
	 * 字符串为 null 或者为 "" 时返回 true
	 */
	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}
	/**
	 * 字符串为null 则返回"" ,不为null则返回原字符
	 * 
	 * @param str
	 * @return
	 */
	public static String notNull(String str) {
		return str == null ? "" : str.trim();
	}
	/**
	 * 字符串不为 null 而且不为 "" 时返回 true
	 */
	public static boolean notBlank(String str) {
		return str != null && !"".equals(str.trim());
	}
	
	
	public static boolean notBlank(String... strings) {
		if (strings == null){
			return false;
		}
		for (String str : strings){
			if (str == null || "".equals(str.trim())){
				return false;
			}
		}
		return true;
	}
	
	
	public static boolean notNull(Object... paras) {
		if (paras == null){
			return false;
		}
		for (Object obj : paras){
			if (obj == null){
				return false;
			}
		}
		return true;
	}
	
	public static String toCamelCase(String stringWithUnderline) {
		if (stringWithUnderline.indexOf('_') == -1){
			return stringWithUnderline;
		}
		stringWithUnderline = stringWithUnderline.toLowerCase();
		char[] fromArray = stringWithUnderline.toCharArray();
		char[] toArray = new char[fromArray.length];
		int j = 0;
		for (int i = 0; i < fromArray.length; i++) {
			if (fromArray[i] == '_') {
				// 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
				i++;
				if (i < fromArray.length){
					toArray[j++] = Character.toUpperCase(fromArray[i]);
				}
			} else {
				toArray[j++] = fromArray[i];
			}
		}
		return new String(toArray, 0, j);
	}
	public static String join(String[] stringArray) {
		StringBuilder sb = new StringBuilder();
		for (String s : stringArray){
			sb.append(s);
		}
		return sb.toString();
	}
	public static String join(String[] stringArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < stringArray.length; i++) {
			if (i > 0){
				sb.append(separator);
			}
			sb.append(stringArray[i]);
		}
		return sb.toString();
	}
	public static boolean isEmpty(Object strObj) {
		return strObj == null || strObj.toString().trim().length() < 1;
	}
	
	
	
	public static String getRandonString(int strLenth) {
		String randomStr = "";
		Random random = new Random();
		for (int i = 0; i < strLenth; i++) {
			randomStr += STR_ARRAY[random.nextInt(STR_ARRAY.length)];
		}
		return randomStr;
	}
	
	/**
	 * 获取随机生成长度的数字字符串
	 * @param strLenth
	 * @return
	 */
	public static String getRandonNum(int strLenth) {
		String randomStr = "";
		Random random = new Random();
		for (int i = 0; i < strLenth; i++) {
			randomStr += random.nextInt(9);
		}
		return randomStr;
	}
	
	
	/**
	 * 判断一个字符在字符串中出现的次数
	 * 
	 * @param str
	 * @param token
	 * @return
	 */
	public static int countToken(String str, String token) {
		int count = 0;
		while (str.indexOf(token) != -1) {
			count++;
			str = str.substring(str.indexOf(token) + token.length());
		}
		return count;
	}
	/**
	 * 合并，去重复，如：str1=1,2,3 str2=2,4 合并后返回 1,2,3,4
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String mergeRemoveRepeatBySplit(String str1, String str2) {
		if (isBlank(str1) && isBlank(str2)) {
			return "";
		}
		if (isBlank(str1)) {
			return str2;
		}
		if (isBlank(str2)) {
			return str1;
		}
		String[] arr1 = str1.split(",");
		String[] arr2 = str2.split(",");
		List<String> list = new ArrayList<String>();
		for (String s : arr1) {
			list.add(s);
		}
		for (String s2 : arr2) {
			boolean exists = false;
			for (String s1 : arr1) {
				if (s2.equals(s1)) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				list.add(s2);
			}
		}
		String temp = "";
		for (String string : list) {
			temp += string + ",";
		}
		return temp.substring(0, temp.length() - 1);
	}
	public static boolean isNum(String strSrc) {
		for (int i = 0; i < strSrc.length(); i++) {
			if (!isCharNum(strSrc.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	public static boolean isCharNum(char ch) {
		return ch > '/' && ch < ':';
	}
	
	
	
	/**
	 * 验证是否为正确手机号
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		boolean b = false;
		if (notBlank(str)) {
			Matcher m = MOBILE_PATTERN.matcher(str);
			b = m.matches();
		}
		return b;
	}
	/**
	 * 判断传入字符是否为中文
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCNChar(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}
	/**
	 * 判断是否包含中文
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isContainChinese(String str) {
		
		Matcher m = CONTAIN_CHINESE_PATTERN.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	/**
	 * 判断字符长度,中文为2个长度,英文为1个长度
	 * 
	 * @param s
	 * @return
	 */
	public static int cnLength(String s) {
		if (s == null){
			return 0;
		}
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isCNChar(c[i])) {
				len++;
			}
		}
		return len;
	}
	public static boolean isTrimedEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}
	// 过滤HTML标签
	public static String stripHtml(String content) {
		// <p>段落替换为换行
		content = content.replaceAll("<p .*?>", "\r\n");
		// <br><br/>替换为换行
		content = content.replaceAll("<br\\s*/?>", "\r\n");
		// 去掉其它的<>之间的东西
		content = content.replaceAll("\\<.*?>", "");
		// 还原HTML
		// content = HTMLDecoder.decode(content);
		return content;
	}
	/**
	 * 获取字符串字节长度
	 * 
	 * @param str
	 * @return
	 */
	public static int getStrByteLength(String str) {
		try {
			if (!StrKit.isBlank(str)) {
				return str.getBytes("GBK").length;
			}
		} catch (UnsupportedEncodingException e) {
		}
		return -1;
	}
	/**
	 * 根据长度分割字符串
	 * 
	 * @param inputString
	 * @param length
	 * @return
	 */
	public static List<String> getStrList(String inputString, int length) {
		int size = inputString.length() / length;
		if (inputString.length() % length != 0) {
			size += 1;
		}
		return getStrList(inputString, length, size);
	}
	private static List<String> getStrList(String inputString, int length, int size) {
		List<String> list = new ArrayList<String>();
		for (int index = 0; index < size; index++) {
			String childStr = substring(inputString, index * length, (index + 1) * length);
			list.add(childStr);
		}
		return list;
	}
	private static String substring(String str, int f, int t) {
		if (f > str.length()){
			return null;
		}
		if (t > str.length()) {
			return str.substring(f, str.length());
		} else {
			return str.substring(f, t);
		}
	}
	public static String subString(String source, int start, int end) {
		String result;
		try {
			result = source.substring(source.offsetByCodePoints(0, start), source.offsetByCodePoints(0, end));
		} catch (Exception e) {
			result = "";
		}
		return result;
	}
	
	/**
	 * 
    * @Title: getLen
    * @Description: (返回字符长度)
    * @param str
    * @return int    返回类型
    *
	 */
	public static int getLen(String str){
		if(StrKit.isBlank(str)){
			return 0;
		}
		return str.length();
	}
	
	/**
	 * 
    * @Title: getSplitLen
    * @Description: (获取分割之后的字符长度)
    * @param str
    * @param tag
    * @return int    返回类型
    *
	 */
	public static int getSplitLen(String str,String tag){
		if(StrKit.isBlank(str)){
			return 0;
		}
		return str.split(tag).length;
	}

	public static int getCharacterPosition(String string,String reg,int times){
		Matcher slashMatcher = Pattern.compile(reg).matcher(string);
		int mIdx = 0;
		while(slashMatcher.find()) {
			mIdx++;
			if(mIdx == times){
				break;
			}
		}
		return slashMatcher.start();
	}
}
