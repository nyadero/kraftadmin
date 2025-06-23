package com.bowerzlabs.repository.kraftrepos;

import com.bowerzlabs.database.DbObjectSchema;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class MainRepo extends KraftAdminRepository {

    private final EntityManager entityManager;

    public MainRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

//    public Object save(Class<?> entityClass, Map<String, String> formValues, Object entity1) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        log.info("formValues: {}", formValues);
//
//        // Step 1: Determine entity class (or subclass if _type is passed)
//
//        // Step 2: Instantiate or reuse entity
//        Object entity = (entity1 == null) ? entityClass.getDeclaredConstructor().newInstance() : entity1;
//        BeanWrapper wrapper = new BeanWrapperImpl(entity);
//        wrapper.setAutoGrowNestedPaths(true); // Allows nested properties like "address.city"
//
//        for (Map.Entry<String, String> entry : formValues.entrySet()) {
//            String key = entry.getKey();
//            String rawValue = entry.getValue();
//
//            // Skip system fields (e.g., _type or empty strings)
//            if (key.startsWith("_") || rawValue == null || rawValue.trim().isEmpty()) continue;
//
//            try {
//                PropertyDescriptor pd = wrapper.getPropertyDescriptor(key);
//                Class<?> propertyType = pd.getPropertyType();
//
//                // Handle @ManyToOne and @OneToOne
//                Field field = getFieldRecursively(entityClass, key);
//                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                    Class<?> relatedClass = propertyType;
//                    Field idField = getIdField(relatedClass);
//                    Object relatedId = convertValue(idField.getType(), rawValue);
//                    Object relatedEntity = entityManager.find(relatedClass, relatedId);
//                    wrapper.setPropertyValue(key, relatedEntity);
//                }
//
//                // Handle @ManyToMany (comma-separated IDs)
//                else if (Collection.class.isAssignableFrom(propertyType)) {
//                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
//                    Class<?> relatedClass = (Class<?>) genericType.getActualTypeArguments()[0];
//                    Field idField = getIdField(relatedClass);
//
//                    List<Object> relatedEntities = Arrays.stream(rawValue.split(","))
//                            .map(String::trim)
//                            .filter(s -> !s.isEmpty())
//                            .map(id -> convertValue(idField.getType(), id))
//                            .map(id -> entityManager.find(relatedClass, id))
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//
//                    wrapper.setPropertyValue(key, relatedEntities);
//                }
//
//                // Regular value or embeddable object
//                else {
//                    Object converted = convertValue(propertyType, rawValue);
//                    wrapper.setPropertyValue(key, converted);
//                }
//
//            } catch (Exception ex) {
//                log.warn("⚠️ Skipping field '{}' due to: {}", key, ex.getMessage());
//            }
//        }
//
//        return entityManager.merge(wrapper.getWrappedInstance());
//    }

    @Override
    public Object save(Class<?> clazz, Map<String, String> fieldValues, Object current) {
        try {
            Object entity = clazz.getDeclaredConstructor().newInstance();

            BeanWrapper wrapper = new BeanWrapperImpl(entity);
            fieldValues.forEach(wrapper::setPropertyValue);

            return entityManager.merge(entity);
        } catch (Exception e) {
            log.error("Exception while saving entity: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object findById(Class<?> clazz, Object id) {
        log.info("Finding entity for class: {}, ID: {}", clazz, id);

        try {
            // Check if this class has @Inheritance (i.e., is a base class)
            boolean isInheritanceRoot = clazz.isAnnotationPresent(Inheritance.class);

            // If this class is abstract or has @Inheritance, assume it's a base class
            if (Modifier.isAbstract(clazz.getModifiers()) || isInheritanceRoot) {
                log.info("Detected inheritance. Delegating to base class resolution.");
            }

            // Fetch entity — Hibernate will return the correct subclass automatically
            Object entity = entityManager.find(clazz, id);

            if (entity == null) {
                log.warn("No entity found for class {} with ID {}", clazz, id);
            } else {
                log.info("Resolved entity: {}", entity.getClass().getSimpleName());
            }

            return entity;

        } catch (Exception e) {
            log.error("Exception while fetching entity: {}", e.toString());
            throw new RuntimeException("Failed to fetch entity", e);
        }
    }

//    @Override
//    public Iterable<?> findAll(Class<?> clazz, Sort sort) {
//        try {
//            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e";
//
//            if (sort.isSorted()) {
//                jpql += " ORDER BY " + sort.stream()
//                        .map(order -> "e." + order.getProperty() + " " + order.getDirection())
//                        .reduce((a, b) -> a + ", " + b)
//                        .orElse("");
//            }
//
//            TypedQuery<?> query = entityManager.createQuery(jpql, clazz);
//            return query.getResultList();
//        } catch (Exception e) {
//            log.error("Exception while finding all entities: {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//    }

//    @Override
//    public Page<?> findAll(Class<?> clazz, Pageable pageable) {
//        try {
//            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e";
//            String countJpql = "SELECT COUNT(e) FROM " + clazz.getSimpleName() + " e";
//
//            // Sorting
//            if (pageable.getSort().isSorted()) {
//                jpql += " ORDER BY " + pageable.getSort().stream()
//                        .map(order -> "e." + order.getProperty() + " " + order.getDirection())
//                        .reduce((a, b) -> a + ", " + b)
//                        .orElse("");
//            }
//
//            // Fetch results
//            TypedQuery<?> query = entityManager.createQuery(jpql, clazz);
//            query.setFirstResult((int) pageable.getOffset());
//            query.setMaxResults(pageable.getPageSize());
//
//            List<?> content = query.getResultList();
//
//            // Total count
//            Long total = entityManager.createQuery(countJpql, Long.class).getSingleResult();
//
//            return new PageImpl<>(content, pageable, total);
//        } catch (Exception e) {
//            log.error("Exception while finding all entities (paged): {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//    }

    private Field getFieldRecursively(Class<?> clazz, String name) throws NoSuchFieldException {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
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

    private Object convertValue(Class<?> type, String value) {
        if (type.equals(Long.class) || type.equals(long.class)) return Long.valueOf(value);
        if (type.equals(Integer.class) || type.equals(int.class)) return Integer.valueOf(value);
        if (type.equals(Double.class) || type.equals(double.class)) return Double.valueOf(value);
        if (type.equals(Boolean.class) || type.equals(boolean.class)) return Boolean.valueOf(value);
        if (type.equals(LocalDate.class)) return LocalDate.parse(value);
        if (type.equals(LocalDateTime.class)) return LocalDateTime.parse(value);
        return value; // Default to String
    }

    @Override
    public Iterable<?> findAll(Class<?> clazz, Sort sort) {
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e";

            if (sort.isSorted()) {
                jpql += " ORDER BY " + sort.stream()
                        .map(order -> "e." + order.getProperty() + " " + order.getDirection())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            }

            TypedQuery<?> query = entityManager.createQuery(jpql, clazz);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Exception while finding all entities: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<?> findAll(Class<?> clazz, Pageable pageable) {
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e";
            String countJpql = "SELECT COUNT(e) FROM " + clazz.getSimpleName() + " e";

            // Sorting
            if (pageable.getSort().isSorted()) {
                jpql += " ORDER BY " + pageable.getSort().stream()
                        .map(order -> "e." + order.getProperty() + " " + order.getDirection())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            }

            // Fetch results
            TypedQuery<?> query = entityManager.createQuery(jpql, clazz);
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            List<?> content = query.getResultList();

            // Total count
            Long total = entityManager.createQuery(countJpql, Long.class).getSingleResult();
            log.info("content {}. total {}", content, total);
            // Convert to schema
            List<DbObjectSchema> schemaList = content.stream()
                    .map(entity -> {
                        try {
                            return new DbObjectSchema(null, entity);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return new PageImpl<>(schemaList, pageable, total);
        } catch (Exception e) {
            log.error("Exception while finding all entities (paged): {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Class<?>> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Class<?>> findAll(Pageable pageable) {
        return null;
    }
}
