package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class KraftSecurityAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftSecurityAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft security Initialized");
    }

    @Bean
    public String helloKraftSecurity() {
        System.out.println("Security enabled");
        return "KraftActive";
    }

}
