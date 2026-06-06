package com.gp.common.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class BeanCopyUtil {


    public static <T> T toBean(Map<String, Object> sourceMap, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanWrapper beanWrapper = new BeanWrapperImpl(target);
            PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String propertyName = propertyDescriptor.getName();
                if (sourceMap.containsKey(propertyName)) {
                    Object value = sourceMap.get(propertyName);
                    beanWrapper.setPropertyValue(propertyName, value);
                }
            }
            return target;
        } catch (Exception e) {
            log.error("BeanCopyUtil.toBean 转换失败 targetClass={}", targetClass.getSimpleName(), e);
            return null;
        }
    }

    public static <S, T> T copyProperties(S source, Class<T> targetClass) {
        T target = null;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            log.error("BeanCopyUtil.copyProperties 转换失败 targetClass={}", targetClass.getSimpleName(), e);
        }

        return target;
    }


    public static <S, T> List<T> copyToList(List<S> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            } catch (Exception e) {
                log.error("BeanCopyUtil.copyToList 转换失败，记录被跳过 source={}, targetClass={}", source, targetClass.getSimpleName(), e);
            }
        }
        return targetList;
    }

    public static <S, T> List<T> copyToList(Collection<S> sourceList, Class<T> targetClass) {
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, target);
                targetList.add(target);
            } catch (Exception e) {
                log.error("BeanCopyUtil.copyToList 转换失败，记录被跳过 source={}, targetClass={}", source, targetClass.getSimpleName(), e);
            }
        }
        return targetList;
    }


}
