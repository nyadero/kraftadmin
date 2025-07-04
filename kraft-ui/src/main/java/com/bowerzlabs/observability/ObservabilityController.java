package com.bowerzlabs.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/observability")
public class ObservabilityController {

    private final HealthEndpoint healthEndpoint;

//    private final MetricsEndpoint metricsEndpoint;

    private final MeterRegistry meterRegistry;

    public ObservabilityController(HealthEndpoint healthEndpoint, MeterRegistry meterRegistry) {
        this.healthEndpoint = healthEndpoint;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/actuator/prometheus")
    public String actuator(
            Model model
    ){
        model.addAttribute("health", healthEndpoint.health());
        return "kraft-observability/index";
    }
}
