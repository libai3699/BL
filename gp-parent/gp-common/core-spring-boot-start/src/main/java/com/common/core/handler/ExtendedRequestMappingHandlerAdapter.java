package com.common.core.handler;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2024/6/14/014 15:32
 */
public class ExtendedRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> methods) {

        return new ServletRequestDataBinderFactory(methods, getWebBindingInitializer()) {

            @Override
            protected ServletRequestDataBinder createBinderInstance(
                    Object target, String name, NativeWebRequest request) throws Exception {

                ServletRequestDataBinder binder = super.createBinderInstance(target, name, request);
                String[] fields = binder.getDisallowedFields();
                List<String> fieldList = new ArrayList<>(fields != null ? Arrays.asList(fields) : Collections.emptyList());
                fieldList.addAll(Arrays.asList("class.*", "Class.*", "*.class.*", "*.Class.*"));
                binder.setDisallowedFields(fieldList.toArray(new String[] {}));
                return binder;
            }
        };
    }
}
