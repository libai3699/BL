package com.gp.common.base.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author laowang
 */
public class Utils {


    private static Logger logger = LoggerFactory.getLogger(Utils.class);


    /**
     * 获取今天0点时间
     *
     * @returns {Date}
     */
    public static Date getZeroDate() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    ;

    /**
     * 构造Map对象
     *
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object... params) {
        LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();

        // 无参 返回空即可
        if (params == null || params.length == 0) {
            return result;
        }

        // 处理成对参数
        int len = params.length;
        for (int i = 0; i < len; i += 2) {
            K key = (K) params[i];
            V val = (V) params[i + 1];

            result.put(key, val);
        }

        return result;
    }

    public static JSONObject toJSON(Object... params) {
        JSONObject json = new JSONObject(params.length / 2);
        // 处理成对参数
        for (int i = 0, len = params.length; i < len; i += 2) {
            json.put(params[i].toString(), params[i + 1]);
        }
        return json;
    }

    public static String toJSONString(Object... params) {
        JSONObject json = toJSON(params);
        return json.toJSONString();
    }

    public static <T> T deserialize(String buf, Class<T> clazz) {
        return JSON.parseObject(buf, clazz);
    }

    public static JSONArray toJSONArray(Object... params) {
        JSONArray arr = new JSONArray(params.length);
        // 处理成对参数
        for (int i = 0; i < params.length; i++) {
            arr.add(params[i]);
        }
        return arr;
    }

    /**
     * 打印数组
     *
     * @param ary
     * @param numPerLine 每行打印多少个数
     */
    public static String printArray(int[] ary, int numPerLine) {
        StringBuffer sb = new StringBuffer("");
        if (numPerLine <= 0) numPerLine = 1;
        if (null == ary) {
            System.out.println("［printArray］array is null.");
            return null;
        }

        int aryLen = ary.length;
        System.out.println("［printArray］array length: " + aryLen);
        for (int i = 0; i < aryLen; ) {
            System.out.print(ary[i] + "\t");
            sb.append(ary[i] + "\t");
            if (++i % numPerLine == 0) System.out.println();
        }
        System.out.println();
        return sb.toString();
    }

    /**
     * 打印数组
     *
     * @param ary
     * @param numPerLine 每行打印多少个数
     */
    public static String printArray(byte[] ary, int numPerLine) {
        StringBuffer sb = new StringBuffer("");
        if (numPerLine <= 0) numPerLine = 1;
        if (null == ary) {
            System.out.println("［printArray］array is null.");
            return null;
        }

        int aryLen = ary.length;
        System.out.println("［printArray］array length: " + aryLen);
        for (int i = 0; i < aryLen; ) {
            System.out.print(ary[i] + "\t");
            sb.append(ary[i] + "\t");
            if (++i % numPerLine == 0) System.out.println();
        }
        System.out.println();
        return sb.toString();
    }

    /**
     * 比较字符串acs
     *
     * @param str1
     * @param str2
     * @return
     */
    public static int compareByAscII(String str1, String str2) {
        int lenght = str1.length() <= str2.length() ? str1.length() : str2.length();
        for (int i = 0; i < lenght; i++) {
            int char1 = str1.charAt(i);
            int char2 = str2.charAt(i);
            if (char1 != char2) {
                return char1 - char2;
            }
            if (i == lenght - 1) {
                return str1.length() - str2.length();
            }
        }
        return 0;
    }

    public static String printStackTrace(Throwable e) {
        StringBuffer sb = new StringBuffer("\n").append(e.getMessage());
        StackTraceElement[] eles = e.getStackTrace();
        for (StackTraceElement ele : eles) {
            sb.append("\n").append(ele.toString());
        }
        return sb.toString();
    }


    public static String map2URLStr(Map<String, Object> map) {
        // Key按照首字母升序排列
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);

        StringBuilder stringBuilder = new StringBuilder();
        for (String mapKey : keys) {
            stringBuilder.append("&").append(mapKey).append("=").append(map.get(mapKey));
        }
        return stringBuilder.deleteCharAt(0).toString();
    }

    /**
     * TODO X-Forwarded-For 头可以伪造追加
     *
     * @param request
     * @return
     */
    private static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        logger.info("ip=" + ip);
        return ip;
    }

    /**
     * getClientIp(获取客户端IP)
     *
     * @Exception 异常对象
     * @since CodingExample　Ver(编码范例查看) 1.1
     * <p>
     * TODO 伪造IP
     * tomcat做法：而是从右向左遍历, 去掉内网ip和代理ip，第一个就是客户端ip了
     * <p>
     * 情况1: 在只有1层nginx代理的情况下，设置nginx配置“proxy_set_header X-Forwarded-For $remote_addr;”。（此时$remote_addr获取的是用于的真是ip）
     * <p>
     * 　　		情况2：在有多层反向代理的情况下，1）设置“最外层”nginx配置和情况1一样“proxy_set_header X-Forwarded-For $remote_addr;”。2）除了最外层之外的nginx配置“proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;”。
     */
    private static String getClientIp(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        logger.info("ip=" + ip);
        return ip;
    }

    /**
     * 获取经过cdn之后的ip
     *
     * @return
     */
    private static String getCdnIP(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (logger.isInfoEnabled()) {
                logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
            }
            return ip;
        }

        String[] ips = ip.split(",");
        if (ips.length < 2) {
            return ips[0];
        }

        // 倒数第一个ip是高防ip，倒数第二个ip就是真实ip了

        return ips[ips.length - 1].trim(); // CC高防和李哥高防去倒数第二个(【用户-高防】 -->> 源站)
