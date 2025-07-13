package com.bowerzlabs.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component("local")
@ConditionalOnProperty(prefix = "kraft.storage", name = "provider", havingValue = "local")
@RequiredArgsConstructor
@Slf4j
public class LocalStorageServiceProvider implements FileStorageProvider{

    private final StorageProperties storageProperties;


    @Override
    public String uploadFile(MultipartFile file) throws IOException {

        Path uploadDirectory = Paths.get(storageProperties.getUploadDir());

        // Create the upload directory if it does not exist
        Files.createDirectories(uploadDirectory);

        // Generate UUID-based filename
        String uuid = UUID.randomUUID().toString();
        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = uuid.concat(extension);
        Path uploadPath = uploadDirectory.resolve(fileName);

        
        // Save the file (replace if it already exists)
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Saved file: {}", uploadPath.toAbsolutePath());
        return "http://localhost:8080/admin/uploads/" +fileName;
    }

    @Override
    public Resource download(String filename) {
//        try {
//            Path dirPath = Paths.get(storageProperties.getUploadDir());
//            return new UrlResource(dirPath.resolve(filename).toUri());
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("File not found", e);
//        }
        return null;
    }

}
