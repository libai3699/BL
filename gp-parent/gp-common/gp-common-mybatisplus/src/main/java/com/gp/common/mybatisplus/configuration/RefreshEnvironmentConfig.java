package com.gp.common.mybatisplus.configuration;

import com.gp.common.mybatisplus.listener.ConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
public class RefreshEnvironmentConfig implements ApplicationListener<EnvironmentChangeEvent>, EnvironmentAware, BeanPostProcessor {

    private Environment environment;

    private final Map<String, Map<Method, Object>> listeners = new ConcurrentHashMap<>(64);

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Map<Method, ConfigListener> annotatedMethods = MethodIntrospector.selectMethods(targetClass, this::findListenerAnnotations);
            if (!annotatedMethods.isEmpty()) {
                for (Map.Entry<Method, ConfigListener> entry : annotatedMethods.entrySet()) {
                    listeners.computeIfAbsent(entry.getValue().key(), k -> new HashMap<>()).put(entry.getKey(), bean);
                    log.info("Register @ConfigListener methods processed on bean {} listener key {}", beanName, entry.getValue().key());
                }
            }
            this.nonAnnotatedClasses.add(bean.getClass());
        }
        return bean;

    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        keys.forEach(key -> {
            String value = environment.getProperty(key);
            int index = 0;
            do {
                index = key.indexOf(".", index + 1);
                String subKey = index > 0 ? key.substring(0, index) : key;
                Map<Method, Object> methodMap = listeners.get(subKey);
                if (methodMap != null) {
                    methodMap.forEach((method, bean) -> {
                        try {
                            method.invoke(bean, key, value);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error("Refresh @ConfigListener {}, error {}", bean, e.getMessage(), e);
                        }
                    });
                }
            } while (index > 0);
        });
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    private ConfigListener findListenerAnnotations(Method method) {
        return findListenerAnnotationsByAnnotatedElement(method);
    }

    private ConfigListener findListenerAnnotationsByAnnotatedElement(AnnotatedElement element) {
        return AnnotationUtils.findAnnotation(element, ConfigListener.class);
    }
}
