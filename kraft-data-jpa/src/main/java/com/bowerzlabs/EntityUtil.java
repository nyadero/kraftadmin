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
