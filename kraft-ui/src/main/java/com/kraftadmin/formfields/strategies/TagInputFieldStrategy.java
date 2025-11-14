

package com.kraftadmin.formfields.strategies;

import com.kraftadmin.EntitiesScanner;
import com.kraftadmin.annotations.DisplayField;
import com.kraftadmin.annotations.FormInputType;
import com.kraftadmin.annotations.KraftAdminField;
import com.kraftadmin.database.DbObjectSchema;
import com.kraftadmin.formfields.FormField;
import com.kraftadmin.formfields.fields.TagInput;
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

import static com.kraftadmin.formfields.FormFieldFactory.*;

public class TagInputFieldStrategy implements FormFieldStrategy {
    private static final Logger log = LoggerFactory.getLogger(TagInputFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {

        // Check ManyToMany annotation
        boolean hasManyToMany = field.isAnnotationPresent(ManyToMany.class);

        if (!hasManyToMany) {
            return false;
        }

        // Check if field type is a Collection
        Class<?> fieldType = field.getType();
        boolean isCollection = Collection.class.isAssignableFrom(fieldType);

        boolean supports = isCollection;

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            if (kraftAdminField.inputType() != FormInputType.Type.UNSET) {
                return kraftAdminField.inputType().equals(FormInputType.Type.TAGS);
            }
        }

        return supports;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) throws NoSuchFieldException, IllegalAccessException {

        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        if (field.isAnnotationPresent(KraftAdminField.class)) {
            KraftAdminField kraftAdminField = field.getAnnotation(KraftAdminField.class);
            // return null when input is not editable
            if (!kraftAdminField.editable()) {
                return null;
            }
            label = !kraftAdminField.label().trim().isEmpty() ? kraftAdminField.label() : label;
            placeholder = !kraftAdminField.placeholder().trim().isEmpty() ? kraftAdminField.placeholder() : placeholder;
        }


        // Get the actual entity class from the generic type parameter
        Class<?> relatedEntityClass = getGenericTypeClass(field);

//        log.info("relatedEntityClass in search select field strategy {} {}",
//                field.getGenericType(),
//                field.getDeclaringClass().getDeclaredField(field.getName()).getClass().getSimpleName()
////                field.get(field.getName())
//        );

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