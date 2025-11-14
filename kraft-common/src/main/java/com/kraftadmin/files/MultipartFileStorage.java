package com.kraftadmin.files;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MultipartFileStorage {
    List<String> uploadMultiple(List<MultipartFile> files);

    String uploadSingle(MultipartFile file);

    Resource download(String filename);
}
