package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnProperty(
//        name = "kraft.kraft-core.enabled",
//        havingValue = "true",
//        matchIfMissing = true
//)
public class KraftrCoreAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KraftrCoreAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("🔧 Kraftr core Initialized");
    }

    @Bean
    public String helloKraftCore() {
        System.out.println("🔧 Kraftr core enabled");
        return "KraftrActive";
    }

}
