package com.bowerzlabs.service;

import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.EntityValueAccessor;
import com.bowerzlabs.OptimizedDynamicQueryBuilder;
import com.bowerzlabs.constants.DataFormat;
import com.bowerzlabs.data.DataExporter;
import com.bowerzlabs.data.DataExporterRegistry;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.dtos.AnalyticsData;
import com.bowerzlabs.dtos.PeriodFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Transactional
public class CrudService {
    private static final Logger log = LoggerFactory.getLogger(CrudService.class);
    @PersistenceContext
    private EntityManager entityManager;
    private final EntitiesScanner entityScanner;
    private final DataExporterRegistry dataExporterRegistry;

    public CrudService(EntitiesScanner entityScanner, DataExporterRegistry dataExporterRegistry) {
        this.entityScanner = entityScanner;
        this.dataExporterRegistry = dataExporterRegistry;
    }

    // Save or Update dynamically
    public Object save(String entityName, Map<String, String> formValues, Object existing) {
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);
            Object subtype = formValues.get("subtype"); // comes from the form
            Class<?> actualClass = clazz.getEntityClass().getJavaType();
            log.info("entity {} formValues {}, actualClass {}", existing, formValues, actualClass);

            EntityMetaModel actualMetaModel = clazz;

            if (subtype != null) {
                actualMetaModel = entityScanner.getEntityFromAllByName(subtype.toString());
                actualClass = actualMetaModel.getEntityClass().getJavaType();
            }

            Object entity = (existing == null)
                    ? actualClass.getDeclaredConstructor().newInstance()
                    : existing;

            //  Use actualMetaModel here, not the original parent model
//            for (Map.Entry<String, String> entry : formValues.entrySet()) {
//                EntityValueAccessor.setFieldValue(actualMetaModel.getEntityClass(), entity, entry.getKey(), entry.getValue(), entityManager);
//            }

            // Handle regular fields and ManyToMany relations separately
            for (Map.Entry<String, String> entry : formValues.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                // Skip subtype field as it's not a real entity field
                if ("subtype".equals(fieldName)) {
                    continue;
                }

                // Check if this field is a ManyToMany relation
                if (isManyToManyField(actualMetaModel, fieldName)) {
                    handleManyToManyField(entity, fieldName, fieldValue, actualMetaModel);
                } else {
                    // Handle regular fields
                    EntityValueAccessor.setFieldValue(actualMetaModel.getEntityClass(), entity, fieldName, fieldValue, entityManager);
                }
            }

            Object savedEntity = entityManager.merge(entity);
            entityManager.flush(); // Force synchronization
            return savedEntity;
//            return entityManager.merge(entity);
        } catch (Exception e) {
            log.info("error {}", e.toString());
            throw new RuntimeException(e);
        }
    }

    // Find by ID dynamically
    public Optional<DbObjectSchema> findById(String entityName, String id) throws Exception {
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);
            Object idVal = clazz.convertId(id);
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
            Root<Object> root = criteriaQuery.from((Class<Object>) clazz.getEntityClass().getJavaType());
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(clazz.getIdField().getName()), idVal.toString()));
//            Object object = entityManager.find(clazz.getEntityClass().getJavaType(), idVal);
            Object object = entityManager.createQuery(criteriaQuery).getSingleResult();
            log.info("object {} idVal {}", object, idVal);
            return Optional.of(new DbObjectSchema(clazz, object));
        } catch (Exception e) {
            log.info("something went wrong1 {}", e.toString());
            throw new Exception();
        }
    }

    // find all data, can sort, search and filter
