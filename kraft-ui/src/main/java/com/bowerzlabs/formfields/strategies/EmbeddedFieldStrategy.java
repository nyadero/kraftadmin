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
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.*;

public class EmbeddedFieldStrategy implements FormFieldStrategy {
    public static final Logger log = LoggerFactory.getLogger(EmbeddedFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(Embedded.class) || field.getType().isAnnotationPresent(Embeddable.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);
        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        String embeddedFieldName = field.getType().getSimpleName().toLowerCase();
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
                String name1 = embeddedFieldName + "." + subField.getPlaceholder().replace("Enter ", "").toLowerCase();
                subField.setName(name1);
                subField.setLabel(FormField.formatLabel(name1));

                try {
                    Object fieldVal = embeddedField.get(embeddedObject);
                    log.info("subfield '{}' actual value = {}", embeddedField.getName(), fieldVal);
                    subField.setValue(fieldVal != null ? fieldVal.toString() : null);
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