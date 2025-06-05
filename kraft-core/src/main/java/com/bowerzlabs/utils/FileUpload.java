//package com.bowerzlabs.utils;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//public class FileUpload {
//
//}
//
//enum FileServiceProvider{
//    Local, Cloudinary, AWS_S3
//}
//
//public interface FileStorageService {
//    String uploadFile(MultipartFile file) throws IOException;
//}
//
//// --- LocalFileStorageService ---
//@Service
//@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "local")
//@RequiredArgsConstructor
//public class LocalFileStorageService implements FileStorageService {
//
//    @Value("${app.local.upload-dir}")
//    private String uploadDir;
//
//    @Override
//    public String uploadFile(MultipartFile file) throws IOException {
//        Path dirPath = Paths.get(uploadDir);
//        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);
//
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Path filePath = dirPath.resolve(fileName);
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        return filePath.toString();
//    }
//}
//
//// --- S3FileStorageService ---
//@Service
//@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "s3")
//@RequiredArgsConstructor
//public class S3FileStorageService implements FileStorageService {
//
//    @Value("${app.aws.bucket-name}")
//    private String bucketName;
//
//    private final S3Client s3Client;
//
//    @Override
//    public String uploadFile(MultipartFile file) throws IOException {
//        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .contentType(file.getContentType())
//                .build();
//
//        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//        return key;
//    }
//}
//
//// --- S3 Configuration ---
//@Configuration
//@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "s3")
//public class S3Config {
//
//    @Value("${app.aws.access-key}")
//    private String accessKey;
//
//    @Value("${app.aws.secret-key}")
//    private String secretKey;
//
//    @Value("${app.aws.region}")
//    private String region;
//
//    @Bean
//    public S3Client s3Client() {
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//        return S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .region(Region.of(region))
//                .build();
//    }
//}
//
//// --- UploadController ---
//@RestController
//@RequestMapping("/api/files")
//@RequiredArgsConstructor
//public class UploadController {
//
//    private final FileStorageService fileStorageService;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
//        try {
//            String location = fileStorageService.uploadFile(file);
//            return ResponseEntity.ok("File uploaded to: " + location);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }
//}

//# application.yml (of consuming app)
//storage:
//provider: s3 # or 'local'
//local:
//path: ./uploads
//s3:
//access-key: YOUR_AWS_ACCESS_KEY
//secret-key: YOUR_AWS_SECRET
//region: us-east-1
//bucket: your-bucket-name

//@Component
//@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
//public class LocalStorageService implements FileStorageService {
//
//    private final Path root;
//
//    public LocalStorageService(StorageProperties props) throws IOException {
//        this.root = Paths.get(props.getLocal().getPath());
//        Files.createDirectories(root);
//    }
//
//    @Override
//    public String upload(MultipartFile file) throws IOException {
//        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Files.copy(file.getInputStream(), root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
//        return filename;
//    }
//
//    @Override
//    public Resource download(String filename) {
//        try {
//            return new UrlResource(root.resolve(filename).toUri());
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("File not found", e);
//        }
//    }
//}

//public interface FileStorageService {
//    String upload(MultipartFile file) throws IOException;
//    Resource download(String filename);
//}


//@Getter
//@Setter
//@ConfigurationProperties(prefix = "storage")
//public class StorageProperties {
//    private String provider;
//    private Local local = new Local();
//
//    @Getter
//    @Setter
//    public static class Local {
//        private String path;
//    }
//}


//@Slf4j
//@Service
//@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
//public class LocalStorageService implements FileStorageService {
//
//    private final Path root;
//
//    public LocalStorageService(StorageProperties props) throws IOException {
//        this.root = Paths.get(props.getLocal().getPath());
//        Files.createDirectories(root);
//    }
//
//    @Override
//    public String upload(MultipartFile file) throws IOException {
//        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Path destination = root.resolve(filename);
//        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//        log.info("Saved file to: {}", destination);
//        return filename;
//    }
//
//    @Override
//    public Resource download(String filename) {
//        try {
//            Path file = root.resolve(filename);
//            return new UrlResource(file.toUri());
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("File not found: " + filename, e);
//        }
//    }
//}
