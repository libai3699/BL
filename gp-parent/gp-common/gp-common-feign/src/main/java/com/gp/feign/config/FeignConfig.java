package com.gp.feign.config;

import cn.hutool.core.util.StrUtil;
import com.common.datasource.param.CecuProp;
import com.common.datasource.util.CecuThreadLocal;
import com.common.datasource.util.CecuUtil;
import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/30/030 20:47
 */

/**
 * feign 开启日志
 **/
@Configuration
@EnableConfigurationProperties(CecuProp.class)
@Slf4j
public class FeignConfig {

    public static final String key = "feignKey";
    public static final String value = "Vz5cULS7bWgalrj3";

    // 分页参数名
    private static final String PAGE_NUM = "pageNum";
    private static final String PAGE_SIZE = "pageSize";
    private static final String ORDER_BY_COLUMN = "orderByColumn";
    private static final String IS_ASC = "isAsc";

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    @ConditionalOnProperty(prefix = "dynamic", name = "cecu-switch", havingValue = "true")
    public RequestInterceptor requestInterceptor(CecuProp cecuProp) {
        return requestTemplate -> {
            String dbCode = CecuThreadLocal.getProperty(CecuUtil.DB_CODE);
            requestTemplate.header(key, value);
            log.info("我得到的code {}", dbCode);
            if (StrUtil.isNotBlank(dbCode)) {
                requestTemplate.header(cecuProp.getCecuField(), dbCode);
            }
        };
    }

    /**
     * 分页参数拦截器
     *
     * @return
     */
    @Bean
    public RequestInterceptor pageRequestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // 获取分页参数并添加到请求中
                String pageNum = request.getParameter(PAGE_NUM);
                String pageSize = request.getParameter(PAGE_SIZE);
                String orderByColumn = request.getParameter(ORDER_BY_COLUMN);
                String isAsc = request.getParameter(IS_ASC);
                log.info("分页参数 {} {} {} {}", pageNum, pageSize, orderByColumn, isAsc);
                if (StrUtil.isNotBlank(pageNum)) {
                    requestTemplate.query(PAGE_NUM, pageNum);
                }
                if (StrUtil.isNotBlank(pageSize)) {
                    requestTemplate.query(PAGE_SIZE, pageSize);
                }
                if (StrUtil.isNotBlank(orderByColumn)) {
                    requestTemplate.query(ORDER_BY_COLUMN, orderByColumn);
                }
                if (StrUtil.isNotBlank(isAsc)) {
                    requestTemplate.query(IS_ASC, isAsc);
                }
            }
        };
    }
}

