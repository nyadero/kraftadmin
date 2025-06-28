package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

//@Configuration
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class KraftUIAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftUIAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft UI Auto Configuration Loaded!");
    }

    @Bean
    public String helloKraftUi() {
        System.out.println("Kraft UI enabled");
        return "KraftActive";
    }

}
