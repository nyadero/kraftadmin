package com.bowerzlabs.database;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.bowerzlabs.annotations.*;
import com.bowerzlabs.config.SpringContextHolder;
import com.bowerzlabs.models.kraftmodels.DisplayFieldsPreference;
import com.bowerzlabs.repository.kraftrepos.KraftDisplayedFieldPreferenceRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.CreatedDate;

import static org.atteo.evo.inflector.English.plural;

public class DbObjectSchema {
    private static final Logger log = LoggerFactory.getLogger(DbObjectSchema.class);
    /**
     * Name of the entity/table
     */
    private final String entityName;
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
    private final Object entity;
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

    // DisplayFieldsPreferenceRepository
    KraftDisplayedFieldPreferenceRepository prefRepo =
            SpringContextHolder.getBean(KraftDisplayedFieldPreferenceRepository.class);

    public DbObjectSchema(Class<?> entityClass, Object entity) {
        try {
            if (!entityClass.isAnnotationPresent(Entity.class)) {
                throw new IllegalArgumentException("Provided class is not an entity: " + entityClass.getName());
            }

            // all fields declared in an entity class
            allFields = List.of(entityClass.getDeclaredFields());

            // a hashmap
            fieldMap = new HashMap<>();

            // assign entityName to the class/entity's name
            this.entityName = entityClass.getSimpleName();

            // assign entity to the entity received via constructor argument
            this.entity = entity;

            // fetch the display preferences for this entity
            Optional<DisplayFieldsPreference> pref = prefRepo.findById(plural(entityName));
            log.info("pref {}", pref);
            pref.ifPresent(displayedFieldsPreference -> log.info("pref {}", displayedFieldsPreference));

            if (pref.isPresent()) {
                List<String> fields = pref.get().getFields();
                log.info("fields {}", fields);
            }

            // loop over entity declared fields
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);

                // Register searchable, sortable, filterable
                if (field.isAnnotationPresent(Searchable.class)) {
                    searchableFields.add(field);
                }

                if (field.isAnnotationPresent(Sortable.class) || isNumeric(field.getType()) || isDataType(field.getType())) {
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

                validationRules.put(field.getName(), extractValidationRules(field));
                fieldLabels.put(field.getName(), extractFieldLabel(field));
                customDisplayFields = new ArrayList<>();
            }

            for (Field f : allFields) {
//                f.setAccessible(true);
//                if (f.isAnnotationPresent(DisplayField.class)) {
//                    DisplayField displayField = f.getAnnotation(DisplayField.class);
//                    String displayFieldName = displayField.value();
////                    log.info("value {}", f.get(displayFieldName));
////                    Optional<Field> field1 = Arrays.stream(f.getType().getDeclaredFields()).filter(field -> field.getName().equalsIgnoreCase(displayFieldName)).findFirst();
////                    field1.ifPresent(field -> fieldMap.put(f.getName(), field));
//                    log.info("displayfieldfield1 {}", (Object) Arrays.stream(f.getType().getDeclaredFields()).filter(field -> field.getName().equalsIgnoreCase(displayFieldName)).findFirst().orElse(null));
////                f.setAccessible(true);
//                    fieldMap.put(f.getName(), Arrays.stream(f.getType().getDeclaredFields()).filter(field -> field.getName().equalsIgnoreCase(displayFieldName)).findFirst().orElse(null));

//                } else fieldMap.put(f.getName(), f);

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
                            log.warn("⚠️ Field '{}' not found in type '{}'", displayFieldName, f.getType().getSimpleName());
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
                    log.error("❌ Error processing field '{}': {}", f.getName(), e.getMessage(), e);
                    fieldMap.put(f.getName(), f); // Fallback to the original field
                }
            }

            log.info("allFields {}", allFields);
            log.info("fieldsMap {}", fieldMap);

            // update allFieldsWithData map
            for (Field field: allFields){
                allFieldsWithData.put(field.getName(), getFieldValue(entity, field));
            }

            // Always show ID first
            Field idField = allFields.stream().filter(f -> f.isAnnotationPresent(Id.class)).findFirst().orElse(null);
//        log.info("idField {}", idField);
            if (idField != null) {
                idField.setAccessible(true);
                fieldsWithData.put(idField.getName(), getFieldValue(entity, idField));
                displayFields.add(idField);
                primaryKey = idField.getName();
            }

