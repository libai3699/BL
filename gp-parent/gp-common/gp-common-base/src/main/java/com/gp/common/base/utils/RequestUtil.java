package com.gp.common.base.utils;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * @author axing
 * @version 1.0
 * @date 2021/9/29 23:59
 */
@Slf4j
@Validated
@NoArgsConstructor
@Data
public class RequestUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 检查当前请求是不是request请求
     *
     * @return {@link Boolean}
     */
    public static boolean checkRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        } else {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
    }

    /**
     * 获取请求头
     *
     * @param header 头
     * @return {@link String}
     */
    public static String getHeader(@NotBlank(message = "请求头不能为空") String header) {
        if (checkRequest()) {
            return ServletUtil.getHeaderIgnoreCase(getRequest(), header);
        } else {
            return "";
        }
    }

    /**
     * 获取请求参数字符串
     *
     * @param param 请求参数
     * @return {@link String}
     */
    public static String getParamStr(@NotBlank(message = "参数不能为空") String param) {
        if (checkRequest()) {
            try {
                return getRequest().getParameter(param);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取请求数字
     *
     * @param param 请求参数
     * @return {@link String}
     */
    public static Integer getParamInt(@NotBlank(message = "参数不能为空") String param) {
        if (checkRequest()) {
            try {
                return Integer.parseInt(getRequest().getParameter(param));
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取请求头
     *
     * @param request 请求对象
     * @return {@link Map<String, String>}
     */
    public static Map<String, String> getHeadersMap(HttpServletRequest request) {
        return ServletUtil.getHeaderMap(request);
    }

}
