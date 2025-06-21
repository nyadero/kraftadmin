package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.TextField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class TextFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.getType().equals(String.class) && !field.isAnnotationPresent(FormInputType.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        return new TextField(label, placeholder, required, value, inputName, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
    }
}
