package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.NumberField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class NumberFieldStrategy implements FormFieldStrategy {
    static boolean isNumeric(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return isNumeric(field.getType());
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        Object value = extractValue(field, dbObjectSchema);

        Number defaultValue = getDefaultValue(field.getType(), value);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new NumberField(label, defaultValue, field.getName(), required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }

    private Number getDefaultValue(Class<?> fieldType, Object value) {
        // If value is not null, cast and return it
        if (value != null) {
            return (Number) value;
        }

        // Handle primitive types with their default values
        if (fieldType == int.class) {
            return 0;
        } else if (fieldType == long.class) {
            return 0L;
        } else if (fieldType == float.class) {
            return 0.0f;
        } else if (fieldType == double.class) {
            return 0.0;
        }

        // For wrapper types, return null (they can handle null values)
        return null;
    }
}
