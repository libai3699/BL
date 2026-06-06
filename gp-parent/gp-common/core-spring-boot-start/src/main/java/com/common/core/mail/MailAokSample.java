package com.common.core.mail;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.common.core.nacos.BNacosParam;
import com.common.core.prop.ProxyProp;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class MailAokSample {
    private static final String API_URL = "https://www.aoksend.com/index/api/send_email";
    @Resource
    public  ProxyProp proxyProp;

    @Resource
    private BNacosParam bNacosParam;
    public static void main(String[] args) {
        // 创建一个参数列表
        Map<String, Object> params = new HashMap<>();
        params.put("app_key", "f05473e49d3c69752ce1c31beb15eb6f");
        params.put("template_id", "E_103351720936");
        params.put("to", "zbj884@gmail.com");
        params.put("alias", "MPAY");
        params.put("data", "{\"code\":\"4567\"}");

        try {
            // 发送POST请求并获取响应
//            String responseBody = HttpRequest.post(API_URL).setProxy((new Proxy(Proxy.Type.HTTP,
//                    new InetSocketAddress(proxyProp.getHost(), proxyProp.getPort()))))
//                    .form(params).timeout(10000)
//                    .execute().body();
//            System.out.println("Response: " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendEmail(SimpleMailMessage simpleMailMessage,MailConfigStart mailConfigStart) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_key", mailConfigStart.getEmailAppKey());
        params.put("template_id", mailConfigStart.getEmailTemplateId());
        params.put("to", Objects.requireNonNull(simpleMailMessage.getTo())[0]);
        params.put("alias", mailConfigStart.getEmailAlias());
        params.put("data", "{\"code\":\""+ simpleMailMessage.getText() +"\",\"product\":\""+ mailConfigStart.getEmailAlias() +"\"}");
        try {
            // 发送POST请求并获取响应
            String responseBody = HttpRequest.post(API_URL).setProxy((new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyProp.getHost(), proxyProp.getPort()))))
                    .form(params).timeout(10000)
                    .execute().body();
            System.out.println("Response: " + responseBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

