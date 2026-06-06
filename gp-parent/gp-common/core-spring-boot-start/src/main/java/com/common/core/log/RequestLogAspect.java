package com.common.core.log;


import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.gp.common.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志方面
 *
 * @author dan
 * @version 1.0
 * @date 2020/11/04
 */
@Slf4j
@Component
@Aspect
public class RequestLogAspect {

//    @Resource
//    private Tracer tracer;

    /**
     * 请求地址
     */
//    @Pointcut("execution(* com.ruoyi.project.controller..*(..))")
    @Pointcut("execution(* com.gp..*.*Controller.*(..))" )
    public void requestServer() {
    }
    /**
     * 请求地址
     */
//    @Pointcut("execution(* com.ruoyi.project.controller..*(..))")
    @Pointcut("execution(* com.gp..manage.CoreHandler.doKey(..))" )
    public void requestServerBot() {
    }

    /**
     * 任务地址
     */
//    @Pointcut("execution(* com.gp.project.controller..*(..))")
//    public void taskServer() {
//    }

    /**
     * 请求切面
     *
     * @param proceedingJoinPoint 进行连接点
     * @return {@link Object}
     * @throws Throwable throwable
     * @author dan
     * @since 2020/11/17
     */
    @Around("requestServer()")
    public Object doRequestAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return runProceed(proceedingJoinPoint);
    }
    @Around("requestServerBot()")
    public Object doRequestAroundBot(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return runProceedBot(proceedingJoinPoint);
    }

    /**
     * 任务切面
     *
     * @param proceedingJoinPoint 进行连接点
     * @return {@link Object}
     * @throws Throwable throwable
     */
//    @Around("taskServer()")
    public Object doTaskAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return runProceed(proceedingJoinPoint);
    }

    private Object runProceedBot(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String url = "非http请求";
        String ip = "非http请求";
        String product = "非http请求";
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            url = request.getRequestURL().toString();
            ip = IpUtil.getRealIpAddr();
            product = ServletUtil.getHeader(request, "product", Charset.defaultCharset());
        }
        //结果
        Map<String, Object> paramMap = getRequestParamsByProceedingJoinPoint(proceedingJoinPoint);
        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        String dateTime = DateUtil.now();
        if( !url.endsWith("getCheck") && !url.endsWith("health") && !url.endsWith("queryActivityRedPoint")) {
            String consoleLog = new StringBuffer().append("\n").append("----------------------------请求时间    ").append(dateTime).append("----------------------------").append("\n")
                    .append("Url      :   ").append(url).append("\n")
                    .append("Product  :   ").append(product).append("\n")
                    .append("Ip       :   ").append(ip).append("\n")
                    .append("Class    :   ").append(className).append("\n")
                    .append("Method   :   ").append(methodName).append("\n")
                    .append("Result   :   ").append("成功").append("\n")
                    .append("Params   :   ").append(paramMap.toString()).append("\n")
                    .toString();
            log.info(consoleLog);
        }
        MDC.put("url",url);
        MDC.put("ip",ip);
        MDC.put("params",paramMap.toString());
//        Span span = tracer.nextSpan().name("botVisit").tag("params", jsonObjectParam.toString());

//        tracer.startScopedSpan("botVisit").tag("params", jsonObjectParam.toString()).finish();
        return proceedingJoinPoint.proceed();
    }

    private Object runProceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        long start = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String url = "非http请求";
        String ip = "非http请求";
        String product = "非http请求";
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            url = request.getRequestURL().toString();
            ip = IpUtil.getRealIpAddr();
            product = ServletUtil.getHeader(request, "product", Charset.defaultCharset());
        }
        //结果
        Map<String, Object> paramMap = getRequestParamsByProceedingJoinPoint(proceedingJoinPoint);

        String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        String dateTime = DateUtil.formatDateTime(new Date());
        if( !url.endsWith("getCheck") && !url.endsWith("health") && !url.endsWith("queryActivityRedPoint")) {
            String consoleLog = new StringBuffer().append("\n").append("----------------------------请求时间    ").append(dateTime).append("----------------------------").append("\n")
                    .append("Url      :   ").append(url).append("\n")
                    .append("Product  :   ").append(product).append("\n")
                    .append("Ip       :   ").append(ip).append("\n")
                    .append("Class    :   ").append(className).append("\n")
                    .append("Method   :   ").append(methodName).append("\n")
                    .append("Result   :   ").append("成功").append("\n")
                    .append("Params   :   ").append(paramMap.toString()).append("\n")
                    .toString();
            log.info(consoleLog);
        }
        MDC.put("url",url);
        MDC.put("ip",ip);
        MDC.put("params", paramMap.toString());
//        if (tracer.currentSpan() != null) {
//            tracer.currentSpan().tag("params", jsonObjectParam.toString());
//        }
        return proceedingJoinPoint.proceed();
    }

    /**
     * 执行异常
     *
     * @param joinPoint 连接点
     * @param e         e
     * @return
     * @author dan
     * @since 2020/11/17
     */
    @AfterThrowing(pointcut = "requestServer()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) {
        Signature signature = joinPoint.getSignature();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String url = "非http请求";
        String ip = "非http请求";
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            url = request.getRequestURL().toString();
            ip =  ServletUtil.getClientIP(request);
        }
        Map<String, Object> paramMap = getRequestParamsByJoinPoint(joinPoint);
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String exceptionMsg = getExceptionSimpleMsg(e);

        String dateTime = DateUtil.formatDateTime(new Date());
        String consoleLog = new StringBuffer().append("\n").append("---------------- 请求时间    ").append(dateTime).append(" ----------------").append("\n")
                .append("Url      :   ").append(url).append("\n")
                .append("Ip       :   ").append(ip).append("\n")
                .append("Class    :   ").append(className).append("\n")
                .append("Method   :   ").append(methodName).append("\n")
                .append("Result   :   ").append("失败").append("\n")
                .append("Params   :   ").append(paramMap.toString()).append("\n")
                .append("Error    :   ").append(exceptionMsg).append("\n")
                .toString();
//        if (tracer.currentSpan() == null) {
//            Span span = tracer.nextSpan().name("error").tag("errorMsg", exceptionMsg);
//            try {
//                tracer.withSpanInScope(span);
//            } finally {
//                span.finish();
//
//            }
//        }else {
//            tracer.currentSpan().tag("errorMsg", exceptionMsg);
//        }
        log.error(consoleLog);
    }

    /**
     * 获取入参
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                Object value = paramValues[i];
                //如果是文件对象
                if (value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    value = file.getOriginalFilename();  //获取文件名
                }
                requestParams.put(paramNames[i], value);
            }
        }
        return requestParams;
    }

    /**
     * 获取exception的字符串
     *
     * @param e e
     * @return {@link String }
     * @author dan
     * @since 2020/11/17
     */
    public static String getExceptionMsg(Exception e) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.getBuffer().toString().replaceAll("\\$", "T");
    }

    /**
     * 获取exception的字符串
     *
     * @param e e
     * @return {@link String }
     * @author dan
     * @since 2020/11/17
     */
    public static String getExceptionSimpleMsg(Exception e) {
        //获取异常信息
        StringBuffer esb = new StringBuffer();
        StackTraceElement[] stacks = e.getStackTrace();
        if (stacks != null && stacks.length>0) {
            esb.append(" class: ").append(stacks[0].getClassName())
                    .append(", method: ").append(stacks[0].getMethodName())
                    .append(", line: ").append(stacks[0].getLineNumber())
                    .append(", message: ").append(e.toString());
        }
        return esb.toString();
    }

}
