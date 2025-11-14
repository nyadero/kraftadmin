package com.kraftadmin.formfields.strategies;

import com.kraftadmin.EntitiesScanner;
import com.kraftadmin.annotations.DisplayField;
import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.formfields.fields.SearchableSelectField;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kraftadmin.formfields.FormFieldFactory.*;

public class SearchableSelectFieldStrategy implements FormFieldStrategy {
    public static final Logger log = LoggerFactory.getLogger(SearchableSelectFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (kraftAdminField.inputType() != FormInputType.Type.UNSET) {
                return kraftAdminField.inputType().equals(FormInputType.Type.SEARCH_SELECT);
            }
        }
        return field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            // return null when input is not editable
            if (!kraftAdminField.editable()) {
                return null;
            }
            label = !kraftAdminField.label().trim().isEmpty() ? kraftAdminField.label() : label;
            placeholder = !kraftAdminField.placeholder().trim().isEmpty() ? kraftAdminField.placeholder() : placeholder;
        }

        Class<?> relatedEntityClass = field.getType();
        String entityName = EntitiesScanner.resolveEntityNameManual(relatedEntityClass);
        List<DbObjectSchema> relatedEntities = staticCrudService.getAll(entityName);
        Map<Object, Object> optionsMap = new HashMap<>();
        for (DbObjectSchema related : relatedEntities) {
            Object key;
            Object value1;

            try {
                Field idField = relatedEntityClass.getDeclaredField(related.getPrimaryKey());
                idField.setAccessible(true);

                if (field.isAnnotationPresent(DisplayField.class)) {
                    DisplayField displayField = field.getAnnotation(DisplayField.class);
                    String displayFieldName = displayField.value();
                    Field field1 = relatedEntityClass.getDeclaredField(displayFieldName);
                    field1.setAccessible(true);

                    key = field1.get(related.getEntity());
                } else {
                    key = idField.get(related.getEntity()); // fallback key
                }

                value1 = idField.get(related.getEntity());
                optionsMap.put(key, value1);

            } catch (Exception e) {
                log.error("Error extracting options from related entity", e);
            }
        }

//  Only set displayField if editing
        if (dbObjectSchema.getEntity() != null) {
            Object fieldValue = extractValue(field, dbObjectSchema);
            if (fieldValue != null) {
                try {
                    Field idField = fieldValue.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    // Extract actual ID from Talent object
                    value = idField.get(fieldValue);
                } catch (Exception e) {
                    log.error("Failed to extract ID from related object displayField", e);
                }
            }
        }


        return new SearchableSelectField(label, inputName, "Type " + inputName + " " + inputName + " to search", optionsMap, value, required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
