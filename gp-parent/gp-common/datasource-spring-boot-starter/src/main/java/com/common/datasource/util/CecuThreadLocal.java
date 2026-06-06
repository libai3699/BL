package com.common.datasource.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 业务上下文线程类（确保线程隔离，不共享Map引用）
 */
@Slf4j
public class CecuThreadLocal {

    /**
     * 当前线程独占Map容器
     */
    private static final ThreadLocal<Map<String, String>> threadLocal = new TransmittableThreadLocal<>();

    private CecuThreadLocal() {}

    /**
     * 添加属性
     */
    public static void addProperty(Map<String, String> newEntries) {
        if (MapUtil.isEmpty(newEntries)) {
            throw new NullPointerException("key parameter is blank");
        }
        Map<String, String> properties = new HashMap<>(newEntries);
        threadLocal.set(properties);
    }

    /**
     * 获取属性
     */
    public static String getProperty(String key) {
        if (StringUtils.isEmpty(key)) return null;
        Map<String, String> properties = threadLocal.get();
        if (CollectionUtils.isEmpty(properties)) return null;
        return properties.get(key);
    }

    /**
     * 删除属性
     */
    public static void delProperty(String key) {
        if (StringUtils.isEmpty(key)) {
            throw new NullPointerException("key parameter is blank");
        }
        Map<String, String> properties = threadLocal.get();
        if (MapUtil.isNotEmpty(properties)) {
            properties.remove(key);
        }
    }

    /**
     * 清除当前线程上下文
     */
    public static void clear() {
        threadLocal.remove();
    }

    /**
     * 打印当前线程 ThreadLocal 内容
     */
    public static void debug() {
        Map<String, String> map = threadLocal.get();
        log.info("🔍 Thread [{}] 当前 ThreadLocal 内容: {}", Thread.currentThread().getName(), map);
    }

    /**
     * 防御式设置 Map（避免共享引用）
     */
    public static void setMapSafe(Map<String, String> map) {
        if (map == null) {
            threadLocal.remove();
        } else {
            // 拷贝一份，防止引用共享
            threadLocal.set(new HashMap<>(map));
        }
    }

    /**
     * 获取只读副本
     */
    public static Map<String, String> getReadonlyMap() {
        Map<String, String> map = threadLocal.get();
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    /**
     * 判断是否开发环境（你可以自定义方式）
     */
    private static boolean isDevEnv() {
        String env = System.getProperty("spring.profiles.active");
        return "dev".equalsIgnoreCase(env) || "local".equalsIgnoreCase(env);
    }
}
