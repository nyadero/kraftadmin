//package com.bowerzlabs.formfields.strategies;
//
//import com.bowerzlabs.annotations.FormInputType;
//import com.bowerzlabs.database.DbObjectSchema;
//import com.bowerzlabs.formfields.FormField;
//import com.bowerzlabs.formfields.fields.TagInput;
//import groovy.util.logging.Slf4j;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.metamodel.EntityType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
//import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;
//
//public class TagInputFieldStrategy implements FormFieldStrategy {
//    private static final Logger log = LoggerFactory.getLogger(TagInputFieldStrategy.class);
//
/// /    @Override
/// /    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
/// /        log.info("inside supports in taginputstrategy");
/// /        if (field.getType().isArray() && field.isAnnotationPresent(FormInputType.class) && field.isAnnotationPresent(ManyToMany.class)) {
/// /            FormInputType formInputType = field.getAnnotation(FormInputType.class);
/// /            return formInputType.value().equals(FormInputType.Type.TAGS);
/// /        }
/// /        return false;
/// /    }
//
//    @Override
//    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
//        log.info("inside supports in taginputstrategy: checking {}", field.getName());
//
//        boolean isCollection = List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType());
//
//        if (isCollection && field.isAnnotationPresent(FormInputType.class) && field.isAnnotationPresent(ManyToMany.class)) {
//            FormInputType formInputType = field.getAnnotation(FormInputType.class);
//            return formInputType.value().equals(FormInputType.Type.TAGS);
//        }
//        return false;
//    }
//
//
//    @Override
//    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
//        log.info("inside create-field in taginputstrategy");
//        String label = FormField.formatLabel(field.getName());
//        String placeholder = "Enter " + label;
//        Object value = extractValue(field, dbObjectSchema);
//
////        if (value == null) {
////            value = List.of();
////        }
//
//
//        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
//
//        return new TagInput(inputName, label, placeholder, required, value, dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationRules());
////        return new TagInput("tags", "Tags Test", "tags placeholder", false, value, Map.of(), Map.of());
//    }
//}

//
//package com.bowerzlabs.formfields.strategies;
//
//import com.bowerzlabs.annotations.FormInputType;
//import com.bowerzlabs.database.DbObjectSchema;
//import com.bowerzlabs.formfields.FormField;
//import com.bowerzlabs.formfields.fields.TagInput;
//import groovy.util.logging.Slf4j;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.metamodel.EntityType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
//import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
//import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;
//
//public class TagInputFieldStrategy implements FormFieldStrategy {
//    private static final Logger log = LoggerFactory.getLogger(TagInputFieldStrategy.class);
//
//    @Override
//    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
//        log.info("inside supports in taginputstrategy for field: {}", field.getName());
//
//        // Check if field has the required annotations
//        if (!field.isAnnotationPresent(FormInputType.class) || !field.isAnnotationPresent(ManyToMany.class)) {
//            log.info("Field {} missing required annotations", field.getName());
//            return false;
//        }
//
//        // Check if FormInputType is TAGS
//        FormInputType formInputType = field.getAnnotation(FormInputType.class);
//        if (!formInputType.value().equals(FormInputType.Type.TAGS)) {
//            log.info("Field {} FormInputType is not TAGS", field.getName());
//            return false;
//        }
//
//        // Check if field type is a Collection (List, Set, etc.)
//        Class<?> fieldType = field.getType();
//        boolean isCollection = Collection.class.isAssignableFrom(fieldType);
//
//        log.info("Field {} - Type: {}, isCollection: {}", field.getName(), fieldType.getSimpleName(), isCollection);
//
//        return isCollection;
//    }
//
//    @Override
//    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
//        log.info("inside create-field in taginputstrategy for field: {}", field.getName());
//
//        String label = FormField.formatLabel(field.getName());
//        String placeholder = "Enter " + label;
//        Object value = extractValue(field, dbObjectSchema);
//
//        // Handle null value for collections
//        if (value == null) {
//            value = List.of();
//        }
//
//        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
//
//        return new TagInput(inputName, label, placeholder, required, value,
//                dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());
//    }
//}

package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.TagInput;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.bowerzlabs.formfields.FormFieldFactory.extractRequiredValidation;
import static com.bowerzlabs.formfields.FormFieldFactory.extractValue;

public class TagInputFieldStrategy implements FormFieldStrategy {
    private static final Logger log = LoggerFactory.getLogger(TagInputFieldStrategy.class);

    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        log.info("=== TagInputFieldStrategy.supports() for field: {} ===", field.getName());
        log.info("Field type: {}", field.getType().getSimpleName());

        // Check FormInputType annotation
        boolean hasFormInputType = field.isAnnotationPresent(FormInputType.class);
        log.info("Has @FormInputType: {}", hasFormInputType);

        if (!hasFormInputType) {
            log.info("Field {} missing @FormInputType annotation", field.getName());
            return false;
        }

        FormInputType formInputType = field.getAnnotation(FormInputType.class);
        boolean isTagsType = formInputType.value().equals(FormInputType.Type.TAGS);
        log.info("FormInputType value: {}, is TAGS: {}", formInputType.value(), isTagsType);

        if (!isTagsType) {
            log.info("Field {} FormInputType is not TAGS", field.getName());
            return false;
        }

        // Check ManyToMany annotation
        boolean hasManyToMany = field.isAnnotationPresent(ManyToMany.class);
        log.info("Has @ManyToMany: {}", hasManyToMany);

        if (!hasManyToMany) {
            log.info("Field {} missing @ManyToMany annotation", field.getName());
            return false;
        }

        // Check if field type is a Collection
        Class<?> fieldType = field.getType();
        boolean isCollection = Collection.class.isAssignableFrom(fieldType);
        log.info("Field type: {}, is Collection: {}", fieldType.getSimpleName(), isCollection);

        boolean supports = isCollection;
        log.info("TagInputFieldStrategy supports field {}: {}", field.getName(), supports);

        return supports;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        log.info("=== TagInputFieldStrategy.createField() for field: {} ===", field.getName());

        String label = FormField.formatLabel(field.getName());
        String placeholder = "Enter " + label;
        Object value = extractValue(field, dbObjectSchema);

        log.info("Extracted value: {}", value);

        // Handle null value for collections
        if (value == null) {
            value = List.of();
            log.info("Value was null, set to empty list");
        }

        boolean required = extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        log.info("Field required: {}", required);

        TagInput tagInput = new TagInput(inputName, label, placeholder, required, value,
                dbObjectSchema.getValidationRules(), dbObjectSchema.getValidationErrors());

        log.info("Created TagInput: {}", tagInput);
        return tagInput;
    }
}