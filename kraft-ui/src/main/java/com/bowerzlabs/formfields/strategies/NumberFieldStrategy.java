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

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new NumberField(label, value != null ? (Number) value : 0, field.getName(), required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
