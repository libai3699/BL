package com.gp.common.utils;

import com.alibaba.excel.annotation.ExcelProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZBJ
 * @version 1.0
 * @date 2020/5/28 17:40
 */
public class BeanUtils {

    private static final String RESULT = "result";
    private static final String FIELD_NAME = "fieldName";

    /**
     * 检查对象字段值为空
     *
     * @param bean      对象
     * @param noFilters 不过滤集合
     * @return boolean
     */
    public static Map<String, Object> checkFieldValueNull(Object bean, List<String> noFilters) {
        String fieldName = "";
        if (bean == null) {
            return null;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!noFilters.contains(field.getName())) {
                    String fieldGetName = parGetName(field.getName());
                    if (!checkGetMet(methods, fieldGetName)) {
                        continue;
                    }
                    Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                    Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                    if (fieldVal != null) {
                        if ("".equals(fieldVal)) {
                            fieldName = field.getName();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(RESULT,false);
                            map.put(FIELD_NAME,fieldName);
                            return map;
                        } else {
                            fieldName = field.getName();
                        }
                    } else {
                        fieldName = field.getName();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(RESULT,false);
                        map.put(FIELD_NAME,fieldName);
                        return map;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(RESULT,true);
        map.put(FIELD_NAME,fieldName);
        return map;
    }

    /**
     * 通过对象和属性名获取该属性的Excel值
     *
     * @param object   对象
     * @param fieldName 属性名称
     * @return {@link String}
     */
    public static String getExcelName(Object object,String fieldName) {
        Field f = null;
        try {
            f = object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ExcelProperty anno2 = f.getAnnotation(ExcelProperty.class);
        return anno2.value()[0];
    }

    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {}

}
