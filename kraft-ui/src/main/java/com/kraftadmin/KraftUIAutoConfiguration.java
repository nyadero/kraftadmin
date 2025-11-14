package com.kraftadmin;

import com.kraftadmin.annotations.ConditionalOnKraftAdminEnabled;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnKraftAdminEnabled
public class KraftUIAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftUIAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft UI Auto Configuration Loaded!");
    }

    @Bean
    public String helloKraftUi() {
        return "KraftActive";
    }

}