            // Use preferred fields if available
            if (pref.isPresent() && !pref.get().getFields().isEmpty()) {
                // Limit to 10 fields max
                int count = 0;
                for (String fieldName : pref.get().getFields()) {
                    if (fieldMap.containsKey(fieldName) &&
                            !fieldName.equals("id") &&
                            !fieldName.equals("createdAt")) {
                        Field f = fieldMap.get(fieldName);
                        f.setAccessible(true);
                        fieldsWithData.put(fieldName, getFieldValue(entity, f));
                        displayFields.add(f);
                        count++;
                        if (count >= 10) break; // stop after 10 fields
                    }
                }
            } else {
                // Use fallback fields if no custom fields defined
                allFields.stream()
                        .filter(f -> !f.isAnnotationPresent(Id.class) && !f.getName().equals("createdAt"))
                        .limit(10)
                        .forEach(f -> {
                            f.setAccessible(true);
                            fieldsWithData.put(f.getName(), getFieldValue(entity, f));
                            displayFields.add(f);
                        });
            }

            // Add createdAt last
            Field createdAtField = allFields.stream().filter(f -> f.getName().equals("createdAt") || f.isAnnotationPresent(CreatedDate.class) || f.isAnnotationPresent(CreationTimestamp.class)).findFirst()
                    .orElse(null);
            log.info("createdField {}", createdAtField);
            if (createdAtField != null) {
                createdAtField.setAccessible(true);
                fieldsWithData.put("createdAt", getFieldValue(entity, createdAtField));
                displayFields.add(createdAtField);
            }