//    public Page<DbObjectSchema> findAll(String entityName, int page, int size, Map<String, String> filters) throws Exception {
//        try {
//            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);
//
//            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//
//            CriteriaQuery<Object> query = DynamicQueryBuilder.buildQuery(cb, clazz.getEntityClass().getJavaType(), filters, filters.get("search"));
//            TypedQuery<Object> typedQuery = entityManager.createQuery(query);
//            typedQuery.setFirstResult(page * size);
//            typedQuery.setMaxResults(size);
//            List<Object> results = typedQuery.getResultList();
//
//            // ---- Count Query ----
//            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//            Root<?> countRoot = countQuery.from(clazz.getEntityClass());
//            Predicate[] countPredicates = DynamicQueryBuilder.buildPredicates(cb, countRoot, clazz.getEntityClass().getJavaType(), filters, filters.get("search"));
//            countQuery.select(cb.count(countRoot)).where(countPredicates);
//            long total = entityManager.createQuery(countQuery).getSingleResult();
//
//            // ---- Transform to Schema List ----
//            List<DbObjectSchema> schemaList = results.stream().map(entity -> {
//                try {
//                    return new DbObjectSchema(clazz, entity);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }).collect(Collectors.toList());
//
//            return new PageImpl<>(schemaList, PageRequest.of(page, size), total);
//        } catch (Exception e) {
//            log.error("Error fetching data", e);
//            throw new RuntimeException(e);
//        }
//    }

    // Enhanced findAll method with nested field support and optimizations
    public Page<DbObjectSchema> findAll(String entityName, int page, int size, Map<String, String> filters) throws Exception {
        log.info("filters {}", filters.entrySet());
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);
            Class<?> entityClass = clazz.getEntityClass().getJavaType();

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Extract search parameter - check both "search" and "filter.search"
            String searchKeyword = filters.get("search");
            log.info("searchKeyword {}", searchKeyword);
            if (searchKeyword == null) {
                searchKeyword = filters.get("search");
            }

            log.info("searchKeyword2 {}", searchKeyword);

            // Build both queries efficiently with shared logic
            OptimizedDynamicQueryBuilder.QueryResult queryResult =
                    OptimizedDynamicQueryBuilder.buildQueries(cb, entityClass, filters, searchKeyword);

            // Execute queries with proper hints for performance
            TypedQuery<Object> dataQuery = entityManager.createQuery(queryResult.getDataQuery());
            TypedQuery<Long> countQuery = entityManager.createQuery(queryResult.getCountQuery());

            // Add query hints for better performance
            dataQuery.setHint("org.hibernate.readOnly", true);
            dataQuery.setHint("org.hibernate.cacheable", true);
            countQuery.setHint("org.hibernate.cacheable", true);

            // Set pagination
            dataQuery.setFirstResult(page * size);
            dataQuery.setMaxResults(size);

            // Execute both queries concurrently for better performance
            CompletableFuture<List<Object>> dataFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return dataQuery.getResultList();
                } catch (Exception e) {
                    log.error("Error executing data query: {}", e.getMessage());
                    throw new RuntimeException("Failed to execute data query", e);
                }
            });

            CompletableFuture<Long> countFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return countQuery.getSingleResult();
                } catch (Exception e) {
                    log.error("Error executing count query: {}", e.getMessage());
                    throw new RuntimeException("Failed to execute count query", e);
                }
            });

            // Wait for both queries to complete
            List<Object> results = dataFuture.get();
            long total = countFuture.get();

            // Transform to schema list with parallel processing for large datasets
            List<DbObjectSchema> schemaList;
            if (results.size() > 100) {
                // Use parallel processing for large datasets
                schemaList = results.parallelStream()
                        .map(entity -> {
                            try {
                                return new DbObjectSchema(clazz, entity);
                            } catch (Exception e) {
                                log.error("Error creating schema for entity: {}", entity, e);
                                throw new RuntimeException("Failed to create schema for entity", e);
                            }
                        })
                        .collect(Collectors.toList());
            } else {
                // Use sequential processing for small datasets
                schemaList = results.stream()
                        .map(entity -> {
                            try {
                                return new DbObjectSchema(clazz, entity);
                            } catch (Exception e) {
                                log.error("Error creating schema for entity: {}", entity, e);
                                throw new RuntimeException("Failed to create schema for entity", e);
                            }
                        })
                        .collect(Collectors.toList());
            }

            log.debug("Successfully retrieved {} records out of {} total for entity '{}' (page {}, size {}) with search: '{}'",
                    schemaList.size(), total, entityName, page, size, searchKeyword);

            return new PageImpl<>(schemaList, PageRequest.of(page, size), total);
        } catch (Exception e) {
            log.error("Error fetching data for entity '{}', page {}, size {}: {}",
                    entityName, page, size, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch data: " + e.getMessage(), e);
        }
    }

    // delete item
    public void deleteById(String entityName, String id) {
        try {
            Optional<DbObjectSchema> entityOptional = findById(entityName, id);
            entityOptional.ifPresent(dbObjectSchema -> entityManager.remove(dbObjectSchema.getEntity()));
        } catch (Exception e) {
            log.info("something went wrong {}", e.toString());
            throw new RuntimeException(e);
        }
    }

    // search items
    public List<DbObjectSchema> searchItems(String entityName, String searchField, String query) {
        log.info("Inside searchItems, entityName: {} searchField: {} query: {}", entityName, searchField, query);

        EntityMetaModel clazz = entityScanner.getEntityByName(entityName);

        // Dynamically build the query based on the search field
        String queryString = "SELECT e FROM " + clazz.getEntityClass().getJavaType().getSimpleName() + " e WHERE e." + searchField + " = :query";

        Class<?> entityClass = clazz.getEntityClass().getJavaType();

        // Use Criteria API
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
        Root<Object> root = criteriaQuery.from((Class<Object>) entityClass);
        criteriaQuery.select(root).where(criteriaBuilder.like(root.get(searchField), query));

        // Create a TypedQuery
        TypedQuery<?> typedQuery = entityManager.createQuery(queryString, clazz.getEntityClass().getJavaType());
        typedQuery.setParameter("query", query);

        // Execute the query and get the results
//        List<Object> resultList = Collections.singletonList(typedQuery.getResultList());
        List<Object> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        // Convert entities to DbObjectSchema
        return resultList.stream().map(entity -> {
            return new DbObjectSchema(clazz, entity);
        }).collect(Collectors.toList());
    }

    // get all items
