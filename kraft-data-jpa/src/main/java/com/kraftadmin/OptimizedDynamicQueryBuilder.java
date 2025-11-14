package com.kraftadmin;

import jakarta.persistence.Transient;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OptimizedDynamicQueryBuilder {
    private static final Logger log = LoggerFactory.getLogger(OptimizedDynamicQueryBuilder.class);

    // Cache for field metadata to avoid reflection overhead
    private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<String>> stringFieldCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<String>> sortableFieldCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, FieldPath>> nestedFieldCache = new ConcurrentHashMap<>();

    // Reserved query parameter names
    private static final Set<String> RESERVED_PARAMS = Set.of("search", "sortBy", "sortOrder", "page", "size");

    // Maximum depth for nested field traversal to prevent infinite loops
    private static final int MAX_NESTED_DEPTH = 3;

    /**
     * Builds both data and count queries efficiently with shared predicates
     */
    public static QueryResult buildQueries(
            CriteriaBuilder cb,
            Class<?> entityClass,
            Map<String, String> filters,
            String searchKeyword
    ) {
        // Build data query
        CriteriaQuery<Object> dataQuery = cb.createQuery(Object.class);
        Root<?> dataRoot = dataQuery.from(entityClass);

        // Build count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<?> countRoot = countQuery.from(entityClass);

        // Build predicates for both queries
        List<Predicate> dataPredicates = buildPredicates(cb, dataRoot, entityClass, filters, searchKeyword);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, entityClass, filters, searchKeyword);

        // Build sort orders
        List<Order> orders = buildSortOrders(cb, dataRoot, filters, entityClass);

        // Apply predicates and orders
        if (!dataPredicates.isEmpty()) {
            dataQuery.select(dataRoot).where(dataPredicates.toArray(new Predicate[0]));
        } else {
            dataQuery.select(dataRoot);
        }

        if (!countPredicates.isEmpty()) {
            countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        } else {
            countQuery.select(cb.count(countRoot));
        }

        if (!orders.isEmpty()) {
            dataQuery.orderBy(orders);
        }

        return new QueryResult(dataQuery, countQuery, orders);
    }

    /**
     * Builds predicates with improved performance and validation
     */
    public static List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<?> root,
            Class<?> entityClass,
            Map<String, String> filters,
            String searchKeyword
    ) {
        List<Predicate> predicates = new ArrayList<>();

        // Cache field metadata
        Map<String, Field> fieldMap = getFieldMap(entityClass);

        // Process filter parameters
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Skip reserved parameters and empty values
            if (RESERVED_PARAMS.contains(key.toLowerCase()) || value == null || value.trim().isEmpty()) {
                continue;
            }

            Field field = fieldMap.get(key);
            if (field != null) {
                try {
                    Predicate predicate = buildFieldPredicate(cb, root, field, value);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                } catch (Exception e) {
                    log.warn("Failed to build predicate for field '{}' with value '{}': {}", key, value, e.getMessage());
                }
            }
        }

        // Add search predicates
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            List<Predicate> searchPredicates = buildSearchPredicates(cb, root, entityClass, searchKeyword.trim());
            if (!searchPredicates.isEmpty()) {
                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }
        }

        return predicates;
    }

    /**
     * Builds search predicates only for string fields
     */
    private static List<Predicate> buildSearchPredicates(
            CriteriaBuilder cb,
            Root<?> root,
            Class<?> entityClass,
            String searchKeyword
    ) {
        List<Predicate> searchPredicates = new ArrayList<>();
        Set<String> stringFields = getStringFields(entityClass);
        String lowerSearchKeyword = "%" + searchKeyword.toLowerCase() + "%";

        for (String fieldName : stringFields) {
            try {
                Path<String> fieldPath = root.get(fieldName);
                searchPredicates.add(cb.like(cb.lower(fieldPath), lowerSearchKeyword));
            } catch (Exception e) {
                log.debug("Skipping search on field '{}': {}", fieldName, e.getMessage());
            }
        }

        return searchPredicates;
    }

    /**
     * Builds field-specific predicates with type handling
     */
    private static Predicate buildFieldPredicate(
            CriteriaBuilder cb,
            Root<?> root,
            Field field,
            String value
    ) {
        try {
            Path<Object> fieldPath = root.get(field.getName());
            Class<?> fieldType = field.getType();

            // Handle different field types
            if (fieldType == String.class) {
                // For strings, use case-insensitive contains search
                return cb.like(cb.lower(fieldPath.as(String.class)), "%" + value.toLowerCase() + "%");
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                Boolean boolValue = Boolean.parseBoolean(value);
                return cb.equal(fieldPath, boolValue);
            } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
                Object convertedValue = EntityUtil.convertValue(fieldType, value);
                return cb.equal(fieldPath, convertedValue);
            } else if (fieldType.isEnum()) {
                try {
                    @SuppressWarnings("unchecked")
                    Object enumValue = Enum.valueOf((Class<Enum>) fieldType, value.toUpperCase());
                    return cb.equal(fieldPath, enumValue);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid enum value '{}' for field '{}'", value, field.getName());
                    return null;
                }
            } else if (Date.class.isAssignableFrom(fieldType)) {
                // Handle date fields - you might want to implement date range queries
                Object convertedValue = EntityUtil.convertValue(fieldType, value);
                return cb.equal(fieldPath, convertedValue);
            } else {
                // For other types, use exact match
                Object convertedValue = EntityUtil.convertValue(fieldType, value);
                return cb.equal(fieldPath, convertedValue);
            }
        } catch (Exception e) {
            log.warn("Failed to build predicate for field '{}': {}", field.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Builds sort orders with validation and default sorting
     */
    public static List<Order> buildSortOrders(
            CriteriaBuilder cb,
            Root<?> root,
            Map<String, String> filters,
            Class<?> entityClass
    ) {
        List<Order> orders = new ArrayList<>();

        String sortBy = filters.get("sortBy");
        String sortOrder = filters.get("sortOrder");

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String fieldName = unformatLabel(sortBy.trim());

            // Validate that the field exists and is sortable
            if (isSortableField(entityClass, fieldName)) {
                try {
                    Path<Object> sortPath = root.get(fieldName);
                    boolean isAscending = sortOrder == null || sortOrder.equalsIgnoreCase("asc");
                    orders.add(isAscending ? cb.asc(sortPath) : cb.desc(sortPath));
                } catch (Exception e) {
                    log.warn("Failed to create sort order for field '{}': {}", fieldName, e.getMessage());
                }
            } else {
                log.warn("Invalid or non-sortable field for sorting: {}", fieldName);
            }
        }

        // Add default sort by id if no other sort is specified or if sort failed
        if (orders.isEmpty()) {
            try {
                orders.add(cb.desc(root.get("id")));
            } catch (Exception e) {
                log.warn("Failed to add default sort by id: {}", e.getMessage());
            }
        }

        return orders;
    }

    /**
     * Cached field map retrieval
     */
    private static Map<String, Field> getFieldMap(Class<?> entityClass) {
        return fieldCache.computeIfAbsent(entityClass, clazz -> {
            Map<String, Field> fieldMap = new HashMap<>();

            // Get all fields including inherited ones
            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers()) &&
                            !field.isAnnotationPresent(Transient.class)) {
                        field.setAccessible(true);
                        fieldMap.put(field.getName(), field);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return fieldMap;
        });
    }

    /**
     * Cached string fields retrieval
     */
    private static Set<String> getStringFields(Class<?> entityClass) {
        return stringFieldCache.computeIfAbsent(entityClass, clazz -> {
            return getFieldMap(clazz).values().stream()
                    .filter(field -> field.getType() == String.class)
                    .map(Field::getName)
                    .collect(Collectors.toSet());
        });
    }

    /**
     * Check if a field is sortable (exists and is not a collection/transient)
     */
    private static boolean isSortableField(Class<?> entityClass, String fieldName) {
        Set<String> sortableFields = sortableFieldCache.computeIfAbsent(entityClass, clazz -> {
            return getFieldMap(clazz).values().stream()
                    .filter(field -> !Collection.class.isAssignableFrom(field.getType()) &&
                            !Map.class.isAssignableFrom(field.getType()) &&
                            !field.isAnnotationPresent(Transient.class))
                    .map(Field::getName)
                    .collect(Collectors.toSet());
        });

        return sortableFields.contains(fieldName);
    }

    /**
     * Converts display label to camelCase field name
     */
    public static String unformatLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return "";
        }

        String[] parts = label.trim().split("\\s+");
        if (parts.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }

    /**
     * Clear caches (useful for testing or dynamic class loading scenarios)
     */
    public static void clearCaches() {
        fieldCache.clear();
        stringFieldCache.clear();
        sortableFieldCache.clear();
        nestedFieldCache.clear();
    }

    /**
     * Enhanced field processing that handles nested field paths
     */
    public static boolean canProcessField(Class<?> entityClass, String fieldPath) {
        if (fieldPath == null || fieldPath.trim().isEmpty()) {
            return false;
        }

        try {
            String[] segments = fieldPath.split("\\.");
            Class<?> currentClass = entityClass;

            for (String segment : segments) {
                Map<String, Field> fieldMap = getFieldMap(currentClass);
                Field field = fieldMap.get(segment);

                if (field == null) {
                    return false;
                }

                currentClass = field.getType();

                // Handle collections
                if (Collection.class.isAssignableFrom(currentClass)) {
                    currentClass = getCollectionElementType(field);
                    if (currentClass == null) {
                        return false;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Builds predicate for nested field paths
     */
    public static Predicate buildNestedFieldPredicate(
            CriteriaBuilder cb,
            Root<?> root,
            String fieldPath,
            String value
    ) {
        try {
            String[] segments = fieldPath.split("\\.");
            Path<Object> path = (Path<Object>) root;

            for (String segment : segments) {
                path = path.get(segment);
            }

            // For string fields, use LIKE operation
            if (path.getJavaType() == String.class) {
                return cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%");
            } else {
                // For other types, convert and use equality
                Object convertedValue = EntityUtil.convertValue(path.getJavaType(), value);
                return cb.equal(path, convertedValue);
            }

        } catch (Exception e) {
            log.warn("Failed to build nested field predicate for '{}': {}", fieldPath, e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the element type from a collection field using reflection
     */
    private static Class<?> getCollectionElementType(Field field) {
        try {
            Type genericType = field.getGenericType();

            if (genericType instanceof ParameterizedType paramType) {
                Type[] typeArgs = paramType.getActualTypeArguments();

                if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                    return (Class<?>) typeArgs[0];
                }
            }

            // Fallback: return Object if we can't determine the type
            return Object.class;
        } catch (Exception e) {
            log.warn("Failed to get collection element type for field '{}': {}", field.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Represents a nested field path for efficient traversal
     */
    public record FieldPath(String[] pathSegments, Class<?> targetType, boolean isCollection) {

        public String getFullPath() {
            return String.join(".", pathSegments);
        }
    }

    public record QueryResult(CriteriaQuery<Object> dataQuery, CriteriaQuery<Long> countQuery, List<Order> orders) {
    }
}