package com.gp.common.mybatisplus.deal;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.github.houbb.opencc4j.core.impl.ZhConvertBootstrap;
import com.gp.common.mybatisplus.aspect.Localized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;
import java.util.Locale;

@Slf4j
public class DataProcessor {
    public static void process(Object obj) {
        if (obj == null) {
            return;
        }
        String  localeLanguageOrigin =LocaleContextHolder.getLocale().toString();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Localized.class)) {
                Localized annotation = field.getAnnotation(Localized.class);
                try {
                    field.setAccessible(true);
                    String  localeLanguage =LocaleContextHolder.getLocale().toString();

                    if(localeLanguage.equals(Locale.TAIWAN.toString())){
                        //因为台湾和简体都是zh 开头的 所以给他反转下
                        localeLanguage=  convertKey(Locale.TAIWAN.toString());
                    }
                    String language = localeLanguage.split("_")[0];
                    // 将首字母大写
                    language = Character.toUpperCase(language.charAt(0)) + language.substring(1);
                    String needField = annotation.value() + language;
                    Field declaredField = obj.getClass().getDeclaredField(needField);
                    declaredField.setAccessible(true);
                    // 使用反射获取字段值并根据语言环境选择返回值
                    Object processedValue = declaredField.get(obj);
                    // 设置当前字段的值为处理后的值
                    if (processedValue == null || StrUtil.isEmpty((String) processedValue)) {
                        field.setAccessible(true);
                        String languageZh = Locale.US.toString().split("_")[0];
                        // 将首字母大写
                        language = Character.toUpperCase(languageZh.charAt(0)) + languageZh.substring(1);
                        String needFieldZh = annotation.value() + language;
                        Object processedValueZh = null;
                        try {
                            Field declaredFields = obj.getClass().getDeclaredField(needFieldZh);
                            declaredFields.setAccessible(true);
                            // 使用反射获取字段值并根据语言环境选择返回值
                            processedValueZh = declaredFields.get(obj);
                            // 设置当前字段的值为处理后的值
                            field.set(obj, dealZHTW(localeLanguageOrigin, processedValueZh));
                        } catch (NoSuchFieldException ex) {
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        // 设置当前字段的值为处理后的值
                        field.set(obj, dealZHTW(localeLanguageOrigin, processedValue));
                    }
                } catch (NoSuchFieldException e) {
//                    log.debug("未找到字段: {}", e.getMessage());
                    //没找到的话就放一个英文的吧
                    field.setAccessible(true);
                    String language = Locale.US.toString().split("_")[0];
                    //台湾没找到就用 中文
                    if( localeLanguageOrigin.equals(Locale.TAIWAN.toString())){
                        language =Locale.CHINA.toString().split("_")[0];
                    }

                    // 将首字母大写
                    language = Character.toUpperCase(language.charAt(0)) + language.substring(1);
                    String needField = annotation.value() + language;
                    Object processedValue = null;
                    try {
                        Field declaredField = obj.getClass().getDeclaredField(needField);
                        declaredField.setAccessible(true);
                        // 使用反射获取字段值并根据语言环境选择返回值
                        processedValue = declaredField.get(obj);
                        //如果英语配置的为null，就用中文的
                        if (processedValue == null || StrUtil.isEmpty((String) processedValue)) {
                            field.setAccessible(true);
                            String languageZh = Locale.CHINA.toString().split("_")[0];
                            // 将首字母大写
                            language = Character.toUpperCase(languageZh.charAt(0)) + languageZh.substring(1);
                            String needFieldZh = annotation.value() + language;
                            Object processedValueZh = null;
                            try {
                                Field declaredFields = obj.getClass().getDeclaredField(needFieldZh);
                                declaredFields.setAccessible(true);
                                // 使用反射获取字段值并根据语言环境选择返回值
                                processedValueZh = declaredFields.get(obj);
                                // 设置当前字段的值为处理后的值
                                field.set(obj, dealZHTW(localeLanguageOrigin, processedValueZh));
                            } catch (NoSuchFieldException ex) {
                                 log.debug("未找到字段: {}", ex.getMessage());

                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            // 设置当前字段的值为处理后的值
                            field.set(obj, dealZHTW(localeLanguageOrigin, processedValue) );
                        }

                    } catch (NoSuchFieldException ex) {
                        field.setAccessible(true);
                        String languageZh = Locale.US.toString().split("_")[0];
                        // 将首字母大写
                        language = Character.toUpperCase(languageZh.charAt(0)) + languageZh.substring(1);
                        String needFieldZh = annotation.value() + language;
                        Object processedValueZh = null;
                        try {
                            Field declaredFields = obj.getClass().getDeclaredField(needFieldZh);
                            declaredFields.setAccessible(true);
                            // 使用反射获取字段值并根据语言环境选择返回值
                            processedValueZh = declaredFields.get(obj);
                            // 设置当前字段的值为处理后的值
                            field.set(obj, dealZHTW(localeLanguageOrigin, processedValueZh));
                        } catch (NoSuchFieldException exss) {
                            log.debug("未找到字段: {}", ex.getMessage());

                        } catch (IllegalAccessException exss) {
                            throw new RuntimeException(exss);
                        }
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (Exception e) {
                    log.error("处理字段时出现异常: {}", e.getMessage());
                }
            }
        }
    }

    private static String dealZHTW(String localeLanguageOrigin, Object processedValueZh) {
        if(processedValueZh == null){
            return "";
        }
//        if(localeLanguageOrigin.equals(Locale.TAIWAN.toString())){
//            String simpleCn = processedValueZh.toString();
//            ZhConvertBootstrap zhConvertBootstrap = ZhConvertBootstrap.newInstance();
//            return zhConvertBootstrap.toTraditional(simpleCn);
//        }
        return processedValueZh.toString();
    }

    public static void main(String[] args) {
        //因为台湾和简体都是zh 开头的 所以给他反转下
        String   language=  DataProcessor.convertKey(Locale.TAIWAN.toString()).toString().split("_")[0];;
        // 将首字母大写
        System.out.println(language);

    }
    public static String convertKey(String key) {
        // 分割字符串
        String[] parts = key.split("_");
        // 进行转换
        return parts[1].toLowerCase() + "_" + parts[0].toUpperCase();
    }
}


