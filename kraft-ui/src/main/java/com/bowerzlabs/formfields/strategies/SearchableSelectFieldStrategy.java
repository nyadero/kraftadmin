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
        log.info("relatedEntityClass in search select field strategy {}", relatedEntityClass);
        String entityName = EntitiesScanner.resolveEntityNameManual(relatedEntityClass); // Pass EntityManager
        List<DbObjectSchema> relatedEntities = staticCrudService.getAll(entityName);
        Map<Object, Object> optionsMap = new HashMap<>();
        for (DbObjectSchema related : relatedEntities) {
            Object key;
            Object value1;

            try {
                if (field.isAnnotationPresent(DisplayField.class)) {
                    DisplayField displayField = field.getAnnotation(DisplayField.class);
                    String displayFieldName = displayField.value();
                    Field field1 = relatedEntityClass.getDeclaredField(displayFieldName);
                    Field idField = relatedEntityClass.getDeclaredField(related.getPrimaryKey());
                    field1.setAccessible(true);
                    idField.setAccessible(true);
                    key = field1.get(related.getEntity());
                    value1 = idField.get(related.getEntity());
                    optionsMap.put(key, value1);
                } else {
                    Field idField = relatedEntityClass.getDeclaredField(related.getPrimaryKey());
                    idField.setAccessible(true);
                    key = idField.get(related.getEntity());
                    value1 = idField.get(related.getEntity());
                    optionsMap.put(key, value1);
                }
//                primaryKey = related.getPrimaryKey();

            } catch (Exception e) {
                log.error("Error extracting options from related entity", e);
            }
        }
        return new SearchableSelectField(label, inputName, "Type " + inputName + " " + inputName + " to search", optionsMap, value, required, dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules());
    }
}
