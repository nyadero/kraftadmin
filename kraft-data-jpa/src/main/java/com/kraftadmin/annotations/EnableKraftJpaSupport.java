package com.kraftadmin.annotations;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;


/**
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@EnableJpaRepositories(basePackages = "com.kraftadmin.repository.kraftrepos")
@EntityScan(basePackages = "com.kraftadmin.kraft_jpa_entities.kraft_jpa_entities")
@ComponentScan(basePackages = {
        "com.kraftadmin.services",
        "com.kraftadmin.utils"
})
@ImportAutoConfiguration({
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
})
public @interface EnableKraftJpaSupport {
}
