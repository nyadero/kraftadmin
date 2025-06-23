package com.bowerzlabs.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.annotations.*;
import com.bowerzlabs.config.SpringContextHolder;
import com.bowerzlabs.constants.PerformableAction;
import com.bowerzlabs.models.kraftmodels.DisplayFieldsPreference;
import com.bowerzlabs.repository.kraftrepos.KraftDisplayedFieldPreferenceRepository;
import com.bowerzlabs.utils.DisplayUtils;
import com.bowerzlabs.utils.KraftUtils;
import com.bowerzlabs.validation.ValidationUtils;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import static org.atteo.evo.inflector.English.plural;

public class DbObjectSchema {
    private static final Logger log = LoggerFactory.getLogger(DbObjectSchema.class);
    public static final int MAX_FIELDS = 6;
    /**
     * Name of the entity/table
     */
    private String entityName = "Unknown";
    /**
     * Name of the primary key field
     */
    private String primaryKey;
    /**
     * Map of validation rules, key being the field, and values its validation rules
     */
    private final Map<String, String> validationRules = new HashMap<>();
    /**
     * Map of labels for each field
     */
    private final Map<String, String> fieldLabels = new HashMap<>();
    /**
     * list of display fields
     */
    private final List<Field> displayFields = new ArrayList<>();
    /**
     * list of image/file fields
     */
    private final List<Field> fileFields = new ArrayList<>();
    /**
     * map of defined display fields by user
     */
    private List<Field> customDisplayFields = new ArrayList<>();
    /**
     * contains a field with its value, id is always first and createdAt is last
     */
    private final LinkedHashMap<String, Object> fieldsWithData = new LinkedHashMap<>();
    /**
     * this returns all the fields with their data
     */
    private final Map<String, Object> allFieldsWithData = new LinkedHashMap<>();
    /**
     * the current entity we are using to build the dbobject schema
     */
    private Object entity;
    /**
     * contains a list of all the actions that can be performed on an entity ie, create, delete and update
     */
    private final List<PerformableAction> performableActions = new ArrayList<>();
    /**
     * Map of validations errors, key is the field
     */
    private final Map<String, String> validationErrors = new HashMap<>();
    /**
     * field annotated by the user
     */
    private Field annotatedDisplayField;
    /**
     * the annotatedDisplayField value
     */
    private Object annotatedDisplayValue;
    /**
     * list of fields to be used to search data in the db
     */
    private final List<Field> searchableFields = new ArrayList<>();
    /**
     * list of fields to be used to sort data in the db
     */
    private final List<Field> sortableFields = new ArrayList<>();
    /**
     * list of fields to be used to filter data in the db
     */
    private final List<Field> filterableFields = new ArrayList<>();
    /**
     * list of all fields for the entity
     */
    private List<Field> allFields = new ArrayList<>();
    /**
     * field map
     */
    Map<String, Field> fieldMap = new HashMap<>();
    private final Field idField;


    // DisplayFieldsPreferenceRepository
    KraftDisplayedFieldPreferenceRepository prefRepo =
            SpringContextHolder.getBean(KraftDisplayedFieldPreferenceRepository.class);

