package com.bowerzlabs.formfields;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.service.CrudService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class FormFieldFactory {
    private static final Logger log = LoggerFactory.getLogger(FormFieldFactory.class);
    public static CrudService staticCrudService;

    private final CrudService crudService;
    private static final FieldFactory factory = new FieldFactory();

    @Autowired
    public FormFieldFactory(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostConstruct
    public void init() {
        staticCrudService = this.crudService;
    }

    public static FormField generateFormField(DbObjectSchema dbObjectSchema, Field field, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        return factory.createField(field, dbObjectSchema, inputName, isSearch, subTypes);
    }


//Get the validation string for the field (e.g. "required|email|max:255")
// Check if "required" is one of the rules (case-insensitive is optional)
    public static boolean extractRequiredValidation(Map<String, String> validationRules, Field field) {
        String key = field.getName().toLowerCase();

        if (!validationRules.containsKey(key)) return false;

        String rules = validationRules.get(key);  // e.g. "required|email|max:255"
        if (rules == null || rules.isEmpty()) return false;

        // Split rules by pipe `|`
        String[] ruleArray = rules.split("\\|");

        // Check if "required" is one of the rules
        boolean required = Arrays.stream(ruleArray)
                .map(String::trim)
                .anyMatch(rule -> rule.equalsIgnoreCase("required"));

//        log.info("Field '{}' is required: {}", key, required);
        return required;
    }

    // Convert an array to a List<Object>
    private static List<Object> arrayToList(Object array) {
        List<Object> list = new ArrayList<>();
        if (array != null && array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(array, i));
            }
        }
        return list;
    }

    // Convert a Collection (List/Set) to a List<Object>
    private static List<Object> collectionToList(Object collection) {
        if (collection instanceof Collection<?>) {
            return new ArrayList<>((Collection<?>) collection);
        }
        return new ArrayList<>();
    }

    public static String formatLabel(String fieldName) {

        if (fieldName == null || fieldName.trim().isEmpty()) {
            return "";
        }

        // Replace dots and underscores with spaces first
        String result = fieldName.replaceAll("[._]", " ");

        // Insert spaces before uppercase letters (handling camelCase nicely)
        result = result.replaceAll("([a-z])([A-Z])", "$1 $2");

        // Normalize spaces
        result = result.replaceAll("\\s+", " ").trim();

        // Capitalize first letter
        if (result.length() > 0) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }

        return result;
    }


    public static Object extractValue(Field field, DbObjectSchema schema) {
        try {
            if (schema.getEntity() != null) {
                Field entityField = schema.getEntity().getClass().getDeclaredField(field.getName());
                entityField.setAccessible(true);
                return entityField.get(schema.getEntity());
            } else {
                return schema.getFieldsWithData().get(field.getName());
            }
        } catch (Exception e) {
            return null;
        }
    }


}
