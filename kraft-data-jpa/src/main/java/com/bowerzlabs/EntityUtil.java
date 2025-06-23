package com.bowerzlabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class EntityUtil {
    private static final Logger log = LoggerFactory.getLogger(EntityUtil.class);
//    public static Object convertValue(Class<?> fieldType, Object value) throws NoSuchFieldException, IllegalAccessException {
////        log.info("fieldType {}, value {}", fieldType, value);
//        List<String> enumValues;
//
//        if (value == null) {
//            return null;
//        }
//
//        if (fieldType == Integer.class || fieldType == int.class) {
//            return Integer.parseInt(value.toString().trim());
//        } else if (fieldType == Long.class || fieldType == long.class) {
//            return Long.parseLong(value.toString().trim());
//        } else if (fieldType == Double.class || fieldType == double.class) {
//            return Double.parseDouble(value.toString().trim());
//        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
//            return Boolean.parseBoolean(String.valueOf(value));
//        }
//        else if (fieldType == LocalDateTime.class) {
//            return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
//        }
//        else if (fieldType == LocalTime.class) {
//            // Support both 24hr and 12hr (AM/PM) formats
//            DateTimeFormatter formatter = value.toString().contains("am") || value.toString().toLowerCase().contains("pm")
//                    ? DateTimeFormatter.ofPattern("hh:mm a")
//                    : DateTimeFormatter.ofPattern("HH:mm");
//            return LocalTime.parse((CharSequence) value, formatter);
//        } else if (fieldType == LocalDate.class) {
//            if (value instanceof LocalDate) {
//                return value; // Already a LocalDate
//            } else if (value instanceof String) {
//                return LocalDate.parse((String) value, DateTimeFormatter.ISO_DATE); // Parse from String
//            }
//        } else if (fieldType == ZonedDateTime.class) {
//            return ZonedDateTime.parse(value.toString());
//        } else if (fieldType == OffsetDateTime.class) {
//            return OffsetDateTime.parse(value.toString());
//        } else if (fieldType == Date.class) {
//            return java.sql.Date.valueOf(value.toString()); // Convert to SQL Date
//        } else if (Collection.class.isAssignableFrom(fieldType)) {
//            // Handle List types (Assuming comma-separated values)
//            return Arrays.stream(value.toString().replaceAll("[\\[\\]]", "").split(","))
//                    .map(String::trim)
//                    .toList(); // Ensure it's a proper list
//        } else if (fieldType.isEnum()) {
//            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) fieldType;
//            enumValues = Arrays.stream(enumClass.getEnumConstants())
//                    .map(Enum::name)
//                    .toList();
//            return enumClass.getField((String) value).get(value);
//        }
//
//        return value.toString().trim(); // Default: String
//    }

public static Object convertValue(Class<?> type, String value) {
    if (value == null || value.isBlank()) return null;

    if (type.equals(String.class)) return value;
    if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
    if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
    if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(value);
    if (type.equals(Float.class) || type.equals(float.class)) return Float.valueOf(value);
    if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(value);
    if (type.equals(Short.class) || type.equals(short.class)) return Short.valueOf(value);
    if (type.equals(Byte.class) || type.equals(byte.class)) return Byte.valueOf(value);
    if (type.equals(Character.class) || type.equals(char.class)) return value.charAt(0);
    if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, value);
    if (type.equals(java.time.LocalDate.class)) return java.time.LocalDate.parse(value);
    if (type.equals(java.time.LocalDateTime.class)) return java.time.LocalDateTime.parse(value);
    if (type.equals(java.time.LocalTime.class)) return java.time.LocalTime.parse(value);

    throw new IllegalArgumentException("Unsupported type: " + type.getName());
}

}
