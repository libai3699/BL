package com.gp.common.base.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 15:57
 */
@Slf4j
public class PayParamEncryUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 按照键值对形式 &符号拼接
     * ASCLL码 从小到大排序
     * 返回字符串
     *
     * @param map
     * @return string
     */
    public static String getAscllString(Map<String, Object> map) {
        String result;
        try {
            String str = MapUtil.sortJoin(map, "&", "=", true);
            result = MD5Util.getStr(str);
        } catch (Exception e) {
            throw new RuntimeException("拼接字符串出错");
        }
        return result;
    }

    /**
     * 按照键值对形式 &符号拼接
     * ASCLL码 从小到大排序
     * 返回字符串
     *
     * @param jsonObject
     * @return string
     */
    public static String getMaskSign(JSONObject jsonObject, String secret) {
        String result;
        try {
            //去掉签名参数
            jsonObject.remove("sign");
            String str = MapUtil.sortJoin(jsonObject, "&", "=", true, secret);
            result = MD5Util.getStr(str);
        } catch (Exception e) {
            throw new RuntimeException("拼接字符串出错");
        }
        return result;
    }

    /**
     * 按照键值对形式 &符号拼接
     * ASCLL码 从小到大排序
     * 返回字符串
     *
     * @param jsonObject
     * @return string
     */
    public static String getMaskNotifySign(JSONObject jsonObject, String key, String secret) {
        String result;
        try {
            //去掉签名参数
            jsonObject.remove("sign");
            jsonObject.remove("remark");
            jsonObject.put("api_key", key);

            //踢出为空字符的
            JSONObject jsonObjectData = new JSONObject();
            Set<String> strings = jsonObject.keySet();
            for (String jsonKey : strings) {
                String o = jsonObject.getString(jsonKey);
                if (StrUtil.isNotBlank(o)) {
                    jsonObjectData.put(jsonKey, o);
                }
            }
            String str = MapUtil.sortJoin(jsonObjectData, "&", "=", true, secret);
            result = MD5Util.getStr(str);
        } catch (Exception e) {
            throw new RuntimeException("拼接字符串出错");
        }
        return result;
    }


    /**
     * 将Map按照键值对形式并用&符号拼接
     *
     * @param map
     * @return string
     */
    public static String getContent(Map<String, Object> map) {
        return MapUtil.sortJoin(map, "&", "=", true);
    }

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map，为空返回otherParams拼接
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @param otherParams       其它附加参数字符串（例如密钥）
     * @return 连接后的字符串，map和otherParams为空返回""
     * @since 3.1.1
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
        final StringBuilder strBuilder = StrUtil.builder();
        boolean isFirst = true;
        if (isNotEmpty(map)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (false == isIgnoreNull || entry.getKey() != null && entry.getValue() != null && entry.getKey() != "") {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        strBuilder.append(separator);
                    }
                    strBuilder.append(Convert.toStr(entry.getKey())).append(keyValueSeparator).append(Convert.toStr(entry.getValue()));
                }
            }
        }
        // 补充其它字符串到末尾，默认无分隔符
        if (ArrayUtil.isNotEmpty(otherParams)) {
            for (String otherParam : otherParams) {
                strBuilder.append(otherParam);
            }
        }
        return strBuilder.toString();
    }
    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }
    /**
     * UPAY
     * ASCLL码 从小到大排序
     * 返回字符串
     *
     * @param
     * @return string
     */
    public static String getUPAYSign(JSONObject jsonObject, String secret) {
        String result;
        try {
            //去掉签名参数
            jsonObject.remove("sign");
            JSONObject encodedJsonObject = new JSONObject();
            // 遍历 jsonObject 的所有键
            Iterator<String> keys = jsonObject.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                // 对每个值进行 urlencode 编码
                String encodedValue = URLEncoder.encode(value, "UTF-8").toLowerCase();
                encodedJsonObject.put(key, encodedValue);
            }

            String str = MapUtil.sortJoin(encodedJsonObject, "&", "=", true) + "&secret=" + secret;
            log.info("str:{}",str);
//            String encodedValue = URLEncoder.encode(str, "UTF-8").toLowerCase()+"&secret="+secret;
//            log.info("encodedValue:{}",encodedValue);
            result = MD5Util.getStr(str.toLowerCase());
        } catch (Exception e) {
            throw new RuntimeException("拼接字符串出错");
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        JSONObject encodedJsonObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("aparam", "中文");
        Iterator<String> keys = jsonObject.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonObject.getString(key);
            // 对每个值进行 urlencode 编码 //%e4%b8%ad%e6%96%87
            String encodedValue = URLEncoder.encode(value, "UTF-8").toLowerCase();
            encodedJsonObject.put(key, encodedValue);
        }
        System.out.println(encodedJsonObject.toString());
    }
}
