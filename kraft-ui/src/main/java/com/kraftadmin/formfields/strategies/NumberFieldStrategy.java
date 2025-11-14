package com.kraftadmin.formfields.strategies;

import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.formfields.fields.NumberField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.List;

import static com.kraftadmin.formfields.FormFieldFactory.extractRequiredValidation;
import static com.kraftadmin.formfields.FormFieldFactory.extractValue;

public class NumberFieldStrategy implements FormFieldStrategy {
    static boolean isNumeric(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (kraftAdminField.inputType() != FormInputType.Type.UNSET) {
                return kraftAdminField.inputType().equals(FormInputType.Type.NUMBER);
            }
        }
        return isNumeric(field.getType());
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        Object value = extractValue(field, dbObjectSchema);
        String placeholder = "Enter " + label;

        Number defaultValue = getDefaultValue(field.getType(), value);

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (!kraftAdminField.editable()) {
                return null;
            }
            label = !kraftAdminField.label().trim().isEmpty() ? kraftAdminField.label() : label;
            placeholder = !kraftAdminField.placeholder().trim().isEmpty() ? kraftAdminField.placeholder() : placeholder;
        }

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new NumberField(label, placeholder, defaultValue, field.getName(), required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }

    private Number getDefaultValue(Class<?> fieldType, Object value) {
        // If displayField is not null, cast and return it
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