            log.info("fieldsWithData {}", fieldsWithData);
        } catch (Exception e) {
            log.info(e.toString());
            throw new RuntimeException(e);
        }

    }

    private boolean isDataType(Class<?> type) {
        return type == Date.class || type == LocalDate.class || type == LocalDateTime.class || type == LocalTime.class;
    }

    private static boolean isNumeric(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }


    private String extractValidationRules(Field field) {
        StringBuilder rules = new StringBuilder();

        // Required
        if (field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(NotBlank.class) || field.isAnnotationPresent(NotEmpty.class)) {
            rules.append("required|");
        }

        // Size (for strings, collections)
        if (field.isAnnotationPresent(Size.class)) {
            Size size = field.getAnnotation(Size.class);
            rules.append("size(min:").append(size.min()).append(",max:").append(size.max()).append(")|");
        }

        // Min / Max (integer types)
        if (field.isAnnotationPresent(Min.class)) {
            Min min = field.getAnnotation(Min.class);
            rules.append("min:").append(min.value()).append("|");
        }
        if (field.isAnnotationPresent(Max.class)) {
            Max max = field.getAnnotation(Max.class);
            rules.append("max:").append(max.value()).append("|");
        }

        // Decimal Min / Max
        if (field.isAnnotationPresent(DecimalMin.class)) {
            DecimalMin min = field.getAnnotation(DecimalMin.class);
            rules.append("decimalMin:").append(min.value()).append("|");
        }
        if (field.isAnnotationPresent(DecimalMax.class)) {
            DecimalMax max = field.getAnnotation(DecimalMax.class);
            rules.append("decimalMax:").append(max.value()).append("|");
        }

        // Positive / Negative
        if (field.isAnnotationPresent(Positive.class)) rules.append("positive|");
        if (field.isAnnotationPresent(PositiveOrZero.class)) rules.append("positiveOrZero|");
        if (field.isAnnotationPresent(Negative.class)) rules.append("negative|");
        if (field.isAnnotationPresent(NegativeOrZero.class)) rules.append("negativeOrZero|");

        // Email
        if (field.isAnnotationPresent(Email.class)) {
            rules.append("email|");
        }

        // Digits
        if (field.isAnnotationPresent(Digits.class)) {
            Digits digits = field.getAnnotation(Digits.class);
            rules.append("digits(integer:").append(digits.integer())
                    .append(",fraction:").append(digits.fraction()).append(")|");
        }

        // AssertTrue / AssertFalse
        if (field.isAnnotationPresent(AssertTrue.class)) rules.append("mustBeTrue|");
        if (field.isAnnotationPresent(AssertFalse.class)) rules.append("mustBeFalse|");

        // Past / Future
        if (field.isAnnotationPresent(Past.class)) rules.append("past|");
        if (field.isAnnotationPresent(Future.class)) rules.append("future|");

        // Regex
        if (field.isAnnotationPresent(Pattern.class)) {
            Pattern pattern = field.getAnnotation(Pattern.class);
            rules.append("regex:").append(pattern.regexp()).append("|");
        }

        // Column nullable
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!column.nullable()) rules.append("required|");
        }

        // Form input type-based validation
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType formInputType = field.getAnnotation(FormInputType.class);
            switch (formInputType.value()) {
                case TEXT, TEXTAREA, WYSIWYG -> rules.append("string|");
                case NUMBER, RANGE -> rules.append("numeric|");
                case COLOR -> rules.append("hexColor|");
                case CHECKBOX, RADIO -> rules.append("boolean|");
                case EMAIL -> rules.append("email|");
                case PASSWORD -> rules.append("minLength:8|mustContainUppercase|mustContainSpecialChar|");
                case FILE, IMAGE -> rules.append("file|");
                case DATE, DATETIME, TIME -> rules.append("date|");
                case TEL -> rules.append("tel|");
                case URL -> rules.append("url|");
            }
        }

        return rules.toString().replaceAll("\\|$", ""); // Remove trailing "|"
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
                        displayValues.add(formatForDisplay(nestedValue != null ? nestedValue.getClass() : String.class, nestedValue));
                    }
                    return String.join(", ", displayValues); // Return as a single string for view
                }

                Object nestedValue = getNestedValue(value, path);
                return formatForDisplay(nestedValue != null ? nestedValue.getClass() : String.class, nestedValue);
            }

            // Fallback formatting for non-nested fields
            return formatForDisplay(field.getType(), value);

        } catch (Exception e) {
            throw new RuntimeException("Error getting field value", e);
        }
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

    public Map<String, String> validateFormValues(Map<String, String> formValues) {
        log.info("Form values in validateFormValues: {}", formValues);

        for (Map.Entry<String, String> entry : validationRules.entrySet()) {
            String fieldName = entry.getKey();
            String rules = entry.getValue();
            String fieldValue = formValues.getOrDefault(fieldName, "").trim();

//            log.info("Validating field: {}, value: {}, rule: {}", fieldName, fieldValue, rules);

            // Required validation
            if (rules.contains("required") && fieldValue.isEmpty()) {
                validationErrors.put(fieldName, fieldLabels.get(fieldName) + " is required.");
            }

            // Size validation
            if (rules.contains("size")) {
                int min = extractSizeValue(rules, "min");
                int max = extractSizeValue(rules, "max");
                log.info("Extracted size for {}: min={}, max={}", fieldName, min, max);
                if (fieldValue.length() < min || fieldValue.length() > max) {
                    validationErrors.put(fieldName, fieldLabels.get(fieldName) + " must be between " + min + " and " + max + " characters.");
                }
            }

            // Regex validation
            if (rules.contains("regex")) {
                String regex = extractRegex(rules);
                log.info("Extracted regex for {}: {}", fieldName, regex);
                if (!fieldValue.matches(regex)) {
                    validationErrors.put(fieldName, fieldLabels.get(fieldName) + " format is invalid.");
                }
            }
        }
        return validationErrors;
    }

    // Helper methods to extract size constraints
    private int extractSizeValue(String rules, String type) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("size\\(.*?" + type + ": (\\d+).*?\\)");
        java.util.regex.Matcher matcher = pattern.matcher(rules);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return (type.equals("min")) ? 0 : Integer.MAX_VALUE; // Defaults
    }

    private String extractRegex(String rules) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("regex: ([^|]+)");
        java.util.regex.Matcher matcher = pattern.matcher(rules);

        return matcher.find() ? matcher.group(1).trim() : "";
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

    // format field value for display
    private String formatForDisplay(Class<?> fieldType, Object value) {
        if (value == null) return "";

        if (fieldType == LocalDateTime.class) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));
        } else if (fieldType == LocalTime.class) {
            return ((LocalTime) value).format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else if (fieldType == LocalDate.class) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (fieldType == ZonedDateTime.class) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z"));
        } else if (fieldType == OffsetDateTime.class) {
            OffsetDateTime odt = (OffsetDateTime) value;
            return odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a O"));
        } else if (fieldType == Date.class) {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format((Date) value);
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return String.join(", ", ((Collection<?>) value).stream().map(Object::toString).toList());
        } else if (fieldType.isEnum()) {
            return value.toString(); // You could prettify this if needed
        }

        return value.toString(); // Default fallback
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
