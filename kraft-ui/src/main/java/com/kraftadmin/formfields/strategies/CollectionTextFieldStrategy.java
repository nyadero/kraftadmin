package com.kraftadmin.formfields.strategies;

import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.formfields.fields.TextField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.kraftadmin.formfields.FormFieldFactory.extractRequiredValidation;
import static com.kraftadmin.formfields.FormFieldFactory.extractValue;

public class CollectionTextFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return Collection.class.isAssignableFrom(field.getType()) || field.getType().isArray();
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName()) + " comma separated";
        String placeholder = "Enter " + label;

        Object rawValue = extractValue(field, dbObjectSchema);
        String value = "";

        if (rawValue instanceof Collection<?> collection) {
            value = collection.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        } else if (rawValue != null && rawValue.getClass().isArray()) {
            Object[] array = (Object[]) rawValue;
            value = java.util.Arrays.stream(array)
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        }

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new TextField(label, placeholder, required, value, inputName, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
    }
}
