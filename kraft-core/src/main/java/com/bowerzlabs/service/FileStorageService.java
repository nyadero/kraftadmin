package com.bowerzlabs.service;

import com.bowerzlabs.files.FileStorageProvider;
import com.bowerzlabs.files.LocalStorageServiceProvider;
import com.bowerzlabs.files.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Slf4j
public class FileStorageService{
    FileStorageProvider fileStorageProvider;
    private StorageProperties storageProperties;

    public FileStorageService() {
        fileStorageProvider = new LocalStorageServiceProvider(null);
    }

    public String upload(MultipartFile file) throws IOException {
        log.info("Uploading file {}", file);
        return fileStorageProvider.uploadFile(file);
    }


    public Resource download(String filename) {
        log.info("Downloading file {}", filename);
        return fileStorageProvider.download(filename);
    }

}