// IgnoreCast
    public List<DbObjectSchema> getAll(String entityName) {
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);

            if (clazz == null) {
                throw new IllegalArgumentException("Entity not found for name: " + entityName);
            }

            Class<?> entityClass = clazz.getEntityClass().getJavaType();

            // Use Criteria API
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery(Object.class);
            Root<Object> root = criteriaQuery.from((Class<Object>) entityClass);
            criteriaQuery.select(root);

            TypedQuery<Object> typedQuery = entityManager.createQuery(criteriaQuery);
            List<Object> resultList = typedQuery.getResultList();

            List<DbObjectSchema> schemaList = new ArrayList<>();
            for (Object entity : resultList) {
                schemaList.add(new DbObjectSchema(clazz, entity));
            }
            return schemaList;
        } catch (Exception e) {
            log.error("Error fetching entities for: {}", entityName, e);
            throw new RuntimeException("Failed to fetch entities for: " + entityName, e);
        }
    }

    // bulk delete items
    public void bulkDelete(String entityName, List<String> selectedIds) throws Exception {
        for (String id : selectedIds) {
            Optional<DbObjectSchema> entityOptional = findById(entityName, id);
            if (entityOptional.isPresent()) {
                entityManager.remove(entityOptional.get().getEntity());
            } else {
                throw new EntityNotFoundException("Entity " + entityName + " with ID " + id + " not found.");
            }
        }
    }

    public Object exportData(String entityName, List<String> selectedIds, String format) {
        EntityMetaModel entityClass = entityScanner.getEntityByName(entityName);
        if (entityClass == null) {
            throw new IllegalArgumentException("Unknown entity: " + entityName);
        }
        // find all items where id is in selectedIds
        List<?> entities = fetchEntitiesByIds(entityClass.getEntityClass().getJavaType(), selectedIds);
        log.info("items {}", entities);

        Map<String, Object> result = extractExportData(format, entities, entityName);

        return new ResponseEntity<>(result.get("content"), (MultiValueMap<String, String>) result.get("headers"), HttpStatus.OK);
    }

    private Map<String, Object> extractExportData(String format, List<?> entities, String entityName) {
        byte[] content = null;
        HttpHeaders headers = null;
        switch (DataFormat.valueOf(format)) {
            case JSON -> {
                DataExporter exporter = dataExporterRegistry.getExporter(format);
                content = exporter.export(entities);
                headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(exporter.getContentType()));
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename(entityName + exporter.getFileExtension()).build());
                break;
            }
            case CSV -> {
                DataExporter exporter = dataExporterRegistry.getExporter(format);
                content = exporter.export(entities);
                headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(exporter.getContentType()));
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename(entityName + exporter.getFileExtension()).build());
                break;
            }
            case XML -> {
                DataExporter exporter = dataExporterRegistry.getExporter(format);
                content = exporter.export(entities);
                headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(exporter.getContentType()));
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename(entityName + exporter.getFileExtension()).build());
                break;
            }
