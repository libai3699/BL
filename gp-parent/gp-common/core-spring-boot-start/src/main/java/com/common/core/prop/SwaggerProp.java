package com.common.core.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProp {
    /**
     * 是否开启
     */
    private boolean enabled;



}
