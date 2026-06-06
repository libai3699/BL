package com.common.core.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@ConfigurationProperties(prefix = "aws")
public class AmazonProp {
    /**
     * 亚马逊id
     */
    private String accessKeyId;
    /**
     * 亚马逊秘钥
     */
    private String accessKeySecret;
    /**
     * 存储桶
     */
    private String bucketName;
    /**
     * 文件夹
     */
    private String dirname;


}