//            default:
//                throw new IllegalArgumentException("Unsupported export format: " + format);
        }
        return Map.of("content", content, "headers", headers);
    }

    private List<?> fetchEntitiesByIds(Class<?> entityClass, List<String> ids) {
        return entityManager.createQuery(
                        "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.id IN :ids"
                )
                .setParameter("ids", ids)
                .getResultList();
    }


    public AnalyticsData loadAnalyticsData(EntityMetaModel entityMetaModel, PeriodFilter filter) throws JsonProcessingException {
        AnalyticsData analyticsData = new AnalyticsData();
        LocalDateTime start = filter.getTime();
        LocalDateTime now = LocalDateTime.now();

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        int points;
        Duration intervalDuration;
        DateTimeFormatter formatter;

        // Define interval based on period
        switch (filter.getPeriod()) {
            case MINUTE -> {
                points = 60;
                intervalDuration = Duration.ofSeconds(1);
                formatter = DateTimeFormatter.ofPattern("ss");
            }
            case HOUR -> {
                points = 60;
                intervalDuration = Duration.ofMinutes(1);
                formatter = DateTimeFormatter.ofPattern("HH:mm");
            }
            case DAY -> {
                points = 24;
                intervalDuration = Duration.ofHours(1);
                formatter = DateTimeFormatter.ofPattern("HH:mm");
            }
            case WEEK -> {
                points = 7;
                intervalDuration = Duration.ofDays(1);
                formatter = DateTimeFormatter.ofPattern("EEE");
            }
            case MONTH -> {
                points = 4;
                intervalDuration = Duration.ofDays(7); // Roughly weekly intervals
                formatter = DateTimeFormatter.ofPattern("dd MMM");
            }
            case YEAR -> {
                points = 12;
                intervalDuration = Duration.ofDays(30); // Monthly-ish intervals
                formatter = DateTimeFormatter.ofPattern("MMM");
            }
            default -> {
                points = 10;
                intervalDuration = Duration.ofMinutes(1);
                formatter = DateTimeFormatter.ofPattern("HH:mm");
            }
    }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        for (int i = 0; i < points; i++) {
            LocalDateTime intervalStart = start.plus(intervalDuration.multipliedBy(i));
            LocalDateTime intervalEnd = intervalStart.plus(intervalDuration);

            //  Skip if no @CreatedTimestamp
            if (entityMetaModel.getCreationTimestampField() == null) {
                continue;
            }

            // Query to count records in this interval
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> root = countQuery.from(entityMetaModel.getEntityClass());

            // Ensure field access is valid
            Path<LocalDateTime> timestampPath = root.get(entityMetaModel.getCreationTimestampField().getName());

            countQuery.select(cb.count(root)).where(
                    cb.between(timestampPath, intervalStart, intervalEnd)
            );

//            // Query to count records in this interval
//            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//            Root<?> root = countQuery.from(entityMetaModel.getEntityClass());
//
//            // if there is no @CreatedTimestamp skip analyzing data
//            countQuery.select(cb.count(root)).where(cb.between(
//                    root.get(entityMetaModel.getCreationTimestampField().getName()), intervalStart, intervalEnd
//            ));

            Long count = entityManager.createQuery(countQuery).getSingleResult();
            values.add(count);

            // Use start of interval for label
            String label = intervalStart.format(formatter);
            labels.add(label);
    }

        analyticsData.setLabels(Collections.singletonList(new ObjectMapper().writeValueAsString(labels)));
        analyticsData.setValues(values);
        analyticsData.setTotal(values.stream().mapToLong(Long::longValue).sum());
        analyticsData.setStartDate(start);
        analyticsData.setEndDate(now);
        analyticsData.setPeriodFilter(filter);

        return analyticsData;
    }


    private boolean isManyToManyField(EntityMetaModel metaModel, String fieldName) {
        try {
            Field field = metaModel.getEntityClass().getJavaType().getDeclaredField(fieldName);
            return field.isAnnotationPresent(ManyToMany.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }


    private void handleManyToManyField(Object entity, String fieldName, String fieldValue, EntityMetaModel metaModel) {
        log.info("handling manytomany insertions");
        try {
            Field field = metaModel.getEntityClass().getJavaType().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Get the target entity type from the generic type
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Class<?> targetEntityClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

            // Parse the comma-separated values
            String[] values = fieldValue.split(",");
            List<Object> relatedEntities = new ArrayList<>();

            log.info("values {}", values);
            for (String value : values) {
                value = value.trim();
                if (!value.isEmpty()) {
                    // Find or create the related entity
                    Object relatedEntity = findOrCreateRelatedEntity(targetEntityClass, value);
                    if (relatedEntity != null) {
                        relatedEntities.add(relatedEntity);
                    }
                }
            }
            // Set the field value
            field.set(entity, relatedEntities);
        } catch (Exception e) {
            log.error("Error handling ManyToMany field {}: {}", fieldName, e.getMessage());
            throw new RuntimeException("Failed to handle ManyToMany field: " + fieldName, e);
        }
    }

    private Object findOrCreateRelatedEntity(Class<?> entityClass, String value) {
        try {
            // First, try to find existing entity by name or other identifier
            Object existingEntity = findExistingEntity(entityClass, value);
            if (existingEntity != null) {
                return existingEntity;
            }

            // If not found, create a new one
            return createNewEntity(entityClass, value);

        } catch (Exception e) {
            log.error("Error finding or creating related entity of type {} with value {}: {}",
                    entityClass.getSimpleName(), value, e.getMessage());
            return null;
        }
    }

    private Object findExistingEntity(Class<?> entityClass, String value) {
        try {
            // Use CriteriaBuilder to search for existing entity
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> cq = (CriteriaQuery<Object>) cb.createQuery(entityClass);
            Root<Object> root = (Root<Object>) cq.from(entityClass);

            // loop over entity declared fields and find one that matches value
            for (Field field : entityClass.getDeclaredFields()) {
                log.info("field name {}", field.getName());
                Predicate predicate = cb.equal(root.get(field.getName()), value);
                cq.where(predicate);
            }

            TypedQuery<Object> query = entityManager.createQuery(cq);
            List<Object> results = query.getResultList();

            return results.isEmpty() ? null : results.get(0);

        } catch (Exception e) {
            log.debug("Could not find existing entity of type {} with value {}: {}",
                    entityClass.getSimpleName(), value, e.getMessage());
            return null;
        }
    }

//    private Object createNewEntity(Class<?> entityClass, String value) {
//        try {
//            Object newEntity = entityClass.getDeclaredConstructor().newInstance();
//
//            log.info("newEntity {}", newEntity);
//            // Set the name field - adjust based on your entity structure
//            Field nameField = entityClass.getDeclaredField("tag");
//            nameField.setAccessible(true);
//            nameField.set(newEntity, value);
//

    /// /            // Persist the new entity
    /// /            entityManager.persist(newEntity);
    /// ///            entityManager.merge(newEntity);
    /// /            return newEntity;
//
//            // DON'T persist here - let the parent transaction handle it
//            return newEntity;
//
//        } catch (Exception e) {
//            log.error("Error creating new entity of type {} with value {}: {}",
//                    entityClass.getSimpleName(), value, e.getMessage());
//            throw new RuntimeException("Failed to create new entity", e);
//
//        }
//    }
    private Object createNewEntity(Class<?> entityClass, String value) {
        try {
            Object newEntity = entityClass.getDeclaredConstructor().newInstance();
            log.info("newEntity before setting value: {}", newEntity);

            // Find the appropriate field to set the value
            Field targetField = findTargetField(entityClass);

            if (targetField != null) {
                targetField.setAccessible(true);
                targetField.set(newEntity, value);
                log.info("Set field '{}' with value '{}'", targetField.getName(), value);
            } else {
                log.warn("No suitable field found to set value '{}' in entity class '{}'",
                        value, entityClass.getSimpleName());
            }

            log.info("newEntity after setting value: {}", newEntity);
            return newEntity;

        } catch (Exception e) {
            log.error("Error creating new entity of type {} with value {}: {}",
                    entityClass.getSimpleName(), value, e.getMessage());
            throw new RuntimeException("Failed to create new entity", e);
        }
    }

    /**
     * Find the target field to set the value based on priority:
     * 1. Field annotated with @Column and not primary key
     * 2. String field that's not id, createdAt, updatedAt, etc.
     * 3. First non-excluded field
     */
    private Field findTargetField(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<Field> candidateFields = new ArrayList<>();

        for (Field field : fields) {
            // Skip fields that shouldn't be set manually
            if (shouldSkipField(field)) {
                continue;
            }

            // Priority 1: Fields with @Column annotation (likely the main content field)
            if (field.isAnnotationPresent(Column.class) && !isPrimaryKey(field)) {
                candidateFields.add(0, field); // Add to beginning for priority
            }
            // Priority 2: String fields that look like content fields
            else if (field.getType() == String.class && isContentField(field)) {
                candidateFields.add(field);
            }
            // Priority 3: Other suitable fields
            else if (field.getType() == String.class) {
                candidateFields.add(field);
            }
        }

        return candidateFields.isEmpty() ? null : candidateFields.get(0);
    }

    /**
     * Check if a field should be skipped (system fields, relations, etc.)
     */
    private boolean shouldSkipField(Field field) {
        String fieldName = field.getName().toLowerCase();

        // Skip system fields
        if (fieldName.contains("id") ||
                fieldName.contains("createdat") ||
                fieldName.contains("updatedat") ||
                fieldName.contains("createtime") ||
                fieldName.contains("updatetime") ||
                fieldName.contains("timestamp")) {
            return true;
        }

        // Skip relationship fields
        if (field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(ManyToMany.class) ||
                field.isAnnotationPresent(ManyToOne.class) ||
                field.isAnnotationPresent(OneToOne.class)) {
            return true;
        }

        // Skip transient fields
        if (field.isAnnotationPresent(Transient.class)) {
            return true;
        }

        // Skip static and final fields
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
    }

    /**
     * Check if a field is a primary key
     */
    private boolean isPrimaryKey(Field field) {
        return field.isAnnotationPresent(Id.class) ||
                field.isAnnotationPresent(EmbeddedId.class);
    }

    /**
     * Check if a field looks like a content field (common naming patterns)
     */
    private boolean isContentField(Field field) {
        String fieldName = field.getName().toLowerCase();

        // Common content field names
        return fieldName.equals("name") ||
                fieldName.equals("title") ||
                fieldName.equals("tag") ||
                fieldName.equals("value") ||
                fieldName.equals("content") ||
                fieldName.equals("description") ||
                fieldName.equals("text") ||
                fieldName.equals("label");
    }

    // Alternative approach using reflection utilities and entity metadata
    private Object createNewEntityWithMetadata(Class<?> entityClass, String value) {
        try {
            Object newEntity = entityClass.getDeclaredConstructor().newInstance();
            log.info("newEntity before setting value: {}", newEntity);

            // Use JPA metadata to find the right field
            EntityMetaModel metaModel = entityScanner.getEntityByName(entityClass.getSimpleName());
            Field targetField = findTargetFieldUsingMetadata(metaModel);

            if (targetField != null) {
                targetField.setAccessible(true);
                targetField.set(newEntity, value);
                log.info("Set field '{}' with value '{}'", targetField.getName(), value);
            }

            log.info("newEntity after setting value: {}", newEntity);
            return newEntity;

        } catch (Exception e) {
            log.error("Error creating new entity of type {} with value {}: {}",
                    entityClass.getSimpleName(), value, e.getMessage());
            throw new RuntimeException("Failed to create new entity", e);
        }
    }

    /**
     * Find target field using your existing EntityMetaModel
     */
    private Field findTargetFieldUsingMetadata(EntityMetaModel metaModel) {
        // This would use your existing entity scanning logic
        // to find the most appropriate field based on your metadata
        Class<?> entityClass = metaModel.getEntityClass().getJavaType();

        // Look for fields that are:
        // 1. Not primary keys
        // 2. Not timestamps
        // 3. Not relationships
        // 4. String type
        // 5. Likely to be the "main" content field

        return findTargetField(entityClass);
    }


}
