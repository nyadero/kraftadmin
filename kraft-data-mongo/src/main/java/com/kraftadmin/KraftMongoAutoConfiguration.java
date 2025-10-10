package com.kraftadmin;

import com.kraftadmin.annotations.ConditionalOnKraftMongoEnabled;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//@AutoConfiguration
//@ConditionalOnProperty(prefix = "kraft.kraft-admin", name = "primaryDB", havingValue = "MONGO")
//@EnableMongoRepositories(basePackages = "com.kraftadmin.mongorepos")
//@ImportAutoConfiguration(exclude = {
//        DataSourceAutoConfiguration.class,
//        JpaRepositoriesAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class
//})
//public class KraftMongoAutoConfiguration {
//
//    private static final Logger log = LoggerFactory.getLogger(KraftMongoAutoConfiguration.class);
//
//    @PostConstruct
//    public void init() {
//        log.info("Kraft Mongo Auto Configuration Loaded!");
//    }
//
//    @Bean
//    public String mongodb(){
//        System.out.println("Kraft mongo enabled");
//        return "Mongo Enabled";
//    }
//
//}

@AutoConfiguration
@ConditionalOnProperty(prefix = "kraft.kraft-admin", name = "primaryDB", havingValue = "MONGO")
@EnableMongoRepositories(basePackages = "com.kraftadmin.mongorepos")
@ConditionalOnKraftMongoEnabled
//@EnableMongoRepositories(
//        basePackages = "com.kraftadmin.repos.mongo", // separate package
//        mongoTemplateRef = "kraftMongoTemplate"
//)
//@Configuration
//@Conditional(MongoPrimaryDBEnabled.class)
//@EntityScan(
//        basePackages = "com.kraftadmin",
//        excludeFilters = {
//                @ComponentScan.Filter(
//                        type = FilterType.REGEX,
//                        pattern = "com\\.kraftadmin\\..*\\.jpa\\..*"
//                ),
//                @ComponentScan.Filter(
//                        type = FilterType.ANNOTATION,
//                        classes = {Entity.class, Table.class}
//                )
//        }
//)
public class KraftMongoAutoConfiguration {


    private static final Logger log = LoggerFactory.getLogger(KraftMongoAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("✅ KraftAdmin Mongo AutoConfiguration loaded (documents + repos active)");
    }
}

