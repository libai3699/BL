package com.gp.common.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Bean复制优化
 *
 * @author Volt
 * @version Id: RegisterRequst.java, v 0.1 2021年12月23日 18:14, By Volt Exp $
 */
public class BeanUtil {

    /**
     * null值不会被复制
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyPropertiesIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }


    @SneakyThrows
    public static <T1, T2> List<T2> copyListPropertiesIgnoreNull(List<T1> src, Class<T2> clazz) {
        ArrayList<T2> targers = new ArrayList<>();
        for (T1 src1 : src) {
            T2 t2 = clazz.newInstance();
            BeanUtil.copyPropertiesIgnoreNull(src1, t2);
            targers.add(t2);
        }
        return targers;
    }


    /**
     * 去掉字符串中斜线后, 再转换为Map
     * "{\"id\":338,\"cn\":\"域名不存在\",\"en\":\"Domain name does not exist\",\"th\":\"ไม่มีชื่อโดเมน\",\"vi\":\"\"}"
     *
     * @param body
     * @return
     */
    public static Map<String, String> clearJsonStringToMap(String body) {
        Object parse1 = JSON.parse(body);
        String value = parse1.toString();
        return JSON.parseObject(value, Map.class);
    }

    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }


    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap2(T bean) {
        try {
            return objToMap(bean);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 将对象转成TreeMap,属性名为key,属性值为value
     *
     * @param object 对象
     * @return
     * @throws IllegalAccessException
     */
    public static TreeMap<String, Object> objToMap(Object object) throws IllegalAccessException {

        Class clazz = object.getClass();
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>();

        while (null != clazz.getSuperclass()) {
            Field[] declaredFields1 = clazz.getDeclaredFields();

            for (Field field : declaredFields1) {
                String name = field.getName();

                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                Object value = field.get(object);
                // 恢复访问控制权限
                field.setAccessible(accessFlag);

                if (null != value && StringUtils.isNotBlank(value.toString())) {
                    //如果是List,将List转换为json字符串
                    if (value instanceof List) {
//                        value = JSON.toJSONString(value);
                    }
                    treeMap.put(name, value);
                }
            }

            clazz = clazz.getSuperclass();
        }
        return treeMap;
    }


    /**
     * 将对象转成TreeMap,属性名为key,属性值为value
     *
     * @param object 对象
     * @return
     * @throws IllegalAccessException
     */
    public static TreeMap<String, String> objToMapForSign(Object object) throws IllegalAccessException {

        Class clazz = object.getClass();
        TreeMap<String, String> treeMap = new TreeMap<>();

        while (null != clazz.getSuperclass()) {
            Field[] declaredFields1 = clazz.getDeclaredFields();

            for (Field field : declaredFields1) {
                String name = field.getName();

                // 获取原来的访问控制权限
                boolean accessFlag = field.isAccessible();
                // 修改访问控制权限
                field.setAccessible(true);
                Object value = field.get(object);
                // 恢复访问控制权限
                field.setAccessible(accessFlag);

                if (null != value && !field.getName().equals("sign") && StringUtils.isNotBlank(value.toString())) {
                    //如果是List,将List转换为json字符串
                    if (value instanceof List) {
                        value = JSON.toJSONString(value);
                    }
                    treeMap.put(name, String.valueOf(value));
                }
            }

            clazz = clazz.getSuperclass();
        }
        return treeMap;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }


    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = Lists.newArrayList();
        if (maps != null && maps.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = maps.size(); i < size; i++) {
                map = maps.get(i);
                bean = clazz.newInstance();
                mapToBean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }
}
