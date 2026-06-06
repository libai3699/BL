package com.gp.common.mybatisplus.until;

import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.common.core.prop.ProxyProp;
import com.gp.common.base.exception.GameLoginException;
import com.gp.common.base.result.ApiResult;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import com.gp.common.mybatisplus.entity.GamePlate;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.param.FaceBookEvent;
import com.gp.common.mybatisplus.param.TiktokEvent;
import com.gp.common.mybatisplus.service.ConfigRiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用http发送方法
 *
 * @author ruoyi
 */
@Service
public class HttpUtils {
    @Resource
    private ConfigRiskService configRiskService;
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public void sendTikTokEvent(String userMark, BigDecimal amount, TUser tUser) {
        Long channelId = tUser.getChannelId();
        try {

            Long userChannelId = tUser.getChannelId();
            if (userChannelId == null) {
                return;
            }
            ConfigRisk configRiskToken = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.tiktokToken+channelId);
            if (configRiskToken == null) {
                return;
            }
            ConfigRisk configRiskPiexID = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.tiktokPiexID+channelId);
            if (configRiskPiexID == null) {
                return;
            }
            Map<String, List<String>> headers = MapUtil.newHashMap();
            headers.put("Access-Token", CollUtil.newArrayList(configRiskToken.getConfigValue()));

            TiktokEvent tiktokEvent = new TiktokEvent();
            tiktokEvent.setEvent_source("web");
            tiktokEvent.setEvent_source_id(configRiskPiexID.getConfigValue());

            TiktokEvent.EventData eventData = new TiktokEvent.EventData();
            eventData.setEvent("CompletePayment");
            // 获取当前的 UTC 时间
            Instant now = Instant.now(); // 获取当前时间戳（UTC）

            // 转换为 Zulu 时间，也就是 UTC+0
            ZonedDateTime utcTime = now.atZone(ZoneId.of("UTC"));
            long seconds = utcTime.toEpochSecond(); // 获取自1970-01-01T00:00:00Z以来的秒数
            eventData.setEvent_time(seconds);
            TiktokEvent.User user = new TiktokEvent.User();
            user.setExternal_id(userMark);
            eventData.setUser(user);
            TiktokEvent.Properties properties = new TiktokEvent.Properties();
            properties.setCurrency("USD");
            properties.setValue(BigDecimalUtils.trim(amount));
            eventData.setProperties(properties);


            List<TiktokEvent.EventData> arr = Lists.newArrayList(eventData);
            tiktokEvent.setData(arr);

            log.info("请求参数: {}", JSON.toJSONString(tiktokEvent));

            String url = "https://business-api.tiktok.com/open_api/v1.3/event/track/";
            HttpUtils.doPostJsonRequest(url,JSON.toJSONString(tiktokEvent), headers);
        } catch (Exception e) {
            log.error("请求失败: {}", e.getMessage());
        }


    }
    public void sendTFacebookEEvent(String userMark, BigDecimal amount, TUser tUser,String webUrl) {

        Long channelId = tUser.getChannelId();
        try {

            Long userChannelId = tUser.getChannelId();
            if (userChannelId == null) {
                return;
            }
            ConfigRisk configRiskToken = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.facebookToken+channelId);
            if (configRiskToken == null) {
                return;
            }
            ConfigRisk configRiskPiexID = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.facebookPiexID+channelId);
            if (configRiskPiexID == null) {
                return;
            }
            Instant now = Instant.now(); // 获取当前时间戳（UTC）

            // 转换为 Zulu 时间，也就是 UTC+0
            ZonedDateTime utcTime = now.atZone(ZoneId.of("UTC"));
            long seconds = utcTime.toEpochSecond(); // 获取自1970-01-01T00:00:00Z以来的秒数
            String piexIDConfigValue = configRiskPiexID.getConfigValue();
            String tokenConfigValue = configRiskToken.getConfigValue();
            String url = "https://graph.facebook.com/v19.0/"+piexIDConfigValue+"/events?access_token="+tokenConfigValue;
//            String url = "https://graph.facebook.com/v21.0/"+piexIDConfigValue+"/events?access_token="+tokenConfigValue;
            Map<String, List<String>> headers = MapUtil.newHashMap();
            HashMap<String, List<FaceBookEvent>> data = new HashMap<>();
            FaceBookEvent faceBookEvent = new FaceBookEvent();

            String eventName ="AddPaymentInfo";
            String actionSource ="website";
                faceBookEvent.setEvent_name(eventName);
            faceBookEvent.setEvent_time(seconds);
            faceBookEvent.setAction_source(actionSource);
            faceBookEvent.setEvent_source_url(webUrl);
            FaceBookEvent.OriginalEventData originalEventData = new FaceBookEvent.OriginalEventData();
            originalEventData.setEvent_name(eventName);
            originalEventData.setEvent_time(seconds);
            faceBookEvent.setOriginal_event_data(originalEventData);
            FaceBookEvent.UserData userData = new FaceBookEvent.UserData();
            userData.setExternal_id(CollUtil.newArrayList(userMark));
            faceBookEvent.setUser_data(userData);
            FaceBookEvent.CustomData customData = new FaceBookEvent.CustomData();
            customData.setCurrency("USD");
            customData.setValue(amount);
            faceBookEvent.setCustom_data(customData);

            List<FaceBookEvent> faceBookEvents = CollUtil.newArrayList(faceBookEvent);

            data.put("data",faceBookEvents);

            HttpUtils.doPostJsonRequest(url,JSON.toJSONString(data), headers);
        } catch (Exception e) {
            log.error("请求失败: {}", e.getMessage());
        }


    }
    public static String doPostJsonRequest(String url,String param, Map<String, List<String>> headers) {
        String result = null;
        try {
            ProxyProp proxyProp = SpringUtil.getBean(ProxyProp.class);
            if (StrUtil.isBlank(proxyProp.getHost())) {
                result = HttpRequest.post(url).header(headers).body(param).timeout(10000).execute().body();
            } else {
                result = HttpRequest.post(url).header(headers).setProxy((new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(proxyProp.getHost(), proxyProp.getPort())))).body(param).timeout(10000).execute().body();
            }

            log.info("返回结果url:{},: {}",url,result);
            return result;
        } catch (Exception e) {
            log.error("请求失败: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public String sendTiktokEvent(String url,Long channelId, String jsonStr) {
        ConfigRisk configRiskToken = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.tiktokToken+channelId);
        HashMap<String, String> map = MapUtil.newHashMap();
        map.put("code", "0");
        map.put("msg", "success");
        if (configRiskToken == null) {
            return JSON.toJSONString(map);
        }
        ConfigRisk configRiskPiexID = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.tiktokPiexID+channelId);
        if (configRiskPiexID == null) {
            return JSON.toJSONString(map);
        }
        Map<String, List<String>> headers = MapUtil.newHashMap();
        headers.put("Access-Token", CollUtil.newArrayList(configRiskToken.getConfigValue()));
        return doPostJsonRequest(url, jsonStr, headers);
    }

    public String sendFacebookEvent(String url,Long channelId, String jsonStr) {
        ConfigRisk configRiskToken = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.facebookToken+channelId);
        HashMap<String, String> map = MapUtil.newHashMap();
        map.put("code", "0");
        map.put("msg", "success");
        if (configRiskToken == null) {
            return JSON.toJSONString(map);
        }
        ConfigRisk configRiskPiexID = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.facebookPiexID+channelId);
        if (configRiskPiexID == null) {
            return JSON.toJSONString(map);
        }
        Map<String, List<String>> headers = MapUtil.newHashMap();
        return doPostJsonRequest(url, jsonStr, headers);
    }
}
