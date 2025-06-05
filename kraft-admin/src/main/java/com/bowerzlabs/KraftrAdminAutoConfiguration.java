package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true")
@ImportAutoConfiguration({
        KraftrJPAAutoConfiguration.class,
        KraftrCoreAutoConfiguration.class,
        KraftSecurityAutoConfiguration.class,
        KraftUIAutoConfiguration.class
})
@ComponentScan(
        basePackages = {
//                "com.bowerzlabs"
//                "com.bowerzlabs.kraft.kraft-admin",
//                "com.bowerzlabs.kraft.kraft-ui",
//                "com.bowerzlabs.kraft.kraft-data-jpa",
//                "com.bowerzlabs.kraft.kraft-security",
//                "com.bowerzlabs.kraft.kraft-core"
        }
)
public class KraftrAdminAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KraftrAdminAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("🔧 Kraft Admina Library Initialized");
    }

    @Bean
    public String helloAdmina() {
        System.out.println("🔧 Kraft Admina Library Initialized in print method");
        return "KraftrActive";
    }

}
