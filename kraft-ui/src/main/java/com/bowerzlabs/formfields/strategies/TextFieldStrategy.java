package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.annotations.KraftAdminField;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.TextField;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;


public class TextFieldStrategy implements FormFieldStrategy {
    private static final Logger log = LoggerFactory.getLogger(TextFieldStrategy.class);
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
//        if (field.isAnnotationPresent(FormInputType.class)) {
//            FormInputType formInputType = field.getAnnotation(FormInputType.class);
//            if (!formInputType.value().equals(FormInputType.Type.TEXT)) {
//                return false;
//            }
//        }

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (kraftAdminField.inputType() == null || kraftAdminField.inputType() != FormInputType.Type.TEXT) {
                return false;
            }
        }

        // Don't support collections
        if (List.class.isAssignableFrom(field.getType())) {
            return false;
        }

        // Only support strings or primitive values
        Class<?> type = field.getType();
        return type.equals(String.class) || type.isPrimitive();
    }


    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            label = !kraftAdminField.label().trim().isEmpty() ? kraftAdminField.label() : FormField.formatLabel(field.getName());
            placeholder = !kraftAdminField.placeholder().trim().isEmpty() ? kraftAdminField.placeholder() : "Enter " + FormField.formatLabel(field.getName());
        }
//        else {
//            label = FormField.formatLabel(field.getName());
//            placeholder = "Enter " + label;
//        }

        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        return new TextField(label, placeholder, required, value, inputName, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
    }
}
