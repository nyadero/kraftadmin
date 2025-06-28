package com.bowerzlabs;

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

import static com.bowerzlabs.EntityUtil.convertValue;

public class EntityValueAccessor {
    private static final Logger log = LoggerFactory.getLogger(EntityValueAccessor.class);

    public static void setFieldValue(EntityType<?> clazz, Object target, String path, String value, EntityManager em) throws Exception {
        try{
        String[] parts = path.split("\\.");
        Object current = target;
        Class<?> currentClass = target.getClass();

        List<Field> allFields = getAllFields(clazz.getJavaType());
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field f : allFields) {
            f.setAccessible(true);
            fieldMap.put(f.getName(), f);
        }

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            Field field = fieldMap.get(part);
            if (field == null) continue;

            if (i == parts.length - 1) {
                Class<?> fieldType = field.getType();
                log.info("fieldType {} ", field.getName());

                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                    Class<?> relatedClass = fieldType;
                    Field idField = getIdField(relatedClass);
                    if (idField == null)
                        throw new IllegalArgumentException("No @Id field in " + relatedClass.getSimpleName());

                    Object id = convertValue(idField.getType(), value);
                    Object relatedEntity = em.find(relatedClass, id);

                    if (relatedEntity == null) {
                        relatedEntity = relatedClass.getDeclaredConstructor().newInstance();
                        idField.setAccessible(true);
                        idField.set(relatedEntity, id);
                        em.persist(relatedEntity);
                        log.info("Auto-created {} with ID {}", relatedClass.getSimpleName(), id);
                    }

                    field.set(target, relatedEntity);
                } else {
                    if (field.getName().contains("password")) {
                        field.set(target, new BCryptPasswordEncoder(6).encode(value));
                        log.info("fieldType is password {} {} {} {}", field.getName().contains("password"), value, new BCryptPasswordEncoder(6).encode(value), target);
                    }
                    Object convertedValue = convertValue(fieldType, value);
                    field.set(target, convertedValue);
                }
            } else {
                Object nested = field.get(current);
                if (nested == null) {
                    nested = field.getType().getDeclaredConstructor().newInstance();
                    field.set(current, nested);
                }
                current = nested;
                currentClass = nested.getClass();
                allFields = getAllFields(currentClass);
                fieldMap.clear();
                for (Field f : allFields) {
                    f.setAccessible(true);
                    fieldMap.put(f.getName(), f);
                }
            }
        }} catch (Exception e) {
            log.info("exception {}", (Object) e.getStackTrace());
            throw new RuntimeException(e);
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
