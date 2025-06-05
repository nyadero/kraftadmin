package com.bowerzlabs.service;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.database.DbObjectSchema;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CrudService {
    private static final Logger log = LoggerFactory.getLogger(CrudService.class);
    @PersistenceContext
    private EntityManager entityManager;
    private final EntitiesScanner entityScanner;

    public CrudService(EntitiesScanner entityScanner) {
        this.entityScanner = entityScanner;
    }

    // Save or Update dynamically
//    public Object save(String entityName, Map<String, String> formValues, Object entity1) throws Exception {
//        // Log the incoming entity (if any) and the form values submitted
//        log.info("entity {} formValues {}", entity1, formValues);
//
//        // Dynamically find the entity class using its name
//        Class<?> entityClass = entityScanner.getEntityByName(entityName);
//
//        // If the entity instance is null, create a new one using reflection
//        Object entity = (entity1 == null) ? entityClass.getDeclaredConstructor().newInstance() : entity1;
//        log.info("entity in save method ${}", entity);
//
//        // Loop over each form entry (e.g., "contact.phone1" -> "0741...")
//        for (Map.Entry<String, String> entry : formValues.entrySet()) {
//            // Split keys by dot to handle nested fields (e.g., contact.phone1)
//            String[] parts = entry.getKey().split("\\.");
//            Object target = entity;         // Start with root entity
//            Class<?> currentClass = entityClass;
//
//            // Traverse each part of the field path
//            for (int i = 0; i < parts.length; i++) {
//                String fieldName = parts[i];
//
//                try {
//                    // Get the field from the current class
//                    Field field = currentClass.getDeclaredField(fieldName);
//                    field.setAccessible(true); // Allow access to private fields
//
//                    if (i == parts.length - 1) {
//                        // If it's the final field, convert and set the value
//                        Object convertedValue = convertValue(field.getType(), entry.getValue());
//                        field.set(target, convertedValue);
//                    } else {
//                        // If it's a nested field, get the object (e.g., contact)
//                        Object nestedObject = field.get(target);
//                        if (nestedObject == null) {
//                            // If it's null, create and assign it
//                            nestedObject = field.getType().getDeclaredConstructor().newInstance();
//                            field.set(target, nestedObject);
//                        }
//                        // Move down the hierarchy
//                        target = nestedObject;
//                        currentClass = field.getType(); // Update current class for the next loop
//                    }
//                } catch (NoSuchFieldException e) {
//                    // If the field is not found in the class, log a warning and break out of the loop
//                    System.err.println("⚠️ No field named '" + fieldName + "' in " + currentClass.getSimpleName());
//                    break;
//                }
//            }
//        }
//
//        // Persist the updated or newly created entity using JPA
//        return entityManager.merge(entity);
//    }
    public Object save(String entityName, Map<String, String> formValues, Object entity1) throws Exception {
        log.info("entity {} formValues {}", entity1, formValues);

        Class<?> entityClass = entityScanner.getEntityByName(entityName);
        Object entity = (entity1 == null) ? entityClass.getDeclaredConstructor().newInstance() : entity1;
        log.info("entity in save method ${}", entity);

        for (Map.Entry<String, String> entry : formValues.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            Object target = entity;
            Class<?> currentClass = entityClass;

            for (int i = 0; i < parts.length; i++) {
                String fieldName = parts[i];

                try {
                    Field field = currentClass.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if (i == parts.length - 1) {
                        Class<?> fieldType = field.getType();

//                        if (Collection.class.isAssignableFrom(fieldType)) {
//                            // Handle @OneToMany / @ManyToMany
//                            ParameterizedType listType = (ParameterizedType) field.getGenericType();
//                            Class<?> relatedClass = (Class<?>) listType.getActualTypeArguments()[0];
//
//                            Field idField = getIdField(relatedClass);
//                            if (idField == null) throw new IllegalArgumentException("No @Id field found in " + relatedClass.getSimpleName());
//
//                            String[] idStrings = entry.getValue().split(",");
//                            Collection<Object> relatedEntities = new ArrayList<>();
//
//                            for (String idStr : idStrings) {
//                                Object id = convertValue(idField.getType(), idStr.trim());
//                                Object relatedEntity = entityManager.find(relatedClass, id);
//                                if (relatedEntity != null) {
//                                    relatedEntities.add(relatedEntity);
//                                }
//                            }
//
//                            field.set(target, relatedEntities);
//
//                        } else
                            if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                            // Handle @ManyToOne / @OneToOne
                            Class<?> relatedClass = field.getType();
                            Field idField = getIdField(relatedClass);
                            if (idField == null) throw new IllegalArgumentException("No @Id field in " + relatedClass.getSimpleName());

                            Object id = convertValue(idField.getType(), entry.getValue());
                            Object relatedEntity = entityManager.find(relatedClass, id);
                            if (relatedEntity != null) {
                                field.set(target, relatedEntity);
                            }

                        } else {
                            // Primitive or normal value
                            Object convertedValue = convertValue(fieldType, entry.getValue());
                            field.set(target, convertedValue);
                        }
                            // handle manytomany relationships
                    } else {
                        // Nested object traversal
                        Object nestedObject = field.get(target);
                        if (nestedObject == null) {
                            nestedObject = field.getType().getDeclaredConstructor().newInstance();
                            field.set(target, nestedObject);
                        }
                        target = nestedObject;
                        currentClass = field.getType();
                    }

                } catch (NoSuchFieldException e) {
                    System.err.println("⚠️ No field named '" + fieldName + "' in " + currentClass.getSimpleName());
                    break;
                }
            }
        }

        return entityManager.merge(entity);
    }

    // Find by ID dynamically
    public Optional<DbObjectSchema> findById(String entityName, String id) throws Exception {
        System.out.println("Id " + id);
        Class<?> entityClass = entityScanner.getEntityByName(entityName);
        Object entityId = convertId(id, entityClass);
        Object object = entityManager.find(entityClass, entityId);
        return Optional.of(new DbObjectSchema(entityClass, object));
    }

    // find all data, can sort, search and filter
        public Page<DbObjectSchema> findAll(String entityName, int page, int size, Map<String, String> filters, List<String> sortParams) throws Exception {
            log.info("filters {}", filters);
            Class<?> entityClass = entityScanner.getEntityByName(entityName);
        
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> cq = cb.createQuery(Object.class);
            Root<?> root = cq.from(entityClass);
        
            List<Predicate> predicates = new ArrayList<>();
        
            // --- Handle Global Search ---
//            String searchKeyword = filters.get("search");
//            if (searchKeyword != null && !searchKeyword.isBlank()) {
//                List<Predicate> searchPredicates = new ArrayList<>();
//                for (Field field : entityClass.getDeclaredFields()) {
//                    String fieldName = field.getName();
//                    Class<?> fieldType = field.getType();
//                    try {
//                        if (fieldType.equals(String.class)) {
//                            searchPredicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + searchKeyword.toLowerCase() + "%"));
//                        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
//                            Object convertedValue = convertValue(fieldType, searchKeyword);
//                            if (convertedValue != null) {
//                                searchPredicates.add(cb.equal(root.get(fieldName), convertedValue));
//                            }
//                        } else if (fieldType.isEnum()) {
//                            Object enumValue = Arrays.stream(fieldType.getEnumConstants())
//                                    .filter(e -> e.toString().equalsIgnoreCase(searchKeyword))
//                                    .findFirst().orElse(null);
//                            if (enumValue != null) {
//                                searchPredicates.add(cb.equal(root.get(fieldName), enumValue));
//                            }
//                        }
//                    } catch (Exception ignored) {}
//                }
//                if (!searchPredicates.isEmpty()) {
//                    predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
//                }
//            }

//            String searchKeyword = filters.get("search");
//            if (searchKeyword != null && !searchKeyword.isBlank()) {
//                List<Predicate> searchPredicates = new ArrayList<>();
//                String lowerKeyword = searchKeyword.toLowerCase();
//
//                for (Field field : entityClass.getDeclaredFields()) {
//                    field.setAccessible(true); // allow private field access
//                    String fieldName = field.getName();
//                    Class<?> fieldType = field.getType();
//
//                    try {
//                        if (fieldType.equals(String.class)) {
//                            searchPredicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + lowerKeyword + "%"));
//                        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
//                            Object convertedValue = convertValue(fieldType, searchKeyword);
//                            if (convertedValue != null) {
//                                searchPredicates.add(cb.equal(root.get(fieldName), convertedValue));
//                            }
//                        } else if (fieldType.isEnum()) {
//                            Object enumValue = Arrays.stream(fieldType.getEnumConstants())
//                                    .filter(e -> e.toString().equalsIgnoreCase(searchKeyword))
//                                    .findFirst()
//                                    .orElse(null);
//                            if (enumValue != null) {
//                                searchPredicates.add(cb.equal(root.get(fieldName), enumValue));
//                            }
//                        }
//                        // TODO: Handle LocalDate, Boolean, or nested @Embeddable objects if needed
//                    } catch (Exception ignored) {
//                        // Log field issue if debugging: e.g., fieldName + " skipped due to error"
//                    }
//                }
//
//                if (!searchPredicates.isEmpty()) {
//                    predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
//                }
//            }

            String searchKeyword = filters.get("search");
            if (searchKeyword != null && !searchKeyword.isBlank()) {
                List<Predicate> searchPredicates = new ArrayList<>();
                Set<String> visitedPaths = new HashSet<>();
                buildSearchPredicates(cb, root, entityClass, searchKeyword, searchPredicates, visitedPaths);

                if (!searchPredicates.isEmpty()) {
                    predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
                }
            }



            // --- Handle Field Filters ---
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase("search")) continue;
        
                String value = entry.getValue();
                if (!value.isBlank()) {
                    try {
                        Field field = entityClass.getDeclaredField(key);
                        Class<?> fieldType = field.getType();

                        if (fieldType.isEnum()) {
                            Object enumValue = Arrays.stream(fieldType.getEnumConstants())
                                    .filter(e -> e.toString().equalsIgnoreCase(value))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
                            predicates.add(cb.equal(root.get(key), enumValue));
                        } else {
                            Object converted = convertValue(fieldType, value);
                            predicates.add(cb.equal(root.get(key), converted));
                        }
                    } catch (NoSuchFieldException e) {
                        log.warn("Skipping unknown filter key: {}", key);
                    }
                }
            } 

            // --- Apply filters ---
            cq.select(root).where(predicates.toArray(new Predicate[0]));
        
            // --- Apply Sorting ---
            List<Order> orderList = new ArrayList<>();
            if (sortParams != null) {
                for (String param : sortParams) {
                    String[] parts = param.split(",");
                    if (parts.length == 2) {
                        String field = parts[0];
                        Sort.Direction dir = Sort.Direction.fromString(parts[1]);
                        orderList.add(dir.isAscending() ? cb.asc(root.get(field)) : cb.desc(root.get(field)));
                    }
                }
            } else {
                // default sort
                orderList.add(cb.desc(root.get("id")));
            }
            cq.orderBy(orderList);
        
            // --- Fetch paged results ---
            TypedQuery<Object> query = entityManager.createQuery(cq);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            List<Object> resultList = query.getResultList();
        
            // --- Count query ---
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(entityClass);
            List<Predicate> countPredicates = new ArrayList<>();
        
            // Reuse filters and search for count query
            if (searchKeyword != null && !searchKeyword.isBlank()) {
                List<Predicate> searchPredicates = new ArrayList<>();
                for (Field field : entityClass.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Class<?> fieldType = field.getType();
                    try {
                        if (fieldType.equals(String.class)) {
                            searchPredicates.add(cb.like(cb.lower(countRoot.get(fieldName)), "%" + searchKeyword.toLowerCase() + "%"));
                        } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
                            Object convertedValue = convertValue(fieldType, searchKeyword);
                            if (convertedValue != null) {
                                searchPredicates.add(cb.equal(countRoot.get(fieldName), convertedValue));
                            }
                        } else if (fieldType.isEnum()) {
                            Object enumValue = Arrays.stream(fieldType.getEnumConstants())
                                    .filter(e -> e.toString().equalsIgnoreCase(searchKeyword))
                                    .findFirst().orElse(null);
                            if (enumValue != null) {
                                searchPredicates.add(cb.equal(countRoot.get(fieldName), enumValue));
                            }
                        }
                    } catch (Exception ignored) {}
                }
                if (!searchPredicates.isEmpty()) {
                    countPredicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
                }
            }
        
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase("search")) continue;
        
                String value = entry.getValue();
                try {
                    Field field = entityClass.getDeclaredField(key);
                    Class<?> fieldType = field.getType();
                    if (fieldType.isEnum()) {
                        Object enumValue = Arrays.stream(fieldType.getEnumConstants())
                                .filter(e -> e.toString().equalsIgnoreCase(value))
                                .findFirst().orElseThrow();
                        countPredicates.add(cb.equal(countRoot.get(key), enumValue));
                    } else {
                        Object converted = convertValue(fieldType, value);
                        countPredicates.add(cb.equal(countRoot.get(key), converted));
                    }
                } catch (NoSuchFieldException e) {
                    log.warn("Skipping unknown filter in count: {}", key);
                }
            }
        
            countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
            Long total = entityManager.createQuery(countQuery).getSingleResult();
        
            // Convert to schema
                List<DbObjectSchema> schemaList = resultList.stream()
                        .map(entity -> {
                            try {
                                return new DbObjectSchema(entity.getClass(), entity);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
        
        
            return new PageImpl<>(schemaList, PageRequest.of(page, size), total);
        }


    // delete item
    public void deleteById(String entityName, String id) throws Exception {
        Optional<DbObjectSchema> entityOptional = findById(entityName, id);
        if (entityOptional.isPresent()) {
            entityManager.remove(entityOptional.get().getEntity());
        } else {
            throw new EntityNotFoundException("Entity " + entityName + " with ID " + id + " not found.");
        }
    }

    // search items
    public List<DbObjectSchema> searchItems(String entityName, String searchField, String query) {
        log.info("Inside searchItems, entityName: {} searchField: {} query: {}", entityName, searchField, query);

        // Get the entity class based on the entity name
        Class<?> entityClass = entityScanner.getEntityByName(entityName);

        // Dynamically build the query based on the search field
        String queryString = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + searchField + " = :query";

        // Create a TypedQuery
        TypedQuery<?> typedQuery = entityManager.createQuery(queryString, entityClass);
        typedQuery.setParameter("query", query);

        // Execute the query and get the results
        List<?> resultList = typedQuery.getResultList();

        // Convert entities to DbObjectSchema
        List<DbObjectSchema> schemaList = resultList.stream()
                .map(entity -> {
                    return new DbObjectSchema(entity.getClass(), entity);
                })
                .collect(Collectors.toList());

        return schemaList;
    }

    public List<DbObjectSchema> getAll(String entityName) {
        try {
            // Resolve entity class by name
            Class<?> entityClass = entityScanner.getEntityByName(entityName);
            if (entityClass == null) {
                throw new IllegalArgumentException("Entity not found for name: " + entityName);
            }

            // Build and execute query
            String queryString = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<?> typedQuery = entityManager.createQuery(queryString, entityClass);
            List<?> resultList = typedQuery.getResultList();

            // Map each entity to DbObjectSchema
            List<DbObjectSchema> schemaList = new ArrayList<>();
            for (Object entity : resultList) {
                schemaList.add(new DbObjectSchema(entity.getClass(), entity));
            }
            return schemaList;
        } catch (Exception e) {
            // Handle unexpected errors
            log.info("entities error {}", e);
            throw new RuntimeException("Failed to fetch entities for: " + entityName, e);
        }
    }


    // converts field value to its equivalent field type
    private Object convertValue(Class<?> fieldType, Object value) throws NoSuchFieldException, IllegalAccessException {
        List<String> enumValues;

        if (value == null) {
            return null;
        }

        if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value.toString().trim());
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.parseLong(value.toString().trim());
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value.toString().trim());
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(String.valueOf(value));
        }
        else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        else if (fieldType == LocalTime.class) {
            // Support both 24hr and 12hr (AM/PM) formats
            DateTimeFormatter formatter = value.toString().contains("am") || value.toString().toLowerCase().contains("pm")
                    ? DateTimeFormatter.ofPattern("hh:mm a")
                    : DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse((CharSequence) value, formatter);
        }
