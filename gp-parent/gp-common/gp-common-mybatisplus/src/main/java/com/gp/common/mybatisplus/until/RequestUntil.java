package com.gp.common.mybatisplus.until;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.common.core.filter.JsonXssRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestUntil {
    public static Map<String, String> doParam(HttpServletRequest request) {
        Map<String, String> map = ServletUtil.getParamMap(request);
        if (MapUtil.isEmpty(map)) {
            //从body中获取
            if (request instanceof JsonXssRequestWrapper) {
                JsonXssRequestWrapper xssRequestWrapper = (JsonXssRequestWrapper) request;
                map = com.alibaba.fastjson.JSON.parseObject(xssRequestWrapper.getBody(), Map.class);
                // 继续处理 map ...
            } else {
                // 处理普通的 HttpServletRequest
                try {
                    BufferedReader reader = request.getReader();
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String requestBody = sb.toString();
                    map = com.alibaba.fastjson.JSON.parseObject(requestBody, Map.class);
                    // 继续处理 map ...
                } catch (IOException e) {
                    // 处理异常
                    e.printStackTrace();
                }
            }
        } else {
            Map<String, String> finalMap = map;
            map.forEach((key, value) -> {
                try {
                    // 处理中文参数乱码
                    String decodedValue = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    finalMap.put(key, decodedValue);
                } catch (Exception e) {
                    finalMap.put(key, value);
                }
            });
        }
        return map;
    }

    /**
     * 解析请求参数，兼容 JSON 和 form-urlencoded 格式
     * 适用于支持多种 Content-Type 的回调接口（如 DXTX 支付）
     *
     * @param request HttpServletRequest 对象
     * @return 参数 Map
     */
    public static Map<String, String> doParamCompatible(HttpServletRequest request) {
        // 1. 先尝试从 URL 参数获取（GET 方式或 POST 表单）
        Map<String, String> paramMap = ServletUtil.getParamMap(request);

        if (MapUtil.isNotEmpty(paramMap)) {
            // URL 参数存在，处理中文乱码
            Map<String, String> finalMap = paramMap;
            paramMap.forEach((key, value) -> {
                try {
                    String decodedValue = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    finalMap.put(key, decodedValue);
                } catch (Exception e) {
                    finalMap.put(key, value);
                }
            });
            return paramMap;
        }

        // 2. URL 参数为空，从 body 中读取
        String requestBody = readRequestBody(request);

        if (StrUtil.isBlank(requestBody)) {
            return MapUtil.newHashMap();
        }

        // 3. 判断 body 格式并解析
        String trimmedBody = requestBody.trim();
        if (trimmedBody.startsWith("{") && trimmedBody.endsWith("}")) {
            // JSON 格式
            try {
                return com.alibaba.fastjson.JSON.parseObject(requestBody,
                    new com.alibaba.fastjson.TypeReference<Map<String, String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
                return MapUtil.newHashMap();
            }
        } else {
            // form-urlencoded 格式: key1=value1&key2=value2
            return parseFormUrlEncoded(requestBody);
        }
    }

    /**
     * 读取请求体内容
     */
    public static String readRequestBody(HttpServletRequest request) {
        if (request instanceof JsonXssRequestWrapper) {
            JsonXssRequestWrapper xssRequestWrapper = (JsonXssRequestWrapper) request;
            return xssRequestWrapper.getBody();
        } else {
            try {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 解析 form-urlencoded 格式数据
     * 格式：key1=value1&key2=value2&key3=
     */
    private static Map<String, String> parseFormUrlEncoded(String body) {
        Map<String, String> result = MapUtil.newHashMap();

        if (StrUtil.isBlank(body)) {
            return result;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            if (pair.contains("=")) {
                int idx = pair.indexOf("=");
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);

                // URL 解码（处理特殊字符和中文）
                try {
                    value = java.net.URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    // 解码失败保持原值
                }

                result.put(key, value);
            }
        }

        return result;
    }


}
