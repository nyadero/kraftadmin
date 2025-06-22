package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.RadioField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class RadioFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType formInputType = field.getAnnotation(FormInputType.class);
            return formInputType.value().equals(FormInputType.Type.RADIO);
        }
        return field.getType().equals(Boolean.class) || field.getType().equals(boolean.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new RadioField(label, placeholder, required, "true".equalsIgnoreCase(String.valueOf(value)), inputName, new ArrayList<>(), dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
