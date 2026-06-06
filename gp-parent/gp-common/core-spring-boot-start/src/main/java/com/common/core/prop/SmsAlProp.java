package com.common.core.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * alySmsConfig config
 */
@Data
@ConfigurationProperties(prefix = "sms.aly")
public class SmsAlProp {

    /**
     * 秘钥id
     */
    public String accessKeyId;

    /**
     * http代理
     */
    public String httpProxy;
    /**
     * https代理
     */
    public String httpsProxy;

    /**
     * 秘钥
     */
    public String secretKey;

    /**
     * 国内签名
     */
    public String sign;

    /**
     * 国内模板编码
     */
    public String templateCode;

    /**
     * 国际签名
     */
    public String interSign;

    /**
     * 国际模板编码
     */
    public String interTemplateCode;


}
