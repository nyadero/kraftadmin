package com.bowerzlabs.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class LocalMultipartFileStorage implements MultipartFileStorage {
    private final StorageProperties storageProperties;

    public LocalMultipartFileStorage(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
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
