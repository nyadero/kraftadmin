package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ImportAutoConfiguration({
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
//@EntityScan(basePackages = "com.bowerzlabs.models.kraftmodels")
//@ComponentScan(basePackages = {"com.bowerzlabs.services", "com.bowerzlabs.repository", "com.bowerzlabs.utils"})
public class KraftJPAAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftJPAAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft JPA Auto Configuration Loaded!");
    }

    @Bean
    public String jpa(){
        System.out.println("Kraft jpa enabled");
        return "Jpa Enabled";
    }
}
