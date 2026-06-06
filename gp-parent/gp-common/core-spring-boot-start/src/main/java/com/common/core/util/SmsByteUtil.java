package com.common.core.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.byteplus.model.request.SmsSendRequest;
import com.byteplus.model.response.SmsSendResponse;
import com.byteplus.service.sms.SmsService;
import com.common.core.prop.SmsByteProp;
import lombok.extern.slf4j.Slf4j;


/**
 * 字节跳动短信
 *
 * @author Administrator
 */
@Slf4j
public class SmsByteUtil {

    public static boolean send(String phone, String code,SmsService smsService, SmsByteProp smsByteProp) {
        try {
            SmsSendRequest req = new SmsSendRequest();
            req.setFrom(smsByteProp.getFrom());
            req.setSmsAccount(smsByteProp.getSmsAccount());
            req.setPhoneNumbers(phone);
            req.setTemplateId(smsByteProp.getTemplateId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            jsonObject.put("time", smsByteProp.getTime());
            req.setTemplateParam(jsonObject.toJSONString());
            req.setTag("验证码发送");
            SmsSendResponse response = smsService.send(req);
            log.info("短信发送结果: {}", JSON.toJSONString(response));
            return true;
        } catch (Exception ex) {
            log.error("发送失败", ex);
            return false;
        }
    }

}
