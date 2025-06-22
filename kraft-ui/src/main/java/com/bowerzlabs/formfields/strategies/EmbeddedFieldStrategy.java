package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.EmbeddedField;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.*;

public class EmbeddedFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(Embedded.class) || field.getType().isAnnotationPresent(Embeddable.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        String embeddedFieldName = field.getType().getDeclaredConstructor().newInstance().getClass().getSimpleName().toLowerCase();
//                log.info("embeddedInstance3 {}", embeddedFieldName);
        List<FormField> embeddedFields = new ArrayList<>();
        // Loop through embedded fields and generate form fields
        for (Field embeddedField : field.getType().getDeclaredFields()) {
            embeddedField.setAccessible(true);
            FormField subField = generateFormField(dbObjectSchema, embeddedField, "", isSearch, subTypes);
            if (subField != null) {
                String name1 = embeddedFieldName.concat(".").concat(subField.getPlaceholder().replace("Enter ", ""));
                subField.setName(name1);
                subField.setLabel(FormField.formatLabel(name1));
                embeddedFields.add(subField);
            }
        }
//                log.info("embeddedFields ${} ", embeddedFields);
        return new EmbeddedField(field.getName(), embeddedFields, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
