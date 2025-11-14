package com.kraftadmin;

import com.kraftadmin.annotations.ConditionalOnKraftAdminEnabled;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

//@Configuration
//@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true")
@AutoConfiguration
@ConditionalOnKraftAdminEnabled
public class KraftCoreAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(KraftCoreAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info(" Kraft core Initialized");
    }

    @Bean
    public String helloKraftCore() {
        System.out.println("Kraft core enabled");
        return "KraftActive";
    }

}
