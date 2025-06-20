package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnProperty(
//        name = "kraft.kraft-ui.enabled",
//        havingValue = "true",
//        matchIfMissing = true
//)

public class KraftUIAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftUIAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("✅ Kraft UI Auto Configuration Loaded!");
    }

    @Bean
    public String helloKraftUi() {
        System.out.println("🔧 Kraft UI enabled");
        return "KraftrActive";
    }

}
