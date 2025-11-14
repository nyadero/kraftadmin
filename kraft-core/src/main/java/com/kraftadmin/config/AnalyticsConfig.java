package com.kraftadmin.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true")
public class AnalyticsConfig {
}
