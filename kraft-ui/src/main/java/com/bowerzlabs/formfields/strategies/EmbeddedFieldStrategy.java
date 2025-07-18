package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.EmbeddedField;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.generateFormField;

public class EmbeddedFieldStrategy implements FormFieldStrategy {
    public static final Logger log = LoggerFactory.getLogger(EmbeddedFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(Embedded.class) || field.getType().isAnnotationPresent(Embeddable.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        String embeddedFieldName = field.getName();
        Object embeddedObject = null;

        // Attempt to extract existing embedded object from actual entity
        try {
            Object actualEntity = dbObjectSchema.getEntity();
            if (actualEntity != null) {
                field.setAccessible(true);
                embeddedObject = field.get(actualEntity);
            }
        } catch (Exception e) {
            log.warn("Couldn't extract embedded object from actual entity: {}", e.getMessage());
        }

        // Fallback to new instance if no embedded object exists
        if (embeddedObject == null) {
            embeddedObject = field.getType().getDeclaredConstructor().newInstance();
        }

        log.info("embeddedInstance3 {} embeddedObject {}", embeddedFieldName, embeddedObject);

        List<FormField> embeddedFields = new ArrayList<>();

        for (Field embeddedField : field.getType().getDeclaredFields()) {
            embeddedField.setAccessible(true);
            FormField subField = generateFormField(dbObjectSchema, embeddedField, "", isSearch, subTypes);
            if (subField != null) {
                String name1 = embeddedFieldName + "." + embeddedField.getName();
                subField.setName(name1);
                subField.setLabel(FormField.formatLabel(name1));

                try {
                    Object fieldVal = embeddedField.get(embeddedObject);
                    if (fieldVal instanceof Collection<?> collection) {
                        Object value = collection.stream()
                                .map(String::valueOf)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("");
                        subField.setValue(value);
                    } else if (fieldVal != null && fieldVal.getClass().isArray()) {
                        Object[] array = (Object[]) fieldVal;
                        Object value = java.util.Arrays.stream(array)
                                .map(String::valueOf)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("");
                        subField.setValue(value);
                    } else {
                        log.info("subfield '{}' actual value = {}", embeddedField.getName(), fieldVal);
                        subField.setValue(fieldVal != null ? fieldVal.toString() : null);
                    }
                } catch (Exception e) {
                    log.warn("Error reading value of subfield '{}': {}", embeddedField.getName(), e.getMessage());
                }
                embeddedFields.add(subField);
            }
        }

        log.info("embeddedFields: {}", embeddedFields);

        return new EmbeddedField(field.getName(), embeddedFields, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}


//public class EmbeddedFieldStrategy implements FormFieldStrategy {
//    public static final Logger log = LoggerFactory.getLogger(EmbeddedFieldStrategy.class);
//
//    @Override
//    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
//        return field.isAnnotationPresent(Embedded.class) || field.getType().isAnnotationPresent(Embeddable.class);
//    }
//
//    @Override
//    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes)
//            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//
//        String label = FormField.formatLabel(field.getName());
//        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
//
//        // Use the actual field name from the entity, not the class name
//        String embeddedFieldName = field.getName(); // This gives "experience" instead of "RequiredExperience"
//        Object embeddedObject = null;
//
//        // Attempt to extract existing embedded object from actual entity
//        try {
//            Object actualEntity = dbObjectSchema.getEntity();
//            if (actualEntity != null) {
//                field.setAccessible(true);
//                embeddedObject = field.get(actualEntity);
//            }
//        } catch (Exception e) {
//            log.warn("Couldn't extract embedded object from actual entity: {}", e.getMessage());
//        }
//
//        // Fallback to new instance if no embedded object exists
//        if (embeddedObject == null) {
//            embeddedObject = field.getType().getDeclaredConstructor().newInstance();
//        }
//
//        log.info("embeddedInstance: fieldName={}, embeddedObject={}", embeddedFieldName, embeddedObject);
//
//        List<FormField> embeddedFields = new ArrayList<>();
//
//        for (Field embeddedField : field.getType().getDeclaredFields()) {
//            // Skip static and transient fields
//            if (Modifier.isStatic(embeddedField.getModifiers()) || Modifier.isTransient(embeddedField.getModifiers())) {
//                continue;
//            }
//
//            embeddedField.setAccessible(true);
//            FormField subField = generateFormField(dbObjectSchema, embeddedField, "", isSearch, subTypes);
//            if (subField != null) {
//                // Now this will create "experience.seniority" instead of "RequiredExperience.seniority"
//                String name1 = embeddedFieldName + "." + embeddedField.getName();
//                subField.setName(name1);
//                subField.setLabel(FormField.formatLabel(name1));
//
//                try {
//                    Object fieldVal = embeddedField.get(embeddedObject);
//                    log.info("subfield '{}' actual value = {}", embeddedField.getName(), fieldVal);
//                    subField.setValue(fieldVal != null ? fieldVal.toString() : null);
//                } catch (Exception e) {
//                    log.warn("Error reading value of subfield '{}': {}", embeddedField.getName(), e.getMessage());
//                }
//
//                embeddedFields.add(subField);
//            }
//        }
//
//        log.info("embeddedFields: {}", embeddedFields);
//
//        return new EmbeddedField(field.getName(), embeddedFields, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
//    }
//}