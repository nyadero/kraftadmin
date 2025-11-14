package com.kraftadmin;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class ObservabilityTagsCustomizer {
    private final Environment env;

    public ObservabilityTagsCustomizer(Environment env) {
        this.env = env;
    }

    public String getAppName() {
        String name = env.getProperty("spring.application.name");
        return StringUtils.hasText(name) ? name : "kraft-library";
    }
}
