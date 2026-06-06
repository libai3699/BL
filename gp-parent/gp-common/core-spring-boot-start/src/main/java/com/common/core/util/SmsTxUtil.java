package com.common.core.util;//package com.common.core.util;
//
//import com.common.core.prop.SmsTxProp;
//import com.common.core.sms.SmsConstant;
//import com.tencentcloudapi.common.Credential;
//import com.tencentcloudapi.common.exception.TencentCloudSDKException;
//import com.tencentcloudapi.sms.v20190711.SmsClient;
//import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
//import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
//import com.tencentcloudapi.sms.v20190711.models.SendStatus;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class SmsTxUtil {
//    protected static final Logger log = LoggerFactory.getLogger(SmsTxUtil.class);
//    public static boolean send(String phone, String code, String active, SmsTxProp smsTxProp) {
//
////        if ("dev".equals(active)){
////            return true;
////        }
//        Credential cred = new Credential(smsTxProp.secretId, smsTxProp.secretKey);
//        log.info("secretId:{}", smsTxProp.secretId);
//
//        // 实例化一个 http 选项，可选，无特殊需求时可以跳过
//        // 具体http配置参考官方文档或demo
////        HttpProfile httpProfile = new HttpProfile();
//
//        SmsClient client = new SmsClient(cred, "");
//        SendSmsRequest req = new SendSmsRequest();
//        // APP ID，需要在短信服务中创建该应用获取APP ID
//        req.setSmsSdkAppid(smsTxProp.appId);
//        // 签名，自己随意签写字符串
//        req.setSign(smsTxProp.sign);
//        // 模板ID
//        req.setTemplateID(smsTxProp.templateId);
//
//        String[] phoneArr = {"+86" + phone};
//
//        // 写入号码列表
//        req.setPhoneNumberSet(phoneArr);
//
//
//        String[] codeArr = {code};
//        // 写入参数，若无则不写
//        req.setTemplateParamSet(codeArr); // 写入参数，若无则不写
//
//        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
//         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
//        SendSmsResponse res = null;
//        try {
//            // 调用API，同步返回结果
//            res = client.SendSms(req);
//            for (SendStatus sendStatus : res.getSendStatusSet()) {
//                if (!SmsConstant.TXY_SMS_CODE.equals(sendStatus.getCode())) {
//                    log.info("腾讯云短信返回code:{}",sendStatus.getCode());
//                    log.info("腾讯云短信返回msg:{}",sendStatus.getMessage());
//                    return false;
//                }
//            }
//        } catch (TencentCloudSDKException e) {
//            log.error("发送失败",e);
//            return false;
//        }
//
//        return true;
//    }
//
//
//}
