package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class KraftrStarter {
    private static final Logger log = LoggerFactory.getLogger(KraftrStarter.class);

    @PostConstruct
    public void init() {
        log.info("🔧 Kraftr starter Initialized");
    }
}