//        return ips[ips.length - 3].trim(); // 阿里云高防取倒数第三个 (【用户-->>高防-->>WAF】-->> 源站)
    }

    /**
     * 没有cdn, 只有一层nginx代理的情况直接取
     */
    private static String getXRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtil.isNullOrEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRealIpAddr(HttpServletRequest request) {
        return getCdnIP(request);
//        return getXRealIp(request);
    }

    public static String getRealIpAddr() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getCdnIP(request);
//        return getXRealIp(request);
    }

    /**
     * 拼接MAP
     *
     * @param json
     * @param join
     * @param eq
     * @return
     */
    public static String convertUrl(JSONObject json, String join, String eq) {
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);

        StringBuffer url = new StringBuffer();
        for (String result : keys) {
            if (!json.containsKey(result)) {
                continue;
            }
            url = StringUtils.isNotBlank(eq) ? url.append(result).append(eq).append(json.getString(result)).append(join) : url.append(json.getString(result)).append(join);
        }
        String result = StringUtils.isNotBlank(join) ? url.substring(0, url.toString().length() - 1) : url.toString();
        logger.info("signStr:{}", result);
        return result;
    }

    /**
     * 拼接MAP
     *
     * @param json
     * @return
     */
    public static String convertUrl(JSONObject json) {
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);
        StringBuffer url = new StringBuffer();
        keys.forEach(k -> url.append("&").append(k).append("=").append(json.getString(k)));
        String result = url.deleteCharAt(0).toString();
        logger.info("convertUrl:{}", result);
        return result;
    }

    /**
     * 生成数据签名
     *
     * @param json
     * @param merchantKey
     * @return
     */
    public static String generateSign(JSONObject json, String merchantKey, Boolean filter) {
        json.remove("sign");
        json.remove("channelCode");

        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);

        StringBuilder stringBuilder = new StringBuilder();
        for (String mapKey : keys) {
            if (filter.equals(true) && "".equals(json.get(mapKey))) continue;
            stringBuilder.append("&").append(mapKey).append("=").append(json.get(mapKey));
        }

        stringBuilder.deleteCharAt(0).append("&key=").append(merchantKey);
        String sign = MD5Util.encrypt(stringBuilder.toString()).toUpperCase();

        logger.info("加密原文：{}, 密文：{}", stringBuilder, sign);

        return sign;
    }

    /**
     * 生成数据签名 勿改
     *
     * @param json
     * @param merchantKey
     * @return
     */
    public static String generateSign(JSONObject json, String merchantKey) {
        json.remove("sign");
//        json.remove("channelCode");
        // Key按照首字母升序排列
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);

        StringBuilder stringBuilder = new StringBuilder();
        for (String mapKey : keys) {
            if (StringUtils.isBlank(json.getString(mapKey))) {
                continue;
            }
            stringBuilder.append("&").append(mapKey).append("=").append(json.get(mapKey));
        }
        stringBuilder.deleteCharAt(0).append("&key=").append(merchantKey);
        logger.info("signStr:{}", stringBuilder.toString());
        return MD5Util.encrypt(stringBuilder.toString()).toLowerCase();
    }

    /**
     * 生成数据签名
     *
     * @param json
     * @param keyStr
     * @param merchantKey
     * @return
     */
    public static String generateSign(JSONObject json, String keyStr, String merchantKey) {
        json.remove("sign");
        json.remove("channelCode");
        // Key按照首字母升序排列
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);

        StringBuilder sbd = new StringBuilder();
        for (String mapKey : keys) {
            if (StringUtils.isBlank(json.getString(mapKey))) {
                continue;
            }
            sbd.append("&").append(mapKey).append("=").append(json.get(mapKey));
        }
        sbd = StringUtils.isNotBlank(keyStr) ? sbd.deleteCharAt(0).append("&" + keyStr + "=").append(merchantKey) : sbd.deleteCharAt(0).append(merchantKey);
        logger.info("signStr:{}", sbd.toString());
        return MD5Util.encrypt(sbd.toString());
    }

    /**
     * 生成数据签名
     *
     * @param json
     * @param merchantKey
     * @return
     */
    public static String generateNoKeySign(JSONObject json, String merchantKey) {
        json.remove("sign");
        json.remove("channelCode");
        // Key按照首字母升序排列
        List<String> keys = new ArrayList<>(json.keySet());
        Collections.sort(keys);

        StringBuilder stringBuilder = new StringBuilder();
        for (String mapKey : keys) {
            stringBuilder.append("&").append(mapKey).append("=").append(json.get(mapKey));
        }
        stringBuilder.deleteCharAt(0).append(merchantKey);
        logger.info("signStr:{}", stringBuilder.toString());
        return MD5Util.encrypt(stringBuilder.toString()).toLowerCase();
    }

    /**
     * json排序
     *
     * @param jsonObject
     * @return
     */
    public static String jsonSort(JSONObject jsonObject) {
        List<String> keys = new ArrayList<>(jsonObject.keySet());
        Collections.sort(keys);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (String mapKey : keys) {
            stringBuilder.append(",").append("\"").append(mapKey).append("\"").append(":").append("\"").append(jsonObject.get(mapKey)).append("\"");
        }
        stringBuilder.deleteCharAt(1).append("}");
        return stringBuilder.toString();
    }

    /**
     * json顺序签名
     *
     * @param jsonObject
     * @return
     */
    public static String jsonToMd5(JSONObject jsonObject, String keyStr, String key) {
        StringBuilder sbd = new StringBuilder();
        jsonObject.forEach((k, v) -> sbd.append("&").append(k).append("=").append(v));
        String signStr;
        if (StringUtils.isNotBlank(keyStr)) {
            signStr = sbd.deleteCharAt(0).append("&").append(keyStr).append("=").append(key).toString();
        } else {
            signStr = sbd.deleteCharAt(0).append(key).toString();
        }
        logger.info("signStr:{}", signStr);
        return MD5Util.encrypt(signStr);
    }

    /**
     * json顺序签名
     *
     * @param jsonObject
     * @param encryptParams
     * @return
     */
    public static String arrayToMd5(JSONObject jsonObject, String[] encryptParams, String keyStr, String key) {
        StringBuilder sbd = new StringBuilder();
        for (String param : encryptParams) {
            sbd.append("&").append(param).append("=").append(jsonObject.getString(param));
        }
        sbd = StringUtils.isNotBlank(keyStr) ? sbd.deleteCharAt(0).append("&" + keyStr + "=").append(key) : sbd.deleteCharAt(0).append(key);
        logger.info("signStr:{}", sbd.toString());
        return MD5Util.encrypt(sbd.toString());
    }


    /**
     * json顺序签名
     *
     * @param jsonObject
     * @param encryptParams
     * @return
     */
    public static String valToMd5(JSONObject jsonObject, String[] encryptParams, String json, String key) {
        StringBuilder sbd = new StringBuilder();
        for (String param : encryptParams) {
            String value = jsonObject.getString(param);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            sbd = StringUtils.isNotBlank(json) ? sbd.append(value).append(json) : sbd.append(value);
        }
        String signStr = sbd.append(key).toString();
        logger.info("signStr:{}", signStr);
        return MD5Util.encrypt(signStr);
    }

    /**
     * 元转分，确保price保留两位有效数字
     *
     * @return
     */
    public static int changeY2F(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        price = Double.valueOf(df.format(price));
        int money = (int) (price * 100);
        return money;
    }

    /**
     * 分转元，转换为bigDecimal在toString
     *
     * @return
     */
    public static String changeF2Y(int price) {
        return BigDecimal.valueOf(Long.valueOf(price)).divide(new BigDecimal(100)).toString();
    }

    /**
     * 获取汇率Redis Key
     */
    public static String getExchangeKey(String addrtype, String currency, int ratetype) {
        return addrtype + "_" + currency + "_" + ratetype;
    }

    public static boolean isETHValidAddress(String input) {
        if (StringUtil.isNullOrEmpty(input) || !input.startsWith("0x"))
            return false;
        return isValidAddress(input);
    }

    private static boolean isValidAddress(String input) {
        String cleanInput = input.substring(2);

        try {
            new BigInteger(cleanInput, 16);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == 40;
    }

    /**
     * 根据URL获取domain
     *
     * @param url
     * @return
     */
    public static String getDomainForUrl(String url) {

        String domainUrl = null;
        if (url == null) {
            return null;
        } else {
            Pattern p = Pattern.compile("(?<=(http|https)://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(url);
            while (matcher.find()) {
                domainUrl = matcher.group();
            }
            return domainUrl;
        }
    }

}
