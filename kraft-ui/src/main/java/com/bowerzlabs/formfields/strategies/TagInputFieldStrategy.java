

package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.TagInput;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bowerzlabs.formfields.FormFieldFactory.*;

public class TagInputFieldStrategy implements FormFieldStrategy {
    private static final Logger log = LoggerFactory.getLogger(TagInputFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
//        log.info("=== TagInputFieldStrategy.supports() for field: {} ===", field.getName());
//        log.info("Field type: {}", field.getType().getSimpleName());

        // Check FormInputType annotation
        boolean hasFormInputType = field.isAnnotationPresent(FormInputType.class);
//        log.info("Has @FormInputType: {}", hasFormInputType);

        if (!hasFormInputType) {
//            log.info("Field {} missing @FormInputType annotation", field.getName());
            return false;
        }

        FormInputType formInputType = field.getAnnotation(FormInputType.class);
        boolean isTagsType = formInputType.value().equals(FormInputType.Type.TAGS);
//        log.info("FormInputType value: {}, is TAGS: {}", formInputType.value(), isTagsType);

        if (!isTagsType) {
//            log.info("Field {} FormInputType is not TAGS", field.getName());
            return false;
        }

        // Check ManyToMany annotation
        boolean hasManyToMany = field.isAnnotationPresent(ManyToMany.class);
//        log.info("Has @ManyToMany: {}", hasManyToMany);

        if (!hasManyToMany) {
//            log.info("Field {} missing @ManyToMany annotation", field.getName());
            return false;
        }

        // Check if field type is a Collection
        Class<?> fieldType = field.getType();
        boolean isCollection = Collection.class.isAssignableFrom(fieldType);
//        log.info("Field type: {}, is Collection: {}", fieldType.getSimpleName(), isCollection);

        boolean supports = isCollection;
//        log.info("TagInputFieldStrategy supports field {}: {}", field.getName(), supports);

        return supports;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) throws NoSuchFieldException, IllegalAccessException {
//        log.info("=== TagInputFieldStrategy.createField() for field: {} ===", field.getName());

        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

//        log.info("Extracted value: {}", value);

        // Get the actual entity class from the generic type parameter
        Class<?> relatedEntityClass = getGenericTypeClass(field);
//        log.info("Related entity class: {}", relatedEntityClass.getSimpleName());

//        Class<?> relatedEntityClass = field.getType();
        log.info("relatedEntityClass in search select field strategy {} {}",
                field.getGenericType(),
                field.getDeclaringClass().getDeclaredField(field.getName()).getClass().getSimpleName()
//                field.get(field.getName())
        );

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

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
//        log.info("Field required: {}", required);

//        TagInput tagInput = new TagInput(inputName, label, placeholder, required, optionsMap,
//                dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors(), optionsMap);

        // Use the correct constructor parameter order
        TagInput tagInput = new TagInput(inputName, label, placeholder, required, value,
                dbObjectSchema.getValidationErrors(), dbObjectSchema.getValidationRules(), optionsMap);

//        log.info("Created TagInput: {}", tagInput);
        return tagInput;
    }

    /**
     * Extract the generic type parameter from a Collection field
     * For example: List<Tag> -> Tag.class
     */
    private Class<?> getGenericTypeClass(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            if (actualTypeArguments.length > 0) {
                Type actualType = actualTypeArguments[0];
                if (actualType instanceof Class<?>) {
                    return (Class<?>) actualType;
                }
            }
        }

        // Fallback - this shouldn't happen for properly defined generic collections
        throw new IllegalArgumentException("Unable to determine generic type for field: " + field.getName());
    }
}