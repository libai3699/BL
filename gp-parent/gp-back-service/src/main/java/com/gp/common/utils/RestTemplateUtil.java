package com.gp.common.utils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

/**
 * @author axing
 * @version 1.0
 * @date 2021/7/25 20:55
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestTemplateUtil <T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 带请求头,参数的get请求
     *
     * @param url     url
     * @param headers 头
     * @param params  参数个数
     * @return {@link JSONObject}
     */
    public JSONObject getByHeader (String url, LinkedMultiValueMap<String, String> headers,Map<String, Object> params){
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
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, JSONObject.class);
        ResponseExtractor<ResponseEntity<JSONObject>> responseExtractor = restTemplate.responseEntityExtractor(JSONObject.class);
        // 执行execute()，发送请求
        ResponseEntity<JSONObject> response = restTemplate.execute(url, HttpMethod.GET, requestCallback, responseExtractor);
        return response.getBody();
    }
}
