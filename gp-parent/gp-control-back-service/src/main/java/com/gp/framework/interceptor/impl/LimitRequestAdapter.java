package com.gp.framework.interceptor.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.utils.SecurityUtils;
import com.gp.common.utils.ServletUtils;
import com.gp.framework.interceptor.annotation.LimitRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author axing
 * @version 1.0
 * @date 2022/7/27 14:30:40
 */
@Component
public class LimitRequestAdapter extends HandlerInterceptorAdapter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LimitRequest annotation = method.getAnnotation(LimitRequest.class);
            if (annotation != null)
            {
                if (!this.isLimitRequest(request,annotation))
                {
                    String messages = MessagesUtils.get("frequent.requests.are.not.allowed");
                    AjaxResult ajaxResult = AjaxResult.error(messages);
                    ServletUtils.renderString(response, JSONObject.toJSONString(ajaxResult));
                    return false;
                }
            }
            return true;
        }
        else
        {
            return super.preHandle(request, response, handler);
        }
    }

    private boolean isLimitRequest(HttpServletRequest request, LimitRequest annotation) {
        String key = annotation.key();
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(key);
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        // 填充表达式上下文环境
        ctx.setRootObject(new SecurityUtils());
        String userInfo = expression.getValue(ctx, String.class);
        //这里后台默认中文
        MessagesUtils.setLang(Locale.CHINA.toString());
        return stringRedisTemplate.opsForValue().setIfAbsent(StrUtil.format(RedisKey.limitFormat, userInfo, request.getRequestURI()), "1", annotation.time(), TimeUnit.SECONDS);
    }


}
