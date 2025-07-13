package com.bowerzlabs.files;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LocalMultipartFileStorage implements MultipartFileStorage {
    private final static Logger log = LoggerFactory.getLogger(LocalMultipartFileStorage.class);
    private final HttpServletRequest httpServletRequest;
    private final StorageProperties storageProperties;

    @Override
    public List<String> uploadMultiple(List<MultipartFile> files) {
        return List.of();
    }

    public static String getApplicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @Override
    public Resource download(String filename) {
        return null;
    }

    @Override
    @Async
    public String uploadSingle(MultipartFile multipartFile) {

        // Use the configured upload directory
        Path uploadDirectory = Paths.get(storageProperties.getUploadDir());

        // Create the upload directory if it doesn't exist
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            log.error("Error creating upload directory: " + e);
            return null;
        }
        // generate random uuid as string for file name
        String randomUuid = UUID.randomUUID().toString();
        // extract extension name
        String fileExtension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        // concat random uuid and extension
        String uploadedFile = randomUuid + fileExtension;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadDirectory.resolve(uploadedFile);
            // Compress the image before saving
//            Thumbnails.of(inputStream)
//                    .size(800, 600) // Adjust the dimensions as needed
//                    .outputQuality(1.0) // Adjust the quality (0.0 to 1.0) to retain quality
//                    .toFile(filePath.toFile());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.info("Error saving your file " + e);
        }
//
        return getApplicationUrl(httpServletRequest) + "/" + storageProperties.getUploadDir() + "/" + uploadedFile;
    }

}
