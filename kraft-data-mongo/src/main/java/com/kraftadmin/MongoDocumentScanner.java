package com.kraftadmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.atteo.evo.inflector.English.plural;


@Component
public class MongoDocumentScanner {
    private static final Logger log = LoggerFactory.getLogger(MongoDocumentScanner.class);

    private final List<DocumentMetaModel> cachedDocuments;
    private final Set<Class<?>> allDocuments;

    public MongoDocumentScanner(ApplicationContext applicationContext) {
        this.allDocuments = scanForDocuments(applicationContext);

        this.cachedDocuments = allDocuments.stream()
                .sorted(Comparator.comparing(Class::getSimpleName))
                .filter(this::isRootDocument) // Filter out documents that extend other documents
//                .filter(docClass -> !docClass.isAnnotationPresent(InternalAdminResource.class))
                .map(docClass -> new DocumentMetaModel(docClass, getAllSubTypesOf(docClass)))
                .collect(Collectors.toList());
    }

    /**
     * Manual document name resolution that checks @Document annotation directly
     */
    public static String resolveDocumentNameManual(Class<?> clazz) {
        try {
            if (clazz.isAnnotationPresent(Document.class)) {
                Document documentAnnotation = clazz.getAnnotation(Document.class);
                String collection = documentAnnotation.collection();

                if (collection != null && !collection.trim().isEmpty()) {
//                    log.debug("Found @Document(collection='{}') for class {}", collection, clazz.getSimpleName());
                    return collection;
                }
            }

//            log.debug("Using class simple name for {}", clazz.getSimpleName());
            return clazz.getSimpleName();

        } catch (Exception e) {
            log.error("Error resolving document name for {}, falling back to simple name", clazz.getSimpleName(), e);
            return clazz.getSimpleName();
        }
    }

    /**
     * Scan classpath for classes annotated with @Document
     */
    private Set<Class<?>> scanForDocuments(ApplicationContext applicationContext) {
        Set<Class<?>> documents = new HashSet<>();

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));

        // Automatically detect base packages from Spring Boot application
        List<String> basePackages = AutoConfigurationPackages.get(applicationContext);

        for (String basePackage : basePackages) {
            try {
                for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    if (clazz.isAnnotationPresent(Document.class)) {
                        documents.add(clazz);
                        log.debug("Found document class: {}", clazz.getSimpleName());
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("Error loading document class from package {}", basePackage, e);
            }
        }

        log.info("Scanned packages: {} and found {} document classes", basePackages, documents);
        return documents;
    }

    /**
     * Check if a document class is a root document (doesn't extend another document)
     */
    private boolean isRootDocument(Class<?> docClass) {
        Class<?> superClass = docClass.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            if (superClass.isAnnotationPresent(Document.class)) {
                return false; // This class extends another document
            }
            superClass = superClass.getSuperclass();
        }
        return true;
    }

    /**
     * Return all documents declared in the parent application
     */
    public List<DocumentMetaModel> getAllDocumentClasses() {
        return cachedDocuments;
    }

    public DocumentMetaModel getDocumentFromAllByName(String name) {
        return allDocuments.stream()
                .map(docClass -> new DocumentMetaModel(docClass, getAllSubTypesOf(docClass)))
                .filter(docModel -> {
                    Class<?> javaType = docModel.getDocumentClass();
                    String documentName = javaType.getSimpleName();
                    String pluralized = plural(documentName);

                    // Direct match with name or plural
                    if (name.equalsIgnoreCase(documentName) || name.equalsIgnoreCase(pluralized)) {
                        return true;
                    }

                    // Match superclass name (e.g., search "User" and match "AdminUserDocument" that extends User)
                    Class<?> superClass = javaType.getSuperclass();
                    while (superClass != null && superClass != Object.class) {
                        String superName = superClass.getSimpleName();
                        if (name.equalsIgnoreCase(superName) || name.equalsIgnoreCase(plural(superName))) {
                            return true;
                        }
                        superClass = superClass.getSuperclass();
                    }

                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    public List<Class<?>> getAllSubTypesOf(Class<?> baseType) {
        return allDocuments.stream()
                .filter(docClass -> baseType.isAssignableFrom(docClass)) // includes subclasses
                .filter(docClass -> !docClass.equals(baseType)) // exclude the base class itself
                .filter(docClass -> !Modifier.isAbstract(docClass.getModifiers())) // exclude abstract classes
                .toList();
    }

    /**
     * Find a DocumentMetaModel based on the name param passed
     */
    public DocumentMetaModel getDocumentByName(String name) {

        // First, let's see what documents we have
        cachedDocuments.forEach(docMetaModel -> {
            Class<?> javaType = docMetaModel.getDocumentClass();
            log.info("mongo java type {}", javaType);
        });

        DocumentMetaModel result = cachedDocuments.stream()
                .filter(docMetaModel -> {
                    Class<?> javaType = docMetaModel.getDocumentClass();
                    String collectionName = resolveDocumentNameManual(javaType);
                    String simpleClassName = javaType.getSimpleName();

                    log.info("Comparing '{}' with collection name '{}' and simple class '{}'",
                            name, collectionName, simpleClassName);

                    boolean matches = name.equalsIgnoreCase(collectionName) ||
                            name.equalsIgnoreCase(simpleClassName);

                    return matches;
                })
                .findFirst()
                .orElse(null);

//        if (result == null) {
//            log.error("No document found with name: {}", name);
//        }

        return result;
    }
}