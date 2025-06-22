package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.SelectField;
import jakarta.persistence.Enumerated;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class EnumFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(Enumerated.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        List<Object> options = Collections.emptyList();
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        // Dynamically get enum values
        Class<?> enumType = field.getType();
        if (enumType.isEnum()) {
            // Get all enum constants
            options = Arrays.asList(enumType.getEnumConstants());
        }
        return new SelectField(label, inputName, placeholder, options, value, required, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
    }
}
