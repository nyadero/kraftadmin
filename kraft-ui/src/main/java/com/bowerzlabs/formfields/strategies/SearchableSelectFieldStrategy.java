package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.SearchableSelectField;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bowerzlabs.formfields.FormFieldFactory.*;

public class SearchableSelectFieldStrategy implements FormFieldStrategy {
    public static final Logger log = LoggerFactory.getLogger(SearchableSelectFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
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

//  Only set value if editing
        if (dbObjectSchema.getEntity() != null) {
            Object fieldValue = extractValue(field, dbObjectSchema);
            if (fieldValue != null) {
                try {
                    Field idField = fieldValue.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    // Extract actual ID from Talent object
                    value = idField.get(fieldValue);
                } catch (Exception e) {
                    log.error("Failed to extract ID from related object value", e);
                }
            }
        }


        return new SearchableSelectField(label, inputName, "Type " + inputName + " " + inputName + " to search", optionsMap, value, required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
