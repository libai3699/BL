//package com.gp.service.config;
//
//
//
//import com.gp.service.service.BigDecimalSerialize;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//
//@Configuration
//public class ProjectConfig {
//
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> {
//            jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, new BigDecimalSerialize());
//        };
//    }
//
//
//}
//
