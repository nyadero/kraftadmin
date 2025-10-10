package com.kraftadmin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MongoPrimaryDBEnabled implements Condition {
    private final Logger logger = LoggerFactory.getLogger(MongoPrimaryDBEnabled.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String primaryDb = context.getEnvironment().getProperty("kraft.kraft-admin.primaryDB", "MONGO");
        boolean enabled = "MONGO".equals(primaryDb);
        if (enabled) {
            logger.info("primary db set to {}", primaryDb);
            blockEntities();
        }
        return enabled;
    }

    // block scanning of library mongo documents
//    @EnableMongoRepositories(
//            basePackages = "com.kraftadmin.repos.mongo", // separate package
//            mongoTemplateRef = "kraftMongoTemplate"
//    )
    public String blockEntities() {
        System.out.println("block entities scanning");

//        RepositoryConfigurationDelegate repositoryConfigurationDelegate = new RepositoryConfigurationDelegate(null, null, null);
//        repositoryConfigurationDelegate.registerRepositoriesIn(null, null);
//        entityScanner.scan();
//        System.out.println(entityScanner.scan());
        return "";
    }


}


/// / 1. Library Auto-Configuration Entry Point
//@Configuration
//@ConditionalOnProperty(name = "kraft.kraft-admin.enabled", havingValue = "true", matchIfMissing = true)
//@EnableConfigurationProperties(KraftAdminProperties.class)
//@Slf4j
//public class KraftAdminAutoConfiguration {
//
//    @PostConstruct
//    public void init() {
//        log.info("Kraft Admin Library initializing...");
//    }
//}
//
//// 2. Library Properties Configuration
//@ConfigurationProperties(prefix = "kraft.kraft-admin")
//@Data
//public class KraftAdminProperties {
//
//    private boolean enabled = true;
//    private String primaryDB = "MONGO";  // Default to MONGO
//    private String title = "Kraft Admin";
//    private Storage storage = new Storage();
//
//    @Data
//    public static class Storage {
//        private String provider = "LOCAL";
//        private String uploadDir = "uploads";
//    }
//}
//
//// 3. JPA Configuration (Only when primaryDB = JPA)
//@Configuration
//@ConditionalOnProperty(name = "kraft.kraft-admin.primaryDB", havingValue = "JPA")
//@EntityScan(basePackages = {
//        "com.kraftadmin.entities",     // Your library JPA entities
//        "com.kraftadmin.models.jpa"    // Alternative JPA models location
//})
//@EnableJpaRepositories(basePackages = {
//        "com.kraftadmin.repos.jpa"     // Your library JPA repositories
//})
//@Slf4j
//public class KraftAdminJPAConfiguration {
//
//    private final KraftAdminProperties properties;
//
//    public KraftAdminJPAConfiguration(KraftAdminProperties properties) {
//        this.properties = properties;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("=== KRAFT ADMIN - JPA MODE ACTIVATED ===");
//        log.info("Library will use JPA entities for admin users, logs, etc.");
//        log.info("MongoDB documents are DISABLED");
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public KraftAdminUserService kraftAdminUserService() {
//        return new KraftAdminJPAUserService(); // JPA implementation
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public KraftAdminLogService kraftAdminLogService() {
//        return new KraftAdminJPALogService(); // JPA implementation
//    }
//}
//
//// 4. MongoDB Configuration (Only when primaryDB = MONGO)
//@Configuration
//@ConditionalOnProperty(name = "kraft.kraft-admin.primaryDB", havingValue = "MONGO", matchIfMissing = true)
//@EnableMongoRepositories(basePackages = {
//        "com.kraftadmin.repos.mongo"   // Your library MongoDB repositories
//})
//@Slf4j
//public class KraftAdminMongoConfiguration {
//
//    private final KraftAdminProperties properties;
//
//    public KraftAdminMongoConfiguration(KraftAdminProperties properties) {
//        this.properties = properties;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("=== KRAFT ADMIN - MONGODB MODE ACTIVATED ===");
//        log.info("Library will use MongoDB documents for admin users, logs, etc.");
//        log.info("JPA entities are DISABLED");
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public KraftAdminUserService kraftAdminUserService() {
//        return new KraftAdminMongoUserService(); // MongoDB implementation
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public KraftAdminLogService kraftAdminLogService() {
//        return new KraftAdminMongoLogService(); // MongoDB implementation
//    }
//}
//
//// 5. Library Service Interfaces (Database-agnostic)
//public interface KraftAdminUserService {
//    void createAdminUser(String username, String email, String password);
//    List<AdminUser> getAllAdminUsers();
//    Optional<AdminUser> findAdminUser(String username);
//    void deleteAdminUser(String username);
//}
//
//public interface KraftAdminLogService {
//    void logAdminAction(String username, String action, String details);
//    List<AdminLog> getAdminLogs(String username, LocalDateTime from, LocalDateTime to);
//    void cleanupOldLogs(int daysToKeep);
//}
//
//// 6. JPA Entity Examples (com.kraftadmin.entities package)
//@Entity
//@Table(name = "kraft_admin_users")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AdminUserEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String email;
//
//    @Column(nullable = false)
//    private String passwordHash;
//
//    @CreationTimestamp
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    private LocalDateTime updatedAt;
//
//    private boolean active = true;
//}
//
//@Entity
//@Table(name = "kraft_admin_logs")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AdminLogEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String username;
//
//    @Column(nullable = false)
//    private String action;
//
//    @Column(length = 1000)
//    private String details;
//
//    @CreationTimestamp
//    private LocalDateTime timestamp;
//}
//
//// 7. MongoDB Document Examples (com.kraftadmin.documents package)
//@Document(collection = "kraft_admin_users")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AdminUserDocument {
//
//    @Id
//    private String id;
//
//    @Indexed(unique = true)
//    private String username;
//
//    private String email;
//    private String passwordHash;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private boolean active = true;
//}
//
//@Document(collection = "kraft_admin_logs")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class AdminLogDocument {
//
//    @Id
//    private String id;
//
//    private String username;
//    private String action;
//    private String details;
//    private LocalDateTime timestamp;
//}
//
//// 8. JPA Repository Examples
//@Repository
//public interface AdminUserJPARepository extends JpaRepository<AdminUserEntity, Long> {
//    Optional<AdminUserEntity> findByUsername(String username);
//    List<AdminUserEntity> findByActiveTrue();
//}
//
//@Repository
//public interface AdminLogJPARepository extends JpaRepository<AdminLogEntity, Long> {
//    List<AdminLogEntity> findByUsernameAndTimestampBetween(String username, LocalDateTime from, LocalDateTime to);
//    void deleteByTimestampBefore(LocalDateTime cutoff);
//}
//
//// 9. MongoDB Repository Examples
//@Repository
//public interface AdminUserMongoRepository extends MongoRepository<AdminUserDocument, String> {
//    Optional<AdminUserDocument> findByUsername(String username);
//    List<AdminUserDocument> findByActiveTrue();
//}
//
//@Repository
//public interface AdminLogMongoRepository extends MongoRepository<AdminLogDocument, String> {
//    List<AdminLogDocument> findByUsernameAndTimestampBetween(String username, LocalDateTime from, LocalDateTime to);
//    void deleteByTimestampBefore(LocalDateTime cutoff);
//}
//
//// 10. JPA Service Implementations
//@Service
//public class KraftAdminJPAUserService implements KraftAdminUserService {
//
//    private final AdminUserJPARepository repository;
//
//    public KraftAdminJPAUserService(AdminUserJPARepository repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public void createAdminUser(String username, String email, String password) {
//        AdminUserEntity user = new AdminUserEntity();
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPasswordHash(hashPassword(password));
//        repository.save(user);
//    }
//
//    @Override
//    public List<AdminUser> getAllAdminUsers() {
//        return repository.findByActiveTrue().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<AdminUser> findAdminUser(String username) {
//        return repository.findByUsername(username)
//                .map(this::convertToDTO);
//    }
//
//    @Override
//    public void deleteAdminUser(String username) {
//        repository.findByUsername(username)
//                .ifPresent(user -> {
//                    user.setActive(false);
//                    repository.save(user);
//                });
//    }
//
//    private AdminUser convertToDTO(AdminUserEntity entity) {
//        return new AdminUser(entity.getUsername(), entity.getEmail(),
//                entity.getCreatedAt(), entity.isActive());
//    }
//
//    private String hashPassword(String password) {
//        // Implement password hashing
//        return password; // Placeholder
//    }
//}
//
//// 11. MongoDB Service Implementations
//@Service
//public class KraftAdminMongoUserService implements KraftAdminUserService {
//
//    private final AdminUserMongoRepository repository;
//
//    public KraftAdminMongoUserService(AdminUserMongoRepository repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public void createAdminUser(String username, String email, String password) {
//        AdminUserDocument user = new AdminUserDocument();
//        user.setUsername(username);
//        user.setEmail(email);
//        user.setPasswordHash(hashPassword(password));
//        user.setCreatedAt(LocalDateTime.now());
//        repository.save(user);
//    }
//
//    @Override
//    public List<AdminUser> getAllAdminUsers() {
//        return repository.findByActiveTrue().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<AdminUser> findAdminUser(String username) {
//        return repository.findByUsername(username)
//                .map(this::convertToDTO);
//    }
//
//    @Override
//    public void deleteAdminUser(String username) {
//        repository.findByUsername(username)
//                .ifPresent(user -> {
//                    user.setActive(false);
//                    repository.save(user);
//                });
//    }
//
//    private AdminUser convertToDTO(AdminUserDocument document) {
//        return new AdminUser(document.getUsername(), document.getEmail(),
//                document.getCreatedAt(), document.isActive());
//    }
//
//    private String hashPassword(String password) {
//        // Implement password hashing
//        return password; // Placeholder
//    }
//}
//
//// 12. Common DTO (Database-agnostic)
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class AdminUser {
//    private String username;
//    private String email;
//    private LocalDateTime createdAt;
//    private boolean active;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class AdminLog {
//    private String username;
//    private String action;
//    private String details;
//    private LocalDateTime timestamp;
//}

//# META-INF/spring.factories
//# This file should be placed in src/main/resources/META-INF/ of your library
//
//org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
//com.kraftadmin.config.KraftAdminAutoConfiguration

//kraft-admin-library/
//        ├── src/main/java/com/kraftadmin/
//        │   ├── config/
//        │   │   ├── KraftAdminAutoConfiguration.java
//│   │   ├── KraftAdminJPAConfiguration.java
//│   │   ├── KraftAdminMongoConfiguration.java
//│   │   └── KraftAdminProperties.java
//│   ├── entities/                     # JPA Entities (only loaded when primaryDB=JPA)
//│   │   ├── AdminUserEntity.java
//│   │   └── AdminLogEntity.java
//│   ├── documents/                    # MongoDB Documents (only loaded when primaryDB=MONGO)
//│   │   ├── AdminUserDocument.java
//│   │   └── AdminLogDocument.java
//│   ├── repos/
//        │   │   ├── jpa/                     # JPA Repositories
//│   │   │   ├── AdminUserJPARepository.java
//│   │   │   └── AdminLogJPARepository.java
//│   │   └── mongo/                   # MongoDB Repositories
//│   │       ├── AdminUserMongoRepository.java
//│   │       └── AdminLogMongoRepository.java
//│   ├── services/                    # Database-agnostic interfaces & implementations
//│   │   ├── KraftAdminUserService.java
//│   │   ├── KraftAdminLogService.java
//│   │   ├── impl/
//        │   │   │   ├── KraftAdminJPAUserService.java
//│   │   │   ├── KraftAdminMongoUserService.java
//│   │   │   └── ...
//        │   └── dto/                         # Common DTOs
//│       ├── AdminUser.java
//│       └── AdminLog.java
//└── src/main/resources/
//        └── META-INF/
//        └── spring.factories

