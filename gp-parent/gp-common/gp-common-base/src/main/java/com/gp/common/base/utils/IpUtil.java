package com.gp.common.base.utils;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * @author axing
 * @version 1.0
 * @date 2022/9/21 19:31
 */
@Slf4j
public class IpUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 获得真正ip地址
     *
     * @return {@link String}
     */
    public static String getRealIpAddr() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "";
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return getCdnIPNoLog(request);
    }

    /**
     * 获取经过cdn之后的ip
     *
     * @return
     */
    public static String getCdnIPNoLog(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ips = ServletUtil.getHeader(request, "X-Forwarded-For", Charset.defaultCharset());
        log.info("X-Forwarded-For: {}", ips);
        if (ips == null || ips.length() == 0 || "unknown".equalsIgnoreCase(ips)) {
            ips = request.getRemoteAddr();
            return ips;
        }
        String[] ipArray = ips.split(",");
        if (ipArray.length == 1) {
            return ipArray[ipArray.length - 1].trim();
        } else {
            return ipArray[0].trim();
        }
    }

    /**
     * 获取经过cdn之后的ip
     *
     * @return
     */
    private static String getCdnIP(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ips = ServletUtil.getHeader(request, "X-Forwarded-For", Charset.defaultCharset());
        String ip = ServletUtil.getHeader(request, "X-Real-IP", Charset.defaultCharset());
        log.info("请求经过ip列表: {}", ips);
        log.info("真实IP: {}", ip);
        if (ips == null || ips.length() == 0 || "unknown".equalsIgnoreCase(ips)) {
            ips = request.getRemoteAddr();
            return ips;
        }
        String[] ipArray = ips.split(",");
        if (ipArray.length < 2) {
            return ipArray[0].trim();
        }
        // 倒数第一个ip是高防ip，倒数第二个ip就是真实ip了
//        return ips[ips.length - 1].trim(); // CC高防和李哥高防去倒数第二个(【用户-高防】 -->> 源站),阿里云高防取倒数第三个 (【用户-->>高防-->>WAF】-->> 源站)
        //倒数第一个是局域网ip,第二个是cdnip,第三个是外部访问真实ip
        return ipArray[0].trim();
    }

    public static void main(String[] args) {
        String[] ips = "34.150.46.224, 172.71.210.129 ".split(",");
        System.out.println(ips[0]);
    }
}
