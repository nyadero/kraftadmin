package com.kraftadmin.config;

import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

//@Configuration
public class WebConfig extends DelegatingWebMvcConfiguration {

    private final KraftProperties kraftProperties;

    public WebConfig(KraftProperties kraftProperties) {
        this.kraftProperties = kraftProperties;
    }

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new PrefixedRequestMappingHandlerMapping(kraftProperties.getBaseUrl());
    }
}
