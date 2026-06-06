package com.common.core.handler;

import com.gp.common.base.utils.MessagesUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/18/018 12:18
 */
public class MvcInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 存入 request attribute（供后续使用）
        request.setAttribute("URL", request.getRequestURL().toString());
        MessagesUtils.setLang(request.getHeader("Accept-Language"));
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
