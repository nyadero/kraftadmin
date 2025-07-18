package com.bowerzlabs;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.FormFieldFactory;
import com.bowerzlabs.formfields.fields.SelectField;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputGenerator {
    private static final Map<EntityMetaModel, List<FormField>> FORM_FIELD_CACHE = new ConcurrentHashMap<EntityMetaModel, List<FormField>>();
    private static final Logger log = LoggerFactory.getLogger(InputGenerator.class);

    public static List<FormField> getFields(EntityMetaModel entityClass, boolean isCreating, boolean isSearching, DbObjectSchema dbObjectSchema) {
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
    private static List<FormField> extractFields(EntityMetaModel clazz, boolean isCreating, boolean isSearch, DbObjectSchema dbObjectSchema, String prefix) {
        try {
            List<FormField> formFields = new ArrayList<>();
            List<EntityType<?>> subTypes = clazz.getSubTypes();

            if (!clazz.getSubTypes().isEmpty()) {
                List<Object> options = new ArrayList<>();
                for (EntityType<?> subType : clazz.getSubTypes()) {
                    String subTypeName = subType.getJavaType().getSimpleName();
                    options.add(subTypeName);
                }

                SelectField subtypeSelect = new SelectField(
                        "Choose " + clazz.getEntityClass().getJavaType().getSimpleName() + " Type",
                        "subtype",
                        "Select " + clazz.getEntityClass().getJavaType().getSimpleName() + " Type",
                        options,
                        null,
                        true,
                        Map.of(), Map.of()
                );

                formFields.add(0, subtypeSelect); // add it to the top
            }

            for (Field field : clazz.getEntityClass().getJavaType().getDeclaredFields()){
                field.setAccessible(true);
//                log.info("field {} should excluded {}", field, shouldExcludeField(field, isCreating));
                if (field.isSynthetic() || shouldExcludeField(field, isCreating)) continue;

                String fieldName = (prefix.isEmpty() ? "" : prefix + ".") + field.getName();
                String inputName = isSearch ? "filter." + fieldName : fieldName;

                FormField formField = FormFieldFactory.generateFormField(dbObjectSchema, field, inputName, isSearch, subTypes);

                if (formField != null && (formField.getLabel() == null || formField.getLabel().isEmpty())) {
                    formField.setLabel(formatLabel(fieldName));
                }

                formFields.add(formField);
            }

            // Subclass-specific fields
            for (EntityType<?> subType : subTypes) {
                Class<?> subClass = subType.getJavaType();
                String subtypeName = subClass.getSimpleName();

                for (Field subField : subClass.getDeclaredFields()) {
                    subField.setAccessible(true);
//                    log.info("subfield {} should excluded {}", subField, shouldExcludeField(subField, isCreating));
                    if (subField.isSynthetic() || shouldExcludeField(subField, isCreating)) continue;

                    String fieldName = (prefix.isEmpty() ? "" : prefix + ".") + subField.getName();
                    String inputName = isSearch ? "filter." + fieldName : fieldName;

                    FormField subFormField = FormFieldFactory.generateFormField(dbObjectSchema, subField, inputName, isSearch, subTypes);

                    if (subFormField != null) {
                        subFormField.setLabel(formatLabel(fieldName));
                        // This is what allows us to toggle fields per subtype using JS
                        subFormField.setWrapperClass("subtype-group subtype-group-" + subtypeName + " hidden");
                        formFields.add(subFormField);
                    }
                }
            }

            formFields.forEach(formField -> {
                log.info("formfield {}", formField);
            });
            return formFields;
        } catch (RuntimeException e) {
            log.info("exception {}", e.toString());
            throw new RuntimeException(e);
        }
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
        // Todo  skip fields having formIgnore annotation
        // Only skip collections
        if (field.isAnnotationPresent(OneToMany.class)) return true;

        if (field.isAnnotationPresent(CreationTimestamp.class) ||
                field.isAnnotationPresent(CreatedDate.class) ||
                field.isAnnotationPresent(UpdateTimestamp.class)) return true;

        return field.isAnnotationPresent(Transient.class);
    }

    /**
     * Generate an HTML form from an entity and schema
     */
    public static List<FormField> generateFormInput(EntityMetaModel entityClass, DbObjectSchema entity, String actionUrl, boolean isEditing, boolean isSearching, Map<String, String> validationErrors) {
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