    public DbObjectSchema(EntityMetaModel entityClass, Object entity) {
        // fetch the display preferences for this entity
        Optional<DisplayFieldsPreference> pref = prefRepo.findById(plural(entityClass.getEntityClass().getName()));

        try {
            if (!entityClass.getEntityClass().getJavaType().isAnnotationPresent(Entity.class)) {
                throw new IllegalArgumentException("Provided class is not an entity: " + entityClass.getEntityClass().getJavaType().getName());
            }

            // determine performable actions for the entity
           performableActions.addAll(determineActionsForEntity(entityClass.getEntityClass().getJavaType()));

            // all fields declared in an entity class
//            allFields = List.of(entityClass.getDeclaredFields());
            allFields = getAllFields(entityClass);
//            log.info("allFields {}", allFields.stream().map(Field::getName).toList());


            // a hashmap
            fieldMap = new HashMap<>();

            // assign entityName to the class/entity's name
            this.entityName = entityClass.getEntityClass().getJavaType().getSimpleName();

            // assign entity to the entity received via constructor argument
            this.entity = entity;

                // loop over entity declared fields
                for (Field field : allFields) {
                    field.setAccessible(true);

                    // Register searchable, sortable, filterable
                    if (field.isAnnotationPresent(Searchable.class)) {
                        searchableFields.add(field);
                    }
                    
                    if (field.isAnnotationPresent(Sortable.class) || isNumeric(field.getType()) || isDateType(field.getType())) {
                        sortableFields.add(field);
                    }

                    if (field.isAnnotationPresent(Filterable.class) || field.isAnnotationPresent(Enumerated.class)) {
                        filterableFields.add(field);
                    }

                    if (field.isAnnotationPresent(FormInputType.class) || field.isAnnotationPresent(DisplayImage.class)){
                        FormInputType formInputType = field.getAnnotation(FormInputType.class);
                        DisplayImage displayImage = field.getAnnotation(DisplayImage.class);
                        if (formInputType.value().equals(FormInputType.Type.FILE) || formInputType.value().equals(FormInputType.Type.IMAGE)){
                            fileFields.add(field);
                        }
                        if (displayImage != null){
                            fileFields.add(field);
                        }
                    }

                    validationRules.put(field.getName(), ValidationUtils.extractValidationRules(field));
                    fieldLabels.put(field.getName(), extractFieldLabel(field));
                    customDisplayFields = new ArrayList<>();
                }

                for (Field f : allFields) {
                    try {
                        f.setAccessible(true); // Allow access to private fields
                        if (f.isAnnotationPresent(DisplayField.class)) {
                            DisplayField displayField = f.getAnnotation(DisplayField.class);
                            String displayFieldName = displayField.value();

                            // Try to get the nested field by name (from the type of f)
                            Field nestedField = null;
                            try {
                                nestedField = f.getType().getDeclaredField(displayFieldName);
                                nestedField.setAccessible(true);
                            } catch (NoSuchFieldException e) {
                                log.warn("Field '{}' not found in type '{}'", displayFieldName, f.getType().getSimpleName());
                            }

                            // Put either the nested field (if found) or the outer field itself
                            if (nestedField != null) {
                                fieldMap.put(f.getName(), nestedField);
                            } else {
                                fieldMap.put(f.getName(), f);
                            }
                        } else {
                            fieldMap.put(f.getName(), f);
                        }
                    } catch (Exception e) {
                        log.error(" Error processing field '{}': {}", f.getName(), e.getMessage(), e);
                        fieldMap.put(f.getName(), f); // Fallback to the original field
                    }
                }

                // update allFieldsWithData map
                for (Field field: allFields){
//                if (getFieldValue(entity, field).toString().toLowerCase().contains("password")) continue;
                    allFieldsWithData.put(field.getName(), getFieldValue(entity, field));
                }

                // Always show ID first
                idField = allFields.stream().filter(f -> f.isAnnotationPresent(Id.class)).findFirst().orElse(null);
                // log.info("idField {}", idField);
                if (idField != null) {
                    idField.setAccessible(true);
                    fieldsWithData.put(KraftUtils.formatLabel(idField.getName()), getIdFieldValue(entity, idField));
                    displayFields.add(idField);
                    primaryKey = idField.getName();
                }

                // Use preferred fields if available
                if (pref.isPresent() && !pref.get().getFields().isEmpty()) {
                    // Limit to 6 fields max
                    int count = 0;
                    for (String fieldName : pref.get().getFields()) {
                        if (fieldMap.containsKey(fieldName) &&
                                !fieldName.equals("id") &&
                                !fieldName.equals("createdAt") && !fieldName.contains("password")) {
                            Field f = fieldMap.get(fieldName);
                            f.setAccessible(true);
                            fieldsWithData.put(KraftUtils.formatLabel(fieldName), getFieldValue(entity, f));
                            displayFields.add(f);
                            count++;
                            if (count >= MAX_FIELDS) break; // stop after 6 fields
                        }
                    }
                } else {
                    // Use fallback fields if no custom fields defined
                    allFields.stream()
                            .filter(f -> !f.isAnnotationPresent(Id.class) && !f.getName().equals("createdAt") && !f.getName().contains("password"))
                            .limit(MAX_FIELDS)
                            .forEach(f -> {
                                f.setAccessible(true);
                                fieldsWithData.put(KraftUtils.formatLabel(f.getName()), getFieldValue(entity, f));
                                displayFields.add(f);
                            });
                }

                // Add createdAt last
                Field createdAtField = allFields.stream().filter(f -> f.getName().equals("createdAt") || f.isAnnotationPresent(CreatedDate.class) || f.isAnnotationPresent(CreationTimestamp.class)).findFirst()
                        .orElse(null);
                if (createdAtField != null) {
                    createdAtField.setAccessible(true);
                    fieldsWithData.put(KraftUtils.formatLabel("createdAt"), getFieldValue(entity, createdAtField));
                    displayFields.add(createdAtField);
                }
        } catch (Exception e) {
            log.info(e.toString());
            throw new RuntimeException(e);
        }

    }



