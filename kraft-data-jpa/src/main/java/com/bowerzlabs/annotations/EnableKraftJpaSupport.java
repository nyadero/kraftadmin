package com.bowerzlabs.annotations;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@EnableJpaRepositories(basePackages = "com.bowerzlabs.repository.kraftrepos")
@EntityScan(basePackages = "com.bowerzlabs.models.kraftmodels")
@ComponentScan(basePackages = {
        "com.bowerzlabs.services",
        "com.bowerzlabs.utils"
})
@ImportAutoConfiguration({
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
})
public @interface EnableKraftJpaSupport {
}
