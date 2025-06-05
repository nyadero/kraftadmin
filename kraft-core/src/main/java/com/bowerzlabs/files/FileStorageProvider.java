package com.bowerzlabs.files;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageProvider {
    String uploadFile(MultipartFile file) throws IOException;
    Resource download(String filename);
}
