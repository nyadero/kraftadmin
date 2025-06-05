package com.bowerzlabs;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.EmbeddedField;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.FormFieldFactory;
import com.bowerzlabs.formfields.SearchableSelectField;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputGenerator {
    private static final Map<Class<?>, List<FormField>> FORM_FIELD_CACHE = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InputGenerator.class);

    public static List<FormField> getFields(Class<?> entityClass, boolean isCreating, boolean isSearching, DbObjectSchema dbObjectSchema) {
        FORM_FIELD_CACHE.remove(entityClass); // Force refresh for dev
        return FORM_FIELD_CACHE.computeIfAbsent(entityClass, clazz ->
                extractFields(clazz, isCreating, isSearching, dbObjectSchema, "")
        );
    }

    /**
     * Extract fields from a class, including embedded fields recursively
     *
     * @param clazz         The class to extract from
     * @param isCreating    Whether it's a creation action or not
     * @param dbObjectSchema The data with values
     * @param prefix        Used for embedded fields like 'address.city'
     * @return list of FormFields
     */
    private static List<FormField> extractFields(Class<?> clazz, boolean isCreating, boolean isSearch, DbObjectSchema dbObjectSchema, String prefix) {
        List<FormField> formFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isSynthetic() || shouldExcludeField(field, isCreating)) continue;

            String fieldName = (prefix.isEmpty() ? "" : prefix + ".") + field.getName();
            String inputName = isSearch ? "filter." + fieldName : fieldName;

            FormField formField = FormFieldFactory.createFormFieldsFromEntity(dbObjectSchema, field, inputName, isSearch);
            formFields.add(formField);
        }

        log.info("formfields {}", formFields);
        return formFields;
    }

    // generate label fields
    private static String generateLabel(String prefix, String fieldName) {
        if (prefix == null || prefix.isEmpty()) {
            return capitalize(fieldName);
        }

        // Replace '.' with ' ' and capitalize each part
        String[] parts = (prefix + fieldName).split("\\.");
        StringBuilder label = new StringBuilder();
        for (String part : parts) {
            label.append(capitalize(part)).append(" ");
        }
        return label.toString().trim();
    }

    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }


    /**
     * Whether to exclude a field from rendering.
     */
    static boolean shouldExcludeField(Field field, boolean isCreating) {
        if (field.isAnnotationPresent(Id.class)) return true;

        // Only skip collections
        if (field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(ManyToMany.class)) return true;

        if (field.isAnnotationPresent(CreationTimestamp.class) ||
                field.isAnnotationPresent(CreatedDate.class) ||
                field.isAnnotationPresent(UpdateTimestamp.class)) return true;

        if (field.isAnnotationPresent(Transient.class)) return true;

        return false;
    }

    /**
     * Generate an HTML form from an entity and schema
     */
    public static List<FormField> generateFormInput(Class<?> entityClass, DbObjectSchema entity, String actionUrl, boolean isEditing, boolean isSearching, Map<String, String> validationErrors) {
        return getFields(entityClass, isEditing, isSearching, entity);
    }

    /**
     * Format a field name into a human-readable label
     */
    private static String formatLabel(String fieldName) {
        String result = fieldName.replaceAll("([A-Z])", " $1")
                .replaceAll("([_])", " ")
                .replaceAll("\\.", " ") // For embedded fields
                .replaceAll("\\s+", " ")
                .trim();

        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }

        return result;
    }

}