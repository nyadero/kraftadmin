package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.DateField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class DateFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType inputType = field.getAnnotation(FormInputType.class);
            return inputType.value().equals(FormInputType.Type.DATE);
        }
        return field.getType() == LocalDate.class;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        return new DateField(label, placeholder, required, (LocalDate) value, inputName, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());

    }
}
