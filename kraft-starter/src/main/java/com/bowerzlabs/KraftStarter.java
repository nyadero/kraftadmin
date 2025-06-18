package com.bowerzlabs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Configuration
public class KraftStarter {
    private static final Logger log = LoggerFactory.getLogger(KraftStarter.class);

    @PostConstruct
    public void init() {
        log.info("Kraft starter Initialized");
    }
}
