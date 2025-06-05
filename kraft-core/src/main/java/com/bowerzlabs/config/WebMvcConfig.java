package com.bowerzlabs.config;

import com.bowerzlabs.components.RequestTimingInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RequestTimingInterceptor requestTimingInterceptor;

    @Autowired
    private KraftrProperties properties;

    @Autowired
    private HttpSession session;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTimingInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Adjust the classpath location to your actual library module's resource path
        registry.addResourceHandler("/js/**", "/css/**", "/uploads/**")
                .addResourceLocations("classpath:/static/css/")
                .addResourceLocations("/uploads/")
                .addResourceLocations("classpath:/static/js/");
    }


}
