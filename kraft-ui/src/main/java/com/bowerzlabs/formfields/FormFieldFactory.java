package com.bowerzlabs.formfields;

import com.bowerzlabs.annotations.DisplayField;
import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.service.CrudService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.bowerzlabs.EntitiesScanner.resolveEntityName;
import static org.atteo.evo.inflector.English.plural;

@Component
public class FormFieldFactory {
    private static final Logger log = LoggerFactory.getLogger(FormFieldFactory.class);
    private static CrudService staticCrudService;

    private final CrudService crudService;

    @Autowired
    public FormFieldFactory(CrudService crudService) {
        this.crudService = crudService;
    }

    @PostConstruct
    public void init() {
        staticCrudService = this.crudService;
    }

    public static FormField createFormFieldsFromEntity(DbObjectSchema dbObjectSchema, Field field, String inputName, boolean isSearch) {
        String label = null;
        String placeholder = null;
        Map<String, String> validationRules = null;
        Object value = null;
        boolean required = !isSearch && extractRequiredValidation(dbObjectSchema.getValidationRules(), field);
        Map<String, String> validationErrors = null;
        boolean isRelationship = false;
        try {
            field.setAccessible(true);
//            label = dbObjectSchema.getFieldLabels().get(field.getName());
            label = formatLabel(dbObjectSchema.getFieldLabels().get(field.getName()));
            placeholder = "Enter " + field.getName();
            // if entity is available use its field values else use values from fieldsWithData LinkedHashMap
            if (dbObjectSchema.getEntity() != null){
                try {
                    Field declaredField = dbObjectSchema.getEntity().getClass().getDeclaredField(field.getName());
                    log.info("declared field {}", declaredField);
                    declaredField.setAccessible(true);
                    value = declaredField.get(dbObjectSchema.getEntity());
                    log.info("value {}", value);
                } catch (Exception e) {
                    log.info("exception {}", e.toString());
                }
            }else{
                value = dbObjectSchema.getFieldsWithData().get(field.getName());
            }

//            name = field.getName();
            List<Object> options = Collections.emptyList();
            String rule = dbObjectSchema.getValidationRules().get(field.getName());
            validationRules = rule != null ? Map.of(field.getName(), rule) : Map.of();
            validationErrors = dbObjectSchema.getValidationErrors();
            isRelationship = extractIsRelation(field);

             if (field.getType().equals(String.class) && !field.isAnnotationPresent(FormInputType.class)) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    if (column.length() > 255) {
                        return new TextAreaField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                    } else {
                        TextField textField = new TextField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                        if (isSearch){
                            textField.setSearchOperations(getSearchOperationsForType(field.getType()));
                        }
                    }
                }
                if (field.isAnnotationPresent(Lob.class)) {
                    return new TextAreaField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                }
                return new TextField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
            }else if (field.isAnnotationPresent(FormInputType.class)) {
                FormInputType inputType = field.getAnnotation(FormInputType.class);
                 return switch (inputType.value()) {
                     case TEXT ->
                             new TextField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                     case NUMBER ->
                             new NumberField(label, value != null ? (Number) value : 0, inputName, required, validationErrors, validationRules);
                     case COLOR ->
                             new ColorField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                     case IMAGE ->
                             new ImageField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                     case DATE ->
                             new DateField(label, placeholder, required, (LocalDate) value, inputName, validationErrors, validationRules);
                     case EMAIL ->
                             new EmailField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                     case PASSWORD ->
                             new PasswordField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                     case FILE ->
                             new FileInput(label, placeholder, required, value, inputName, validationErrors, validationRules);
                     case TEXTAREA ->
                             new TextAreaField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                     case WYSIWYG -> new WYSIWYGField(label, placeholder, required, (String)value, inputName, validationErrors, validationRules);
                     case DATETIME ->
                             new DateTimeField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
                     case TIME ->
                             new TimeField(label, placeholder, required, (LocalTime) value, inputName, validationErrors, validationRules);
                     case RANGE -> null;
                     case TEL ->
                             new TelephoneField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                     case URL ->
                             new URLField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                     case RADIO -> new RadioField(label, placeholder, required, (String) value, inputName, new ArrayList<>(),  validationRules, validationErrors);
                     default -> new TextField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
                 };
            } else if (isNumeric(field.getType())) {
                NumberField numberField = new NumberField(label, value != null ? (Number) value : 0, inputName, required, validationErrors, validationRules);
                if (isSearch){
                    numberField.setSearchOperations(getSearchOperationsForType(field.getType()));
                }
                return numberField;
            } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                return new CheckboxField(label, "true".equalsIgnoreCase(String.valueOf(value)), inputName, required, validationErrors, validationRules, isRelationship);
            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                Class<?> relatedEntityClass = field.getType();
                 String entityName = resolveEntityName(relatedEntityClass);
                 List<DbObjectSchema> relatedEntities = staticCrudService.getAll(relatedEntityClass.getSimpleName());
                log.info("related entities {}", relatedEntities);
                Map<Object, Object> optionsMap = new HashMap<>();
                 String primaryKey = "";
                for (DbObjectSchema related : relatedEntities) {
                    log.info("related {}", related);
                    Object key;
                    Object value1;

                    try {
                        if (field.isAnnotationPresent(DisplayField.class)){
                            DisplayField displayField = field.getAnnotation(DisplayField.class);
                            log.info("displayField {}", displayField);
                            String displayFieldName = displayField.value();
                            Field field1 = relatedEntityClass.getDeclaredField(displayFieldName);
                            Field idField = relatedEntityClass.getDeclaredField(related.getPrimaryKey());
                            log.info("field1 {}", field1);
                            field1.setAccessible(true);
                            idField.setAccessible(true);
                            key = field1.get(related.getEntity());
                            value1 = idField.get(related.getEntity());
                            optionsMap.put(key, value1);
                        }else{
                            Field idField = relatedEntityClass.getDeclaredField(related.getPrimaryKey());
                            log.info("idfield1 {}", idField);
                            idField.setAccessible(true);
                            key = idField.get(related.getEntity());
                            value1 = idField.get(related.getEntity());
                            optionsMap.put(key, value1);
                        }

                        primaryKey = related.getPrimaryKey();

                        log.info("key {}, value {} ", key, value1);
                        log.info("optionsMap {}", optionsMap);
                        log.info("form_name {}", optionsMap);
                        
                    } catch (Exception e) {
                        log.error("Error extracting options from related entity", e);
                    }
                }
                return new SearchableSelectField(label, inputName, "Type " + inputName + " " + inputName + " to search", optionsMap, value, required, validationErrors, validationRules);
             } else if (field.isAnnotationPresent(Enumerated.class)) {
                // Dynamically get enum values
                Class<?> enumType = field.getType();
                if (enumType.isEnum()) {
                    options = Arrays.asList(enumType.getEnumConstants()); // Get all enum constants
                }
                return new SelectField(label, inputName, placeholder, options, value, required, validationErrors, validationRules);
            } else if (field.isAnnotationPresent(Lob.class)) {
                return new TextAreaField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
            } else if (field.getType().equals(LocalDate.class)) {
                return new DateField(label, placeholder, required, (LocalDate) value, inputName, validationErrors, validationRules);
            } else if (field.getType().equals(LocalDateTime.class)) {
                return new DateTimeField(label, placeholder, required, (String) value, inputName, validationErrors, validationRules);
            } else if (field.getType().equals(LocalTime.class)) {
                return new TimeField(label, placeholder, required, (LocalTime) value, inputName, validationErrors, validationRules);
            }else if (field.getType().equals(MultipartFile.class)) {
                 return new FileInput(label, placeholder, required, value, inputName, validationErrors, validationRules);
            } else if (field.isAnnotationPresent(Embedded.class) || field.getType().isAnnotationPresent(Embeddable.class)) {
                String embeddedFieldName = field.getType().getDeclaredConstructor().newInstance().getClass().getSimpleName().toLowerCase();
                log.info("embeddedInstance3 {}", embeddedFieldName);
                List<FormField> embeddedFields = new ArrayList<>();
                 // Loop through embedded fields and generate form fields
                 for (Field embeddedField : field.getType().getDeclaredFields()) {
                     embeddedField.setAccessible(true);
                     FormField subField = createFormFieldsFromEntity(dbObjectSchema, embeddedField, "", isSearch);
                     if (subField != null) {
                         String name1 = embeddedFieldName.concat(".").concat(subField.getPlaceholder().replace("Enter ", ""));
                         subField.setName(name1);
                         subField.setLabel(FormField.formatLabel(name1));
                         embeddedFields.add(subField);
                     }
                 }
                log.info("embeddedFields ${} ", embeddedFields);
                return new EmbeddedField(field.getName(), embeddedFields, validationErrors, validationRules);
            }
             // Handle Array and Collection (List, Set) Fields
            else if (field.getType().isArray()) {
                // Convert array to list
                return new TextField(label, placeholder, required, (String) value, inputName, validationRules, validationErrors);
            }else if (!(field.getType().equals(String.class) || field.getType().equals(byte[].class))) {
                // Check for collection
//                if (List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType())) {
                    // Convert collection to list
                    return new TextField(label, placeholder, required, value, inputName, validationRules, validationErrors);
//                }
            }
            else {
                return new TextField(label, placeholder, required, value, inputName, validationRules, validationErrors);
            }
        } catch (Exception e) {
            log.info("exception1 {}", e.toString());
            return new TextField(label, placeholder, required, value, inputName, validationRules, validationErrors);
        }
    }

    private static List<String> getSearchOperationsForType(Class<?> fieldType) {
        if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
            return List.of("=", ">", "<", ">=", "<=");
        } else if (fieldType == String.class) {
            return List.of("contains", "=", "startsWith", "endsWith");
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return List.of("=");
        } else if (fieldType.isEnum()) {
            return List.of("=");
        } else if (fieldType == LocalDate.class || fieldType == LocalDateTime.class || fieldType == LocalTime.class) {
            return List.of("=", ">", "<", ">=", "<=");
        } else {
            return List.of("="); // fallback
        }
    }


    private static boolean extractIsRelation(Field field) {
        return field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToMany.class);
    }

    private static Object extractDisplayValue(Object entity, String displayFieldName) {
        if (entity == null) return null;
        try {
            Field displayField = entity.getClass().getDeclaredField(displayFieldName);
            displayField.setAccessible(true);
            return displayField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null; // Fallback if the field is not found
        }
    }


//Get the validation string for the field (e.g. "required|email|max:255")
// Check if "required" is one of the rules (case-insensitive is optional)
    private static boolean extractRequiredValidation(Map<String, String> validationRules, Field field) {
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

        log.info("Field '{}' is required: {}", key, required);
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

    private static boolean isNumeric(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }


}
