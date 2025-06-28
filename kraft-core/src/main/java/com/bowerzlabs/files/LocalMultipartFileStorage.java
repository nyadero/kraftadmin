package com.bowerzlabs.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class LocalMultipartFileStorage implements MultipartFileStorage {
    private final static Logger log = LoggerFactory.getLogger(LocalMultipartFileStorage.class);
    
    private final StorageProperties storageProperties;

    public LocalMultipartFileStorage(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
        log.info("Local Multipart Storage initialized {} {}", storageProperties.getProvider(), storageProperties.getUploadDir());
    }

    @Override
    public List<String> uploadMultiple(List<MultipartFile> files) {
        return List.of();
    }

    @Override
    public String uploadSingle(MultipartFile file) {
        return "";
    }

    @Override
    public Resource download(String filename) {
        return null;
    }

}
