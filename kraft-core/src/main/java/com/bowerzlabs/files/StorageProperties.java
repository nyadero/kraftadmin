package com.bowerzlabs.files;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kraft.storage")
public class StorageProperties {
    private String provider;
    private String uploadDir;
}

