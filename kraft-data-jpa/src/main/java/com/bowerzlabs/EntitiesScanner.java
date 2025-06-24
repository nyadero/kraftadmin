package com.bowerzlabs;

import com.bowerzlabs.annotations.InternalAdminResource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.atteo.evo.inflector.English.plural;

@Component
public class EntitiesScanner {
    private static final Logger log = LoggerFactory.getLogger(EntitiesScanner.class);

    private final List<EntityMetaModel> cachedEntities;
    private final Set<EntityType<?>> allEntities;

//    private final List<Class<?>> cachedEntities;

    public EntitiesScanner(EntityManager entityManager) {
        this.allEntities = entityManager.getMetamodel().getEntities();

//      Todo  get the entity name from @Table/@Entity name value
        this.cachedEntities = entityManager.getMetamodel().getEntities()
                .stream()
                .map(entityType -> entityType)
                .sorted(Comparator.comparing(EntityType::getName))
                .filter(entityType -> entityType.getSupertype() == null)
//                .filter(this::isInternalResource)
                .map(entityType -> new EntityMetaModel(entityType, (List<EntityType<?>>) getAllSubTypesOf(entityType)))
                .collect(Collectors.toList());

//        entities.forEach(entityType -> log.info(" entityType {}, entityName {}", entityType.getClass().getEnclosingClass(), entityType.getName()));
        cachedEntities.forEach(entityType -> {
            log.info("subtypes for {} are {}, supers {}", entityType.getEntityClass().getName(), getAllSubTypesOf(entityType.getEntityClass()
            ), getEntityByName(entityType.getEntityClass().getName()));
        });
    }

//    public EntitiesScanner(EntityManager entityManager) {
////      Todo  get the entity name from @Table/@Entity name value
//        this.cachedEntities = entityManager.getMetamodel().getEntities()
//                .stream()
//                .map(Type::getJavaType)
////                .filter(this::isInternalResource)
//                .sorted(Comparator.comparing(Class::getSimpleName))
//                .collect(Collectors.toList());
//
//        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
//        entities.forEach(entityType -> log.info(" entityType {}, entityName {}", entityType.getSupertype(), entityType.getBindableType()));
//        entities.stream().filter(entityType -> entityType.getSupertype() == null).toList().forEach(entityType -> log.info("entity2 {}", entityType));
//    }

    /**
     * Return all entities declared in the parent application
     */
    public List<EntityMetaModel> getAllEntityClasses() {
        return cachedEntities;
    }


    /**
    * Return a single entity using its name
     */
    public EntityMetaModel getEntityByName(String name) {
        return cachedEntities.stream()
                .filter(entityType -> {
                    Class<?> javaType = entityType.getEntityClass().getJavaType();
                    String entityName = javaType.getSimpleName();
                    String pluralized = plural(entityName);


                    // Direct match with name or plural
                    if (name.equalsIgnoreCase(entityName) || name.equalsIgnoreCase(pluralized)) {
                        return true;
                    }

                    // Match superclass name (e.g., search "User" and match "AdminUser" that extends User)
                    Class<?> superClass = javaType.getSuperclass();
                    while (superClass != null && superClass != Object.class) {
                        String superName = superClass.getSimpleName();
                        if (name.equalsIgnoreCase(superName) || name.equalsIgnoreCase(plural(superName))) {
                            return true;
                        }
                        superClass = superClass.getSuperclass();
                    }

                    return false;
                })
                .findFirst()
                .orElse(null);
    }


    public EntityMetaModel getEntityFromAllByName(String name){
        return allEntities.stream()
                .map(entityType -> new EntityMetaModel(entityType, (List<EntityType<?>>) getAllSubTypesOf(entityType)))
                .filter(entityType -> {
                    Class<?> javaType = entityType.getEntityClass().getJavaType();
                    String entityName = javaType.getSimpleName();
                    String pluralized = plural(entityName);

                    // Direct match with name or plural
                    if (name.equalsIgnoreCase(entityName) || name.equalsIgnoreCase(pluralized)) {
                        return true;
                    }

                    // Match superclass name (e.g., search "User" and match "AdminUser" that extends User)
                    Class<?> superClass = javaType.getSuperclass();
                    while (superClass != null && superClass != Object.class) {
                        String superName = superClass.getSimpleName();
                        if (name.equalsIgnoreCase(superName) || name.equalsIgnoreCase(plural(superName))) {
                            return true;
                        }
                        superClass = superClass.getSuperclass();
                    }

                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    /**
     *     checks if the entity package name does not start with com.bowerzlabs.models
      */
    private boolean isInternalResource(EntityType<?> clazz) {
        return !clazz.getJavaType().isAnnotationPresent(InternalAdminResource.class);
    }


    public List<? extends  EntityType<?>> getAllSubTypesOf(EntityType<?> baseType) {
       return allEntities.stream()
               .map(entityType -> entityType)
               .filter(entityType -> baseType.getJavaType().isAssignableFrom(entityType.getJavaType())) // includes subclasses
               .filter(type -> !type.equals(baseType)) // exclude the base class itself
               .toList();
    }



    public static String resolveEntityName(Class<?> clazz) {
        jakarta.persistence.Table entityAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        if (entityAnnotation != null && !entityAnnotation.name().isBlank()) {
            log.info("entity table name value {}", entityAnnotation.name());
            return entityAnnotation.name();
        }
        return clazz.getSimpleName(); // default
    }


}
