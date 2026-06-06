package com.gp.consumer.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author axing
 * @version 1.0
 * @date 2022/11/12 16:24
 */
@RefreshScope
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NacosParam {

    @Value("${fileAdmin}")
    private String fileAdmin;
    @Value("${isWH:false}")
    private Boolean isWH;




}
