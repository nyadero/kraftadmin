package com.bowerzlabs.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component("local")
@ConditionalOnProperty(prefix = "kraft.storage", name = "provider", havingValue = "s3")
@RequiredArgsConstructor
@Slf4j
public class S3StorageServiceProvider {
    private final StorageProperties storageProperties;

}