//    else if (fieldType == LocalDateTime.class) {
//        LocalDateTime dateTime = LocalDateTime.parse(value.toString());
//        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));
//    }
        else if (fieldType == LocalDate.class) {
            if (value instanceof LocalDate) {
                return value; // Already a LocalDate
            } else if (value instanceof String) {
                return LocalDate.parse((String) value, DateTimeFormatter.ISO_DATE); // Parse from String
            }
        }
//        else if (fieldType == LocalTime.class) {
//            return LocalTime.parse(value.toString());
//        }
        else if (fieldType == ZonedDateTime.class) {
            return ZonedDateTime.parse(value.toString());
        } else if (fieldType == OffsetDateTime.class) {
            return OffsetDateTime.parse(value.toString());
        } else if (fieldType == Date.class) {
            return java.sql.Date.valueOf(value.toString()); // Convert to SQL Date
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            // Handle List types (Assuming comma-separated values)
            return Arrays.stream(value.toString().replaceAll("[\\[\\]]", "").split(","))
                    .map(String::trim)
                    .toList(); // Ensure it's a proper list
        } else if (fieldType.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) fieldType;
            enumValues = Arrays.stream(enumClass.getEnumConstants())
                    .map(Enum::name)
                    .toList();
            return enumClass.getField((String) value).get(value);
        }

        return value.toString().trim(); // Default: String
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


    // id is passed as a string, so we need to parse it to field's class
    private Object convertId(String id, Class<?> entityClass) throws NoSuchFieldException {
        Field idField = findPrimaryKeyField(entityClass);
        if (idField == null) {
            throw new NoSuchFieldException("No field annotated with @Id found in class: " + entityClass.getName());
        }

        Class<?> idType = idField.getType();

        if (idType == Long.class || idType == long.class) {
            return Long.parseLong(id);
        } else if (idType == UUID.class) {
            return UUID.fromString(id);
        } else if (idType == Integer.class || idType == int.class) {
            return Integer.parseInt(id);
        } else if (idType == String.class) {
            return id;
        }
//        if (idType == String.class) return id;
//        if (idType == Long.class || idType == long.class) return Long.parseLong(id);
//        if (idType == Integer.class || idType == int.class) return Integer.parseInt(id);
//        if (idType == UUID.class) return UUID.fromString(id);

        throw new IllegalArgumentException("Unsupported ID type: " + idType.getName());
    }


    // find the primary id by checking Id annotation on JPA entities
    private Field findPrimaryKeyField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            // find both jpa and mongo id
            if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null; // No primary key found
    }

    private Field getFieldIncludingSuperclasses(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy.");
    }

    private Field getIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }


    // === Helper Method ===
    private Field getFieldFromClass(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        for (Class<?> current = clazz; current != null; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        throw new NoSuchFieldException("No such field: " + fieldName);
    }

    private void buildSearchPredicates(CriteriaBuilder cb,
                                       Root<?> root,
                                       Class<?> entityClass,
                                       String searchKeyword,
                                       List<Predicate> searchPredicates,
                                       Set<String> visitedPaths) {

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            try {
                if (visitedPaths.contains(fieldName)) continue;
                visitedPaths.add(fieldName);

                if (fieldType.equals(String.class)) {
                    searchPredicates.add(cb.like(cb.lower(root.get(fieldName)), "%" + searchKeyword.toLowerCase() + "%"));

                } else if (Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive()) {
                    Object convertedValue = convertValue(fieldType, searchKeyword);
                    if (convertedValue != null) {
                        searchPredicates.add(cb.equal(root.get(fieldName), convertedValue));
                    }

                } else if (fieldType.isEnum()) {
                    Object enumValue = Arrays.stream(fieldType.getEnumConstants())
                            .filter(e -> e.toString().equalsIgnoreCase(searchKeyword))
                            .findFirst()
                            .orElse(null);
                    if (enumValue != null) {
                        searchPredicates.add(cb.equal(root.get(fieldName), enumValue));
                    }

                } else if (field.getAnnotation(Embedded.class) != null || fieldType.isAnnotationPresent(Embeddable.class)) {
                    // Recursive call for @Embeddable fields
                    Path<?> embeddablePath = root.get(fieldName);
                    buildSearchPredicates(cb, embeddablePath, fieldType, searchKeyword, searchPredicates, visitedPaths);

                } else if (field.getAnnotation(OneToOne.class) != null || field.getAnnotation(ManyToOne.class) != null) {
                    // Join and search associated entity
                    Join<?, ?> join = root.join(fieldName, JoinType.LEFT);
                    buildSearchPredicates(cb, join, fieldType, searchKeyword, searchPredicates, visitedPaths);
                }

            } catch (Exception e) {
                // Optionally log: fieldName + " skipped due to: " + e.getMessage()
            }
        }
    }

    private void buildSearchPredicates(CriteriaBuilder cb,
                                       Path<?> path,
                                       Class<?> clazz,
                                       String searchKeyword,
                                       List<Predicate> searchPredicates,
                                       Set<String> visitedPaths) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            try {
                if (visitedPaths.contains(fieldName)) continue;
                visitedPaths.add(fieldName);

                if (fieldType.equals(String.class)) {
                    searchPredicates.add(cb.like(cb.lower(path.get(fieldName)), "%" + searchKeyword.toLowerCase() + "%"));

                } else if (fieldType.isEnum()) {
                    Object enumValue = Arrays.stream(fieldType.getEnumConstants())
                            .filter(e -> e.toString().equalsIgnoreCase(searchKeyword))
                            .findFirst()
                            .orElse(null);
                    if (enumValue != null) {
                        searchPredicates.add(cb.equal(path.get(fieldName), enumValue));
                    }

                } else if (field.getAnnotation(Embedded.class) != null || fieldType.isAnnotationPresent(Embeddable.class)) {
                    buildSearchPredicates(cb, path.get(fieldName), fieldType, searchKeyword, searchPredicates, visitedPaths);
                }

            } catch (Exception e) {
                // Log error if needed
            }
        }
    }


}
