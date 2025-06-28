package com.bowerzlabs.service;

import com.bowerzlabs.DynamicQueryBuilder;
import com.bowerzlabs.EntitiesScanner;
import com.bowerzlabs.EntityMetaModel;
import com.bowerzlabs.EntityValueAccessor;
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
            for (Map.Entry<String, String> entry : formValues.entrySet()) {
                EntityValueAccessor.setFieldValue(actualMetaModel.getEntityClass(), entity, entry.getKey(), entry.getValue(), entityManager);
            }

            return entityManager.merge(entity);
        } catch (Exception e) {
            log.info("error {}", e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    // Find by ID dynamically
    public Optional<DbObjectSchema> findById(String entityName, String id) throws Exception {
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);
            Object idVal = clazz.convertId(id);
            Object object = entityManager.find(clazz.getEntityClass().getJavaType(), idVal);
            log.info("object {} idVal {}", object, idVal);
            return Optional.of(new DbObjectSchema(clazz, object));
        } catch (Exception e) {
            log.info("something went wrong1 {}", e.toString());
            throw new Exception();
        }
    }

    // find all data, can sort, search and filter
    public Page<DbObjectSchema> findAll(String entityName, int page, int size, Map<String, String> filters) throws Exception {
        try {
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Object> query = DynamicQueryBuilder.buildQuery(cb, clazz.getEntityClass().getJavaType(), filters, filters.get("search"));
            TypedQuery<Object> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(page * size);
            typedQuery.setMaxResults(size);
            List<Object> results = typedQuery.getResultList();

            // ---- Count Query ----
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(clazz.getEntityClass());
            Predicate[] countPredicates = DynamicQueryBuilder.buildPredicates(cb, countRoot, clazz.getEntityClass().getJavaType(), filters, filters.get("search"));
            countQuery.select(cb.count(countRoot)).where(countPredicates);
            long total = entityManager.createQuery(countQuery).getSingleResult();

            // ---- Transform to Schema List ----
            List<DbObjectSchema> schemaList = results.stream().map(entity -> {
                try {
                    return new DbObjectSchema(clazz, entity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            return new PageImpl<>(schemaList, PageRequest.of(page, size), total);
        } catch (Exception e) {
            log.error("Error fetching data", e);
            throw new RuntimeException(e);
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

        // Create a TypedQuery
        TypedQuery<?> typedQuery = entityManager.createQuery(queryString, clazz.getEntityClass().getJavaType());
        typedQuery.setParameter("query", query);

        // Execute the query and get the results
        List<Object> resultList = Collections.singletonList(typedQuery.getResultList());

        // Convert entities to DbObjectSchema
        return resultList.stream().map(entity -> {
            return new DbObjectSchema(clazz, entity);
        }).collect(Collectors.toList());
    }

    // get all items
    public List<DbObjectSchema> getAll(String entityName) {
        try {
            // Resolve entity class by name
//            Class<?> entityClass = entityScanner.getEntityByName(entityName);
//            EntityType<?> clazz = entityScanner.getEntityByName(entityName);
            EntityMetaModel clazz = entityScanner.getEntityByName(entityName);

            if (clazz == null) {
                throw new IllegalArgumentException("Entity not found for name: " + entityName);
            }

            // Build and execute query
            String queryString = "SELECT e FROM " + clazz.getEntityClass().getJavaType().getSimpleName() + " e";
            TypedQuery<?> typedQuery = entityManager.createQuery(queryString, clazz.getEntityClass().getJavaType());
            List<?> resultList = typedQuery.getResultList();

            // Map each entity to DbObjectSchema
            List<DbObjectSchema> schemaList = new ArrayList<>();
            for (Object entity : resultList) {
                schemaList.add(new DbObjectSchema(clazz, entity));
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
        } else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (fieldType == LocalTime.class) {
            // Support both 24hr and 12hr (AM/PM) formats
            DateTimeFormatter formatter = value.toString().contains("am") || value.toString().toLowerCase().contains("pm") ? DateTimeFormatter.ofPattern("hh:mm a") : DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse((CharSequence) value, formatter);
        } else if (fieldType == LocalDate.class) {
            if (value instanceof LocalDate) {
                return value; // Already a LocalDate
            } else if (value instanceof String) {
                return LocalDate.parse((String) value, DateTimeFormatter.ISO_DATE); // Parse from String
            }
        } else if (fieldType == ZonedDateTime.class) {
            return ZonedDateTime.parse(value.toString());
        } else if (fieldType == OffsetDateTime.class) {
            return OffsetDateTime.parse(value.toString());
        } else if (fieldType == Date.class) {
            return java.sql.Date.valueOf(value.toString()); // Convert to SQL Date
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            // Handle List types (Assuming comma-separated values)
            return Arrays.stream(value.toString().replaceAll("[\\[\\]]", "").split(",")).map(String::trim).toList();
        } else if (fieldType.isEnum()) {
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) fieldType;
            enumValues = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
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

        throw new IllegalArgumentException("Unsupported ID type: " + idType.getName());
    }


    // find the primary id by checking Id annotation on JPA entities
    private Field findPrimaryKeyField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            // find both jpa and mongo id
            if (field.isAnnotationPresent(Id.class)) {
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

    private void buildSearchPredicates(CriteriaBuilder cb, Root<?> root, Class<?> entityClass, String searchKeyword, List<Predicate> searchPredicates, Set<String> visitedPaths) {

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
                    Object enumValue = Arrays.stream(fieldType.getEnumConstants()).filter(e -> e.toString().equalsIgnoreCase(searchKeyword)).findFirst().orElse(null);
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
                //  log: fieldName + " skipped due to: " + e.getMessage()
            }
        }
    }

    private void buildSearchPredicates(CriteriaBuilder cb, Path<?> path, Class<?> clazz, String searchKeyword, List<Predicate> searchPredicates, Set<String> visitedPaths) {

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

            // Query to count records in this interval
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> root = countQuery.from(entityMetaModel.getEntityClass());

            countQuery.select(cb.count(root)).where(cb.between(
                    root.get(entityMetaModel.getCreationTimestampField().getName()), intervalStart, intervalEnd
            ));

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

}
