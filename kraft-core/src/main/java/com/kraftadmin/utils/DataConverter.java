package com.kraftadmin.utils;

import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class DataConverter {
    /**
     * format field displayField for display
     */
    private static String formatForDisplay(Class<?> fieldType, Object value) {
        if (value == null) return "";

        if (fieldType == LocalDateTime.class) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
        } else if (fieldType == LocalTime.class) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (fieldType == LocalDate.class) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (fieldType == ZonedDateTime.class) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z"));
        } else if (fieldType == OffsetDateTime.class) {
            OffsetDateTime odt = (OffsetDateTime) value;
            return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a O"));
        } else if (fieldType == Date.class) {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format((Date) value);
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return String.join(", ", ((Collection<?>) value).stream().map(Object::toString).toList());
        } else if (fieldType.isEnum()) {
            return value.toString();
        }

        return value.toString(); // Default fallback
    }


    /**
     * id is passed as a string, so we need to parse it to field's class
     */
    private static Object convertId(String id, Class<?> entityClass) throws NoSuchFieldException {
        Field idField = findPrimaryKeyField(entityClass);
        if (idField == null) {
            throw new NoSuchFieldException("No field annotated with @Id found in class: " + entityClass.getName());
        }

        Class<?> idType = idField.getType();

        if (idType == String.class) return id;
        if (idType == Long.class || idType == long.class) return Long.parseLong(id);
        if (idType == Integer.class || idType == int.class) return Integer.parseInt(id);
        if (idType == UUID.class) return UUID.fromString(id);

        throw new IllegalArgumentException("Unsupported ID type: " + idType.getName());
    }


    /**
     * find the primary id by checking Id annotation on JPA entities
     */
    private static Field findPrimaryKeyField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            // find both jpa and mongo id
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null; // No primary key found
    }
}