    private boolean isDateType(Class<?> type) {
        return type == Date.class || type == LocalDate.class || type == LocalDateTime.class || type == LocalTime.class;
    }

    private static boolean isNumeric(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }

    private String extractFieldLabel(Field field) {
        String name = field.getName();
        return name.substring(0, 1).toUpperCase() + name.substring(1).replaceAll("([A-Z])", " $1").trim();
    }

//    private Object getFieldValue(Object entity, Field field) {
//        if (entity == null || field == null) return null;
//
//        field.setAccessible(true);
//
//        try {
//            Object value = field.get(entity); // Could be a nested object or collection
//
//            if (field.isAnnotationPresent(DisplayField.class)) {
//                DisplayField displayField = field.getAnnotation(DisplayField.class);
//                String path = displayField.value(); // e.g., "name" or "profile.name"
//
//                if (value instanceof Collection<?>) {
//                    Collection<?> collection = (Collection<?>) value;
//                    List<Object> displayValues = new ArrayList<>();
//                    for (Object item : collection) {
//                        displayValues.add(getNestedValue(item, path));
//                    }
//                    return displayValues;
//                }
//
//                return getNestedValue(value, path); // Handle single nested object
//            }
//
//
//            return value;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Error getting field value", e);
//        }
//    }

    private Object getFieldValue(Object entity, Field field) {
        if (entity == null || field == null) return null;

        field.setAccessible(true);

        try {
            Object value = field.get(entity); // Could be a nested object or collection

            // Handle @DisplayField annotated fields
            if (field.isAnnotationPresent(DisplayField.class)) {
                DisplayField displayField = field.getAnnotation(DisplayField.class);
                String path = displayField.value(); // e.g., "name" or "profile.name"

                if (value instanceof Collection<?>) {
                    Collection<?> collection = (Collection<?>) value;
                    List<String> displayValues = new ArrayList<>();
                    for (Object item : collection) {
                        Object nestedValue = getNestedValue(item, path);
                        displayValues.add(DisplayUtils.formatForDisplay(nestedValue != null ? nestedValue.getClass() : String.class, nestedValue));
                    }
                    return String.join(", ", displayValues); // Return as a single string for view
                }

                Object nestedValue = getNestedValue(value, path);
                return DisplayUtils.formatForDisplay(nestedValue != null ? nestedValue.getClass() : String.class, nestedValue);
            }

            // Fallback formatting for non-nested fields
            return DisplayUtils.formatForDisplay(field.getType(), value);

        } catch (Exception e) {
            log.info("exception {}", e.toString());
            return null;
//            throw new RuntimeException("Error getting field value", e);
        }
    }

    public Object getIdFieldValue(Object entity, Field idField) {
        if (entity == null || idField == null) return null;
        idField.setAccessible(true);
        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
            log.info("error {}", e.toString());
        }
        return null;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, String> getValidationRules() {
        return validationRules;
    }

    public Map<String, String> getFieldLabels() {
        return fieldLabels;
    }

    public List<Field> getDisplayFields() {
        return displayFields;
    }

    public List<Field> getCustomSettings() {
        return customDisplayFields;
    }

