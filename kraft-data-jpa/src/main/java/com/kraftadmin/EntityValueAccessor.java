package com.kraftadmin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.util.*;

import static com.kraftadmin.EntityUtil.convertValue;

public class EntityValueAccessor {
    private static final Logger log = LoggerFactory.getLogger(EntityValueAccessor.class);

    public static void setFieldValue(EntityType<?> clazz, Object target, String path, String value, EntityManager em) throws Exception {
        try {
            String[] parts = path.split("\\.");
            Object current = target;

            // Start with fields from the main entity
            List<Field> allFields = getAllFields(clazz.getJavaType());
            Map<String, Field> fieldMap = new HashMap<>();
            for (Field f : allFields) {
                f.setAccessible(true);
                fieldMap.put(f.getName(), f);
            }

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                Field field = fieldMap.get(part);
                if (field == null) {
//                    log.warn("Field '{}' not found in class {}", part, current.getClass().getSimpleName());
                    continue;
                }

                if (i == parts.length - 1) {
                    // This is the final field - set its value
                    Class<?> fieldType = field.getType();
//                    log.info("Setting field {} of type {} with value {} on object {}",
//                            field.getName(), fieldType.getSimpleName(), value, current.getClass().getSimpleName());

                    if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                        // Handle relationship fields
                        Class<?> relatedClass = fieldType;
                        Field idField = getIdField(relatedClass);
                        if (idField == null) {
//                            throw new IllegalArgumentException("No @Id field in " + relatedClass.getSimpleName());
                            continue;
                        }

                        Object id = convertValue(idField.getType(), value);
                        Object relatedEntity = em.find(relatedClass, id);

                        if (relatedEntity == null) {
                            relatedEntity = relatedClass.getDeclaredConstructor().newInstance();
                            idField.setAccessible(true);
                            idField.set(relatedEntity, id);
                            em.persist(relatedEntity);
//                            log.info("Auto-created {} with ID {}", relatedClass.getSimpleName(), id);
                        }

                        field.set(current, relatedEntity);
                    } else {
                        // Handle regular fields
                        Object convertedValue;
                        if (field.getName().contains("password")) {
                            convertedValue = new BCryptPasswordEncoder(6).encode(value);
//                            log.info("Password field {} encoded", field.getName());
                        } else {
                            convertedValue = convertValue(fieldType, value);
                        }
                        field.set(current, convertedValue);
                    }
                } else {
                    // This is a nested field - navigate to it
                    Object nested = field.get(current);
                    if (nested == null) {
                        // Create the nested object
                        nested = field.getType().getDeclaredConstructor().newInstance();
                        field.set(current, nested);
//                        log.info("Created nested object of type {} for field {}", field.getType().getSimpleName(), field.getName());
                    }

                    // Move to the nested object
                    current = nested;

                    // Update field map for the nested object's class
                    allFields = getAllFields(nested.getClass());
                    fieldMap.clear();
                    for (Field f : allFields) {
                        f.setAccessible(true);
                        fieldMap.put(f.getName(), f);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception setting field value for path: {}, value: {}, target: {}", path, value, target.getClass().getSimpleName(), e);
//            throw new RuntimeException(e);
        }
    }

    private static Field getIdField(Class<?> clazz) {
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null && type != Object.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }
}