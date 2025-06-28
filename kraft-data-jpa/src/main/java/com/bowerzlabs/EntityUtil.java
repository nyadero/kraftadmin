package com.bowerzlabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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

//public static Object convertValue(Class<?> type, String value) {
//    if (value == null || value.isBlank()) return null;
//
//    if (type.equals(String.class)) return value;
//    if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
//    if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
//    if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(value);
//    if (type.equals(Float.class) || type.equals(float.class)) return Float.valueOf(value);
//    if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(value);
//    if (type.equals(Short.class) || type.equals(short.class)) return Short.valueOf(value);
//    if (type.equals(Byte.class) || type.equals(byte.class)){
//        return value.getBytes();
//    }
//    if (type.equals(Character.class) || type.equals(char.class)) return value.charAt(0);
//    if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, value);
//    if (type.equals(java.time.LocalDate.class)) return java.time.LocalDate.parse(value);
//    if (type.equals(java.time.LocalDateTime.class)) return java.time.LocalDateTime.parse(value);
//    if (type.equals(java.time.LocalTime.class)) return java.time.LocalTime.parse(value);
//
//    throw new IllegalArgumentException("Unsupported type: " + type.getName());
//}

//public static Object convertValue(Class<?> type, Object value) {
//    if (value == null) return null;
//
//    String str = value.toString().trim();
//    if (str.isBlank()) return null;
//
//    if (type.equals(String.class)) return str;
//    if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(str);
//    if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(str);
//    if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(str);
//    if (type.equals(Float.class) || type.equals(float.class)) return Float.valueOf(str);
//    if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(str);
//    if (type.equals(Short.class) || type.equals(short.class)) return Short.valueOf(str);
//    if (type.equals(Byte.class) || type.equals(byte.class)) return Byte.valueOf(str);
//    if (type.equals(Character.class) || type.equals(char.class)) return str.charAt(0);
//
//    if (type.isEnum()) {
//        return Enum.valueOf((Class<Enum>) type, str);
//    }
//
//    if (type.equals(java.time.LocalDate.class)) return java.time.LocalDate.parse(str);
//    if (type.equals(java.time.LocalDateTime.class)) return java.time.LocalDateTime.parse(str);
//    if (type.equals(java.time.LocalTime.class)) return java.time.LocalTime.parse(str);
//
//    throw new IllegalArgumentException("Unsupported type: " + type.getName());
//}
@SuppressWarnings("unchecked")
public static Object convertValue(Class<?> type, Object value) {
    if (value == null) return null;

    // If it's already the correct type
    if (type.isInstance(value)) return value;

    // For primitives not covered by isInstance
    if (type.isPrimitive()) {
        if ((type == int.class && value instanceof Integer) ||
                (type == long.class && value instanceof Long) ||
                (type == double.class && value instanceof Double) ||
                (type == float.class && value instanceof Float) ||
                (type == boolean.class && value instanceof Boolean) ||
                (type == short.class && value instanceof Short) ||
                (type == byte.class && value instanceof Byte) ||
                (type == char.class && value instanceof Character)) {
            return value;
        }
    }

    String str = value.toString().trim();
    if (str.isBlank()) return null;

    // Common types
    if (type.equals(String.class)) return str;
    if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(str);
    if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(str);
    if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(str);
    if (type.equals(Float.class) || type.equals(float.class)) return Float.valueOf(str);
    if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(str);
    if (type.equals(Short.class) || type.equals(short.class)) return Short.valueOf(str);
    if (type.equals(Byte.class) || type.equals(byte.class)) return Byte.valueOf(str);
    if (type.equals(Character.class) || type.equals(char.class)) return str.charAt(0);

    // BigDecimal, BigInteger
    if (type.equals(BigDecimal.class)) return new BigDecimal(str);
    if (type.equals(BigInteger.class)) return new BigInteger(str);

    // UUID
    if (type.equals(UUID.class)) return UUID.fromString(str);

    // Date (legacy)
    if (type.equals(Date.class)) return java.sql.Timestamp.valueOf(str);

    // Java Time
    if (type.equals(LocalDate.class)) return LocalDate.parse(str);
    if (type.equals(LocalDateTime.class)) return LocalDateTime.parse(str);
    if (type.equals(LocalTime.class)) return LocalTime.parse(str);

    // Enums
    if (type.isEnum()) return Enum.valueOf((Class<Enum>) type, str);

    // Arrays (e.g., comma-separated)
    if (type.equals(String[].class)) return str.split(",");
    if (type.equals(Integer[].class))
        return Arrays.stream(str.split(",")).map(Integer::valueOf).toArray(Integer[]::new);
    if (type.equals(Long[].class))
        return Arrays.stream(str.split(",")).map(Long::valueOf).toArray(Long[]::new);
    if (type.equals(Double[].class))
        return Arrays.stream(str.split(",")).map(Double::valueOf).toArray(Double[]::new);

    // Lists
    if (type.equals(List.class) || type.equals(ArrayList.class)) {
        return Arrays.asList(str.split(","));
    }

    throw new IllegalArgumentException("Unsupported type: " + type.getName());
}

}
