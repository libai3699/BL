package com.common.core.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.common.core.nacos.BNacosParam;
import com.common.core.prop.SwaggerProp;
import com.common.core.util.Md5Utils;
import com.gp.common.base.utils.ShellUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Data
public class JsonXssFilter extends OncePerRequestFilter {

    public static List<String> uris = CollUtil.newArrayList();

    public static final String swagger = "swagger";

    private SwaggerProp swaggerProp;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        BNacosParam bNacosParam = SpringUtil.getBean(BNacosParam.class);
        boolean checkXss = bNacosParam.getCheckXss();
        if (!checkXss) {
            if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) && MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())) {
                JsonXssRequestWrapper wrappedRequest = new JsonXssRequestWrapper(request, response);
                filterChain.doFilter(wrappedRequest, response);
            }else {
                filterChain.doFilter(request, response);
            }
            return;
        }
        if (HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) && MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType())|| HttpMethod.POST.name().equalsIgnoreCase(request.getMethod()) &&MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(request.getContentType())) {
            // 放行的接口
            String[] split = bNacosParam.getXxsUrl().split(",");
            List<String> currentUris = new ArrayList<>(uris); // 创建一个新的列表
            currentUris.addAll(CollUtil.newArrayList(split));
            if (matchesAnyPattern(request.getRequestURI(), currentUris)) {
                filterChain.doFilter(request, response);
            } else {
                JsonXssRequestWrapper wrappedRequest = new JsonXssRequestWrapper(request, response);
                // 校验xss注入
                // if (wrappedRequest.isCheckIsXss()) {
                //     checkXss(response);
                //     return;
                // }
                // 校验命令注入
                if (check(request, response, wrappedRequest.getBody())) return;
                filterChain.doFilter(wrappedRequest, response);
            }
        } else {
            // 校验命令注入
            if (check(request, response, "")) return;
            filterChain.doFilter(request, response);
        }
    }


    public static void main(String[] args) {
        ArrayList<String> arr = CollUtil.newArrayList("/api/v1/*");

        if (matchesAnyPattern("/api/v1/1/1/1",arr )) {
            System.out.println(1);
        }else {
            System.out.println(2);
        }
    }

    private boolean check(HttpServletRequest request, HttpServletResponse response, String postParam) throws IOException {
        Map<String, String> headerMap = ServletUtil.getHeaderMap(request);
        Map<String, String> paramMap = ServletUtil.getParamMap(request);
        paramMap.remove("link");
        String headerStr = JSON.toJSONString(headerMap);
        String paramStr = JSON.toJSONString(paramMap);
        String checkStr = new StringBuilder()
                .append(request.getRequestURL()).append(" ")
                .append(headerStr).append(" ")
                .append(paramStr).append(" ")
                .append(postParam).append(" ").toString();
        if(checkStr.contains("swagger-ui.html")){

        }else {
            if (ShellUtil.checkCommandInjection(checkStr)) {
                log.info("安防校验失败: {}", checkStr);
                // 校验失败，设置响应状态码和错误信息
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.setContentType("text/plain");
                response.getWriter().write(Md5Utils.complexMd5("Validation failed."));
                //校验一下是个乱码

                // 不再调用 chain.doFilter，请求处理在此终止
                return true;
            }
        }

        return false;
    }


    private boolean checkXss(HttpServletResponse response) throws IOException {
        // 校验失败，设置响应状态码和错误信息
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        response.setContentType("text/plain");
        response.getWriter().write(Md5Utils.complexMd5("Validation failed."));
        // 不再调用 chain.doFilter，请求处理在此终止
        return true;
    }

    private static boolean matchesAnyPattern(String uri, List<String> patterns) {
        for (String pattern : patterns) {
            if (Pattern.matches(convertToRegex(pattern), uri)) {
                return true;
            }
        }
        return false;
    }

    private static String convertToRegex(String pattern) {
        return pattern.replace(".", "\\.").replace("*", ".*");
    }


}
