package com.gp.common.base.utils;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

public class TraceUtil {
    public static void putTraceIdIfAbsent() {
        if (MDC.get("traceId") == null) {
            MDC.put("traceId", IdUtil.fastSimpleUUID());
        }
    }
    public static String getTraceId() {
        return MDC.get("traceId");
    }
}
