package com.kraftadmin.utils;

import com.kraftadmin.config.KraftProperties;
import jakarta.persistence.metamodel.EntityType;
import kotlin.Metadata;

/**
 * Collection of utility functions used across the library
 */
public class KraftUtils {
    public static final String BASE_URL = new KraftProperties().getBaseUrl();

    /**
     * Converts snake case to camel case
     *
     * @param text
     * @return
     */
    public static String snakeToCamel(String text) {
        boolean shouldConvertNextCharToLower = true;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            if (currentChar == '_') {
                shouldConvertNextCharToLower = false;
            } else if (shouldConvertNextCharToLower) {
                builder.append(Character.toLowerCase(currentChar));
            } else {
                builder.append(Character.toUpperCase(currentChar));
                shouldConvertNextCharToLower = true;
            }
        }
        return builder.toString();
    }

    /**
     * Converts camel case to snake case
     *
     * @param v
     * @return
     */
    public static String camelToSnake(String v) {
        if (Character.isUpperCase(v.charAt(0))) {
            v = Character.toLowerCase(v.charAt(0)) + v.substring(1);
        }

        return v.replaceAll("([A-Z][a-z])", "_$1").toLowerCase();

    }

    /**
     * Format a field name into a human-readable label
     */
    public static String formatLabel(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return "";
        }

        // Replace dots and underscores with spaces first
        String result = fieldName.replaceAll("[._]", " ");

        // Insert spaces before uppercase letters (handling camelCase nicely)
        result = result.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Normalize spaces
        result = result.replaceAll("\\s+", " ").trim();

        // Capitalize first letter
        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }

        // capitalize first letter after space
//        if (!result.isEmpty()){
//            result = result.substring(0, fieldName.length()-1).toUpperCase();
//        }
        return result;
    }

    public static String unformatLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return "";
        }

        // Lowercase the label and split by space
        String[] parts = label.trim().split("\\s+");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());

        // Reconstruct camelCase
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }


    //     Detect Kotlin-Compiled Entities
    public static boolean isKotlinClass(EntityType<?> clazz) {
        return clazz.getJavaType().getAnnotation(Metadata.class) != null;
    }

}
