package com.common.core.util;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author axing
 * @version 1.0
 * @date 2021/7/25 20:55
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestTemplateUtil<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    private RestTemplate restTemplate;

    /**
     * 带请求头,参数的get请求
     *
     * @param url     url
     * @param headers 头
     * @param params  参数个数
     * @return {@link JSONObject}
     */
    public JSONObject getByHeader (String url, LinkedMultiValueMap<String, String> headers, Map<String, Object> params){
        //参数处理
        if (params != null) {
            Set<Map.Entry<String, Object>> entries = params.entrySet();
            int num= 0;
            for (Map.Entry<String, Object> entry : entries) {
                num ++;
                String key = entry.getKey();
                Object value = entry.getValue();
                if (num == 1) {
                    url = url + "?" + key + "=" +value;
                }else {
                    url = url + "&" + key + "=" +value;
                }
            }
        }
        //请求头设置
        HttpEntity request = new HttpEntity(headers);
        // 构造execute()执行所需要的参数。
        RestTemplate restTemplate = this.getRestTemplate();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, JSONObject.class);
        ResponseExtractor<ResponseEntity<JSONObject>> responseExtractor = restTemplate.responseEntityExtractor(JSONObject.class);
        // 执行execute()，发送请求
        log.info("请求URL: {}",url);
        log.info("请求参数: {}",params);
        ResponseEntity<JSONObject> response = this.restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        JSONObject body = response.getBody();
        log.info("请求结果 {}",body);
        return body;
    }

    /**
     * 带请求头,参数的post请求
     *
     * @param url     url
     * @param headers 头
     * @param params  参数个数
     * @return {@link JSONObject}
     */
    public JSONObject postByHeader (String url, LinkedMultiValueMap<String, String> headers, Object params){
        //请求头设置
        HttpEntity request = new HttpEntity(params,headers);
        // 构造execute()执行所需要的参数。
        RestTemplate restTemplate = this.getRestTemplate();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, JSONObject.class);
        ResponseExtractor<ResponseEntity<JSONObject>> responseExtractor = restTemplate.responseEntityExtractor(JSONObject.class);
        // 执行execute()，发送请求
        // 执行execute()，发送请求
        log.info("请求URL: {}",url);
        log.info("请求参数: {}",params);
        ResponseEntity<JSONObject> response = restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor);
        JSONObject body = response.getBody();
        log.info("请求结果: {}",body);
        return response.getBody();
    }
}
