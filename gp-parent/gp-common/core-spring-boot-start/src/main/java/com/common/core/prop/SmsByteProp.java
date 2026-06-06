package com.common.core.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TxSmsConfig config
 */
@Data
@ConfigurationProperties(prefix = "sms.bt")
public class SmsByteProp {
    /**
     * 访问
     */
    public String accessKey;
    /**
     * 秘钥key
     */
    public String secretKey;
    /**
     * 用户id
     */
    public String from;
    /**
     * 短信群组id
     */
    public String smsAccount;
    /**
     * 模板编码ID
     */
    public String templateId;

    public String time;


}
