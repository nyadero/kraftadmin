package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

//@Configuration
//@ConditionalOnProperty(name = "kraft.kraft-jpa.enabled", havingValue = "true", matchIfMissing = true)
//@ImportAutoConfiguration({
//        DataSourceAutoConfiguration.class,
//        JpaRepositoriesAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class
//})
//public class KraftrJPAAutoConfiguration {
//    private static final Logger log = LoggerFactory.getLogger(KraftrJPAAutoConfiguration.class);
//
//    @PostConstruct
//    public void init() {
//        log.info("✅ Kraft JPA Auto Configuration Loaded!");
//    }
//
//    @Bean
//    public String jpa(){
//        System.out.println("Kraft jpa enabled");
//        return "Jpa Enabled";
//    }
//}

@Configuration
@ImportAutoConfiguration({
        DataSourceAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
//@EnableJpaRepositories(basePackages = "com.bowerzlabs.repository.kraftrepos")
//@EntityScan(basePackages = "com.bowerzlabs.models.kraftmodels")
//@ComponentScan(basePackages = {"com.bowerzlabs.services", "com.bowerzlabs.repository", "com.bowerzlabs.utils"})
public class KraftrJPAAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KraftrJPAAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("✅ Kraft JPA Auto Configuration Loaded!");
    }

    @Bean
    public String jpa(){
        System.out.println("Kraft jpa enabled");
        return "Jpa Enabled";
    }
}
