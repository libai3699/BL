package com.common.core.util;//package com.common.core.util;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson2.JSONObject;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import com.aliyuncs.http.HttpClientConfig;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.common.core.prop.SmsAlProp;
//import com.common.core.sms.SmsConstant;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class SmsAlUtil {
//    protected static final Logger log = LoggerFactory.getLogger(SmsAlUtil.class);
//
//
//    //产品名称:云通信短信API产品,开发者无需替换
//    private static final String product = "Dysmsapi";                            // 无需修改
//    //产品域名,开发者无需替换
//    private static final String domain = "dysmsapi.aliyuncs.com";                // 无需修改
//
//
//
//    public static boolean send(String phone, String code, SmsAlProp smsAlProp) {
//        SendSmsResponse sendSmsResponse = null;
//        try {
//            // Client 代理配置
//            HttpClientConfig clientConfig = HttpClientConfig.getDefault();
//            // 设置HTTP代理
//            if (StrUtil.isNotBlank(smsAlProp.getHttpProxy())) {
//                clientConfig.setHttpProxy(smsAlProp.getHttpProxy());
////                clientConfig.setHttpProxy("http://16.162.45.9:8112");
//            }
//            // 设置HTTPS代理
//            if (StrUtil.isNotBlank(smsAlProp.getHttpsProxy())) {
//                clientConfig.setHttpsProxy(smsAlProp.getHttpsProxy());
////              clientConfig.setHttpsProxy("http://16.162.45.9:8112");
//            }
//
//            // 设置忽略代理地址列表
//
//            //初始化acsClient,暂不支持region化
//            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsAlProp.accessKeyId, smsAlProp.secretKey);
//            profile.setHttpClientConfig(clientConfig);
//            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
//            IAcsClient acsClient = new DefaultAcsClient(profile);
//
//            //组装请求对象-具体描述见控制台-文档部分内容
//            SendSmsRequest request = new SendSmsRequest();
//            //必填:待发送手机号
//            request.setPhoneNumbers(phone);
//            //必填:短信签名-可在短信控制台中找到
//            request.setSignName(smsAlProp.sign);
//            //必填:短信模板-可在短信控制台中找到
//            request.setTemplateCode(smsAlProp.templateCode);
//            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${title}"时,此处的值为 {\"name\":\"Tom\", \"code\":\"123\"}
//            JSONObject templateJson = new JSONObject();
//            templateJson.put("code",code);
//            request.setTemplateParam(templateJson.toJSONString());
//            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
//            //request.setSmsUpExtendCode("90997");
//            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
////            request.setOutId("yourOutId");
//            sendSmsResponse = acsClient.getAcsResponse(request);                             // 发送短信，且返回结果
//            if (!SmsConstant.ALY_SMS_CODE.equalsIgnoreCase(sendSmsResponse.getCode())) {
//                log.info("阿里云短信返回code:{}",sendSmsResponse.getCode());
//                log.info("阿里云短信返回msg:{}",sendSmsResponse.getMessage());
//                return false;
//            }
//        } catch (Exception ex) {
//            log.error("发送失败",ex);
//            return false;
//        }
//
//
//        return true;
//    }
//
//
//}
