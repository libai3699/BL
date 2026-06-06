package com.common.core.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FastJson2Config {

    @PostConstruct
    public void init() {
        JSON.config(JSONWriter.Feature.WriteBigDecimalAsPlain);
    }
}
