package com.gp.common.base.utils;//package com.gp.common.base.utils;
//
//import cn.hutool.core.map.MapUtil;
//import com.alibaba.ttl.TransmittableThreadLocal;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.util.CollectionUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 上下文工具类
// * @author axing
// * @version 1.0
// * @date 2023/10/31/031 14:42
// */
//public class IskyContextHolder {
//
//    /**
//     * 本地线程
//     */
//    private static ThreadLocal<Map<String, Object>> local = new TransmittableThreadLocal<>();
//
//    private IskyContextHolder() {
//        // 禁用构造函数
//    }
//
//
//    /**
//     * 初始化业务上下文
//     */
//    public static void init() {
//        if (local.get() == null) {
//            local.set(MapUtil.newHashMap());
//        }
//    }
//
//    /**
//     * 增加属性
//     *
//     * @param key   属性<code>key</code>
//     * @param value 属性<code>value</code>
//     */
//    public static ThreadLocal<Map<String, Object>> addProperty(String key, Object value) {
//        if (StringUtils.isEmpty(key)) {
//            throw new NullPointerException("key parameter is blank");
//        }
//        Map<String, Object> properties = local.get();
//        if (properties == null) {
//            properties = new HashMap<>();
//            local.set(properties);
//        }
//        properties.put(key, value);
//        return local;
//    }
//
//    /**
//     * 获取属性
//     *
//     * @param key
//     * @return 属性
//     */
//    public static Object getProperty(String key) {
//        if (StringUtils.isEmpty(key)) {
//            return null;
//        }
//        Map<String, Object> properties = local.get();
//        if (CollectionUtils.isEmpty(properties)) {
//            return null;
//        }
//        return properties.get(key);
//    }
//
//    /**
//     * 清除当前线程上下文
//     */
//    public static void clear() {
//        local.remove();
//    }
//
//    /**
//     * 清除当前线程上下文
//     */
//    public static void clear(String key) {
//        Map<String, Object> properties = local.get();
//        properties.remove(key);
//    }
//}
