package com.kraftadmin.formfields.strategies;

import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.formfields.fields.SelectField;
import jakarta.persistence.Enumerated;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kraftadmin.formfields.FormFieldFactory.extractRequiredValidation;
import static com.kraftadmin.formfields.FormFieldFactory.extractValue;

public class SelectFieldStrategy implements FormFieldStrategy {
    public static final Logger log = LoggerFactory.getLogger(SelectFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (kraftAdminField.inputType() != FormInputType.Type.UNSET) {
                return kraftAdminField.inputType().equals(FormInputType.Type.SELECT);
            }
        }
        return field.isEnumConstant() || field.isAnnotationPresent(Enumerated.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        List<Object> options = Collections.emptyList();
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            // return null when input is not editable
            if (!kraftAdminField.editable()) {
                return null;
            }
            label = !kraftAdminField.label().trim().isEmpty() ? kraftAdminField.label() : label;
            placeholder = !kraftAdminField.placeholder().trim().isEmpty() ? kraftAdminField.placeholder() : placeholder;
        }

        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        // Dynamically get enum values
        Class<?> enumType = field.getType();
        if (enumType.isEnum()) {
            options = getEnumOptions(enumType);
        }

        return new SelectField(label, inputName, placeholder, options, value, required, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
    }

    /**
     * Create OptionItem objects that contain both the enum constant and its display displayField
     */
    private List<Object> getEnumOptions(Class<?> enumType) {
        List<Object> options = new ArrayList<>();
        Object[] enumConstants = enumType.getEnumConstants();

        for (Object enumConstant : enumConstants) {
            String displayValue = getEnumDisplayValue(enumConstant);
            options.add(new OptionItem(enumConstant, displayValue != null ? displayValue : enumConstant.toString()));
        }

        return options;
    }

    /**
     * Get the display displayField for an enum constant by automatically discovering String fields and their getters
     */
    private String getEnumDisplayValue(Object enumConstant) {
        try {
            // Get all declared fields from the enum
            Field[] fields = enumConstant.getClass().getDeclaredFields();

            // Find the first String field (excluding enum metadata fields)
            for (Field field : fields) {
                if (field.getType() == String.class &&
                        !field.getName().equals("name") &&
                        !field.getName().startsWith("$")) {

                    // Try to find the corresponding getter method using standard JavaBean naming convention
                    String getterName = "get" + capitalize(field.getName());
                    try {
                        Method getter = enumConstant.getClass().getMethod(getterName);
                        if (getter.getReturnType() == String.class) {
                            return (String) getter.invoke(enumConstant);
                        }
                    } catch (NoSuchMethodException e) {
                        // If no getter found, try to access the field directly
                        field.setAccessible(true);
                        return (String) field.get(enumConstant);
                    }
                }
            }

            // If no String field found, use toString()
            return enumConstant.toString();

        } catch (Exception e) {
            // If all else fails, use toString()
            return enumConstant.toString();
        }
    }

    /**
     * Capitalize the first letter of a string (helper method for getter name generation)
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}