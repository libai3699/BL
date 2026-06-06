package com.common.core.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TxSmsConfig config
 */
@Data
@ConfigurationProperties(prefix = "sms.tx")
public class SmsTxProp {
    /**
     * 产品ID
     */
    public String appId;
    /**
     * 秘钥ID
     */
    public String secretId;
    /**
     * 秘钥
     */
    public String secretKey;
    /**
     * 国内签名
     */
    public String sign;
    /**
     * 模板编码ID
     */
    public String templateId;


}
