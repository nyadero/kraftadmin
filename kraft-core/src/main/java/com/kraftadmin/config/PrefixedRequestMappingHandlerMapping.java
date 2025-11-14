package com.kraftadmin.config;

import com.kraftadmin.annotations.AdminController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Slf4j
public class PrefixedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final String prefix;

    public PrefixedRequestMappingHandlerMapping(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return AnnotationUtils.findAnnotation(beanType, AdminController.class) != null;
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        Class<?> beanType = (handler instanceof String)
                ? obtainApplicationContext().getType((String) handler)
                : handler.getClass();

        if (beanType != null && AnnotationUtils.findAnnotation(beanType, AdminController.class) != null) {
            String prefixedPath = "/" + prefix;
            RequestMappingInfo prefixedMapping = RequestMappingInfo
                    .paths(prefixedPath)
                    .build()
                    .combine(mapping);
            log.info("prefixMapping {}, prefix {}", prefixedMapping, prefix);
            super.registerHandlerMethod(handler, method, prefixedMapping);
        } else {
            super.registerHandlerMethod(handler, method, mapping);
        }
    }
}

