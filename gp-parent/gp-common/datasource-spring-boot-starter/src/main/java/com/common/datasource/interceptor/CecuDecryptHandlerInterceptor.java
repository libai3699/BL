package com.common.datasource.interceptor;

import cn.hutool.core.map.MapUtil;
import com.common.datasource.annotation.CecuExclude;
import com.common.datasource.param.CecuProp;
import com.common.datasource.util.CecuThreadLocal;
import com.common.datasource.util.CecuUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * com.ny.common.mysql.interceptor
 * <p>
 * 分库TOKEN拦截解析
 * </p>
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 11:19
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CecuDecryptHandlerInterceptor implements HandlerInterceptor {

    private CecuUtil cecuUtil;
    private CecuProp cecuProp;

    //健康检测接口
    public static String healthUri = "/index/aws/elb/health";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        LogUtil.info("------------------切库WEB拦截----------------------");
        //健康检查的直接跳过
        if (request.getRequestURI().equals(healthUri)) {
            return true;
        }
        CecuThreadLocal.clear();
        if (isCecuExclude(handler)) {
            return true;
        }

        String cecuId = request.getHeader(cecuProp.getCecuField());

        if (StringUtils.isNotBlank(cecuId)) {
            String dbName = cecuUtil.getDbNameFromList(cecuId);
            if (StringUtils.isNotEmpty(dbName)) {
                log.info("切库WEB拦截 - 库名: {} - 库编码: {}", dbName, cecuId);
                Map<String, String> newEntries = MapUtil.newHashMap(2);
                newEntries.put(CecuUtil.DB_NAME_KEY, dbName);
                newEntries.put(CecuUtil.DB_CODE, cecuId);
                CecuThreadLocal.addProperty(newEntries);
            }
        } else {
            log.info("切库WEB拦截 - 无法获取库名:{}", request.getRequestURI());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CecuThreadLocal.clear();
    }

    /**
     * 获取切库排除注解
     */
    public boolean isCecuExclude(Object handler) {
        try {
            if (handler instanceof ResourceHttpRequestHandler) {
                return true;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            boolean beanAnnotation = handlerMethod.getBeanType().isAnnotationPresent(CecuExclude.class);
            boolean methodAnnotation = handlerMethod.getMethod().isAnnotationPresent(CecuExclude.class);
            return beanAnnotation || methodAnnotation;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
