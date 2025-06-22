package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.TextAreaField;
import jakarta.persistence.Column;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class TextAreaFieldStrategy implements FormFieldStrategy {

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.getType().equals(String.class) && !field.isAnnotationPresent(FormInputType.class)) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                return column.length() > 255;
            }
        }
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType formInputType = field.getAnnotation(FormInputType.class);
            return formInputType.value().equals(FormInputType.Type.TEXTAREA);
        }
        return false;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        return new TextAreaField(label, placeholder, required, (String) value, inputName, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
