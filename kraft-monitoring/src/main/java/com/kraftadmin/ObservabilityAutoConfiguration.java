package com.kraftadmin;

import com.kraftadmin.annotations.ConditionalOnKraftAdminEnabled;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@AutoConfigureAfter(name = {
        "org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration",
        "org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration"
})
//@AutoConfiguration
@ConditionalOnKraftAdminEnabled
public class ObservabilityAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ObservabilityAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("Kraft observability Initialized");
    }

    @Bean
    public String helloKraftMonitoring() {
        System.out.println("observability enabled");
        return "KraftActive";
    }

//    @Bean
//    @ConfigurationProperties(prefix = "management.endpoints.web")
//    public WebEndpointProperties webEndpointProperties() {
//        WebEndpointProperties props = new WebEndpointProperties();
////        props.setExposure(new WebEndpointProperties.Exposure());
//        props.getExposure().setInclude(Set.of("*"));
//        return props;
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public ObservabilityTagsCustomizer observabilityTagsCustomizer(Environment env) {
//        return new ObservabilityTagsCustomizer(env);
//    }
//
//    @Bean
//    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(ObservabilityTagsCustomizer customizer) {
//        return registry -> registry.config().commonTags("application", customizer.getAppName());
//    }

    @EventListener
    public void onServerReady(WebServerInitializedEvent event) {
        System.out.println("[KraftLib] Actuator Prometheus endpoint at: http://localhost:" +
                event.getWebServer().getPort() + "/admin/observability/actuator/prometheus");
    }

//    @Bean
//    public HealthDetailsCustomizer healthDetailsCustomizer() {
//        return registry -> registry.getPrimary().withDetailsShown(ShowDetails.ALWAYS);
//    }
}