package com.bowerzlabs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private KraftProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**", "/css/**", "/uploads/**", "/images/**")
                .addResourceLocations("file:uploads/")
                .addResourceLocations("classpath:/static/css/")
                .addResourceLocations("/uploads/")
                .addResourceLocations("classpath:/static/images/")
                .addResourceLocations("classpath:/static/js/");
    }


}
