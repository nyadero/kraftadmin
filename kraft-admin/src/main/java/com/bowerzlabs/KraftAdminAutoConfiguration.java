package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true")
@ImportAutoConfiguration({
        KraftJPAAutoConfiguration.class,
        KraftCoreAutoConfiguration.class,
        KraftSecurityAutoConfiguration.class,
        KraftUIAutoConfiguration.class
})
public class KraftAdminAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KraftAdminAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft Admina Library Initialized");
    }

    @Bean
    public String helloAdmina() {
        System.out.println("Kraft Admina Library Initialized in print method");
        return "KraftActive";
    }

}