    public Map<String, Object> getFieldsWithData() {
        return new LinkedHashMap<>(fieldsWithData);
    }

    public Object getEntity() {
        return entity;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public List<Field> getSearchableFields() {
        return searchableFields;
    }

    public List<Field> getSortableFields() {
        return sortableFields;
    }

    public List<Field> getFilterableFields() {
        return filterableFields;
    }

    public Map<String, Object> getAllFieldsWithData() {
        return allFieldsWithData;
    }

    public List<Field> getAllFields() {
        return allFields;
    }

    public List<PerformableAction> getPerformableActions() {
        return performableActions;
    }

    // validate form values
    public Map<String, String> validateFormValues(Map<String, String> formValues) {
//        log.info("Form values in validateFormValues: {}", formValues);
        return ValidationUtils.validateValues(validationRules, formValues, fieldLabels);
    }

    private Object getNestedValue(Object obj, String path) throws Exception {
        if (obj == null || path == null || path.isEmpty()) return null;

        String[] fields = path.split("\\.");
        Object current = obj;

        for (String fieldName : fields) {
            Field nestedField = current.getClass().getDeclaredField(fieldName);
            nestedField.setAccessible(true);
            current = nestedField.get(current);

            if (current == null) return null; // Avoid NPE in deep chains
        }

        return current;
    }

    public List<PerformableAction> determineActionsForEntity(Class<?> entityClass) {
        List<PerformableAction> actions = new ArrayList<>();

        boolean isAbstract = Modifier.isAbstract(entityClass.getModifiers());
        boolean isEmbeddable = entityClass.isAnnotationPresent(Embeddable.class);
        boolean isMappedSuperclass = entityClass.isAnnotationPresent(MappedSuperclass.class);
        boolean isInheritanceParent = entityClass.isAnnotationPresent(Inheritance.class);
        boolean isEntity = entityClass.isAnnotationPresent(Entity.class);
        boolean isConcreteSubclass = isEntity && !isAbstract && !isInheritanceParent;

        if (!isEntity || isMappedSuperclass || isEmbeddable) {
            // Cannot be managed independently
            return actions;
        }

        if (isInheritanceParent) {
            // Parent in an inheritance hierarchy
            actions.add(PerformableAction.VIEW);
            actions.add(PerformableAction.DELETE); // e.g., delete cascade
        } else if (isConcreteSubclass) {
            // Child class of an inheritance strategy
            actions.addAll(List.of(PerformableAction.CREATE, PerformableAction.EDIT, PerformableAction.VIEW));
        } else {
            // Normal entity (not part of inheritance)
            actions.addAll(List.of(PerformableAction.CREATE, PerformableAction.EDIT, PerformableAction.DELETE, PerformableAction.VIEW));
        }

        // Common extensions
        if (!isAbstract) {
            actions.add(PerformableAction.EXPORT);
            actions.add(PerformableAction.IMPORT);
        }

        return actions;
    }

    public static List<Field> getAllFields(EntityMetaModel type) {
        // add fields for parent/non-concrete entity classes
        List<Field> fields = new ArrayList<>(Arrays.asList(type.getEntityClass().getJavaType().getDeclaredFields()));
        // add fields for subtype entities
        for (EntityType<?> entityType: type.getSubTypes()){
            fields.addAll(Arrays.asList(entityType.getJavaType().getDeclaredFields()));
        }
        return fields;
    }

    public Object getIdValue() {
        return allFieldsWithData.get(idField.getName());
    }

    @Override
    public String toString() {
        return "DbObjectSchema{" +
                "entityName='" + entityName + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", validationRules=" + validationRules +
                ", fieldLabels=" + fieldLabels +
                ", displayFields=" + displayFields +
                ", customSettings=" + customDisplayFields +
                ", fieldsWithData=" + fieldsWithData +
                ", entity=" + entity +
                ", validationErrors=" + validationErrors +
                ", annotatedDisplayField=" + annotatedDisplayField +
                ", annotatedDisplayValue=" + annotatedDisplayValue +
                ", searchableFields=" + searchableFields +
                ", sortableFields=" + sortableFields +
                ", filterableFields=" + filterableFields +
                '}';
    }

}
