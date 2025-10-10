package com.kraftadmin;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;

//public class MongoDocumentMetaModel {
//    private static final Logger log = LoggerFactory.getLogger(MongoDocumentMetaModel.class);
//    private final Class<?> entityClass;
//    private final String entityName;
//    private final List<Class<?>> subTypes;
//    private final boolean superClass;
//
//    public MongoDocumentMetaModel(Class<?> entityClass, List<Class<?>> subTypes) {
//        this.entityClass = entityClass;
//        this.entityName = entityClass.getSimpleName();
//        this.subTypes = subTypes;
//        this.superClass = true;
//    }
//
//    public Field getIdField() {
//        for (Field field : entityClass.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Id.class)) {
//                field.setAccessible(true);
//                return field;
//            }
//        }
//        log.info("No @Id field in {}", entityClass.getSimpleName());
//        return null;
//    }
//
//    public Field getCreationDateField() {
//        for (Field field : entityClass.getDeclaredFields()) {
//            if (field.isAnnotationPresent(CreatedDate.class)) {
//                field.setAccessible(true);
//                return field;
//            }
//        }
//        throw new RuntimeException("No @CreatedDate field in " + entityClass.getSimpleName());
//    }
//
//    public Object convertId(String idString) {
//        try {
//            Field idField = getIdField();
//            if (idField == null) return null;
//            Class<?> type = idField.getType();
//            if (type.equals(String.class)) return idString;
//            if (type.equals(org.bson.types.ObjectId.class)) return new org.bson.types.ObjectId(idString);
//            // add more conversions if needed
//            return idString;
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to convert id", e);
//        }
//    }
//
//    public Class<?> getEntityClass() {
//        return entityClass;
//    }
//
//    public List<Class<?>> getSubTypes() {
//        return subTypes;
//    }
//
//    public String getEntityName() {
//        return entityName;
//    }
//
//    public boolean isSuperClass() {
//        return superClass;
//    }
//}

public class MongoDocumentMetaModel {
    private final Class<?> entityClass;
    private final Document documentAnnotation;
    private final String entityName;

    public MongoDocumentMetaModel(Class<?> entityClass, Document documentAnnotation) {
        this.entityClass = entityClass;
        this.documentAnnotation = documentAnnotation;
        this.entityName = entityClass.getSimpleName();
    }

    public Field getIdField() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    public Field getCreationDateField() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(CreatedDate.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    public boolean matchesName(String name) {
        String collectionName = (documentAnnotation != null) ? documentAnnotation.collection() : null;
        return name.equalsIgnoreCase(entityName) ||
                (name.equalsIgnoreCase(collectionName));
    }

    public String getEntityName() {
        return entityName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
