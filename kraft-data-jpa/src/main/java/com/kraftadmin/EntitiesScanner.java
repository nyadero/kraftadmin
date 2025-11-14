package com.kraftadmin;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.atteo.evo.inflector.English.plural;

@Component
public class EntitiesScanner {
    private static final Logger log = LoggerFactory.getLogger(EntitiesScanner.class);

    private final List<EntityMetaModel> cachedEntities;
    private final Set<EntityType<?>> allEntities;

    public EntitiesScanner(EntityManager entityManager) {
        this.allEntities = entityManager.getMetamodel().getEntities();

        this.cachedEntities = entityManager.getMetamodel().getEntities()
                .stream()
                .sorted(Comparator.comparing(EntityType::getName))
                .filter(entityType -> entityType.getSupertype() == null)
//                .filter(entityType -> !entityType.getJavaType().isAnnotationPresent(InternalAdminResource.class))
                .map(entityType -> new EntityMetaModel(entityType, (List<EntityType<?>>) getAllSubTypesOf(entityType)))
                .collect(Collectors.toList());

        log.info("entities scanned successfully {}", cachedEntities);
    }

    @PostConstruct
    public void init() {
        log.info("entities scanning");
    }

    /**
     * Return all entities declared in the parent application
     */
    public List<EntityMetaModel> getAllEntityClasses() {
        return cachedEntities;
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

    public List<? extends  EntityType<?>> getAllSubTypesOf(EntityType<?> baseType) {
       return allEntities.stream()
               .filter(entityType -> baseType.getJavaType().isAssignableFrom(entityType.getJavaType())) // includes subclasses
               .filter(type -> !type.equals(baseType)) // exclude the base class itself
               .toList();
    }


    // Manual entity name resolution that checks @Entity annotation directly
    public static String resolveEntityNameManual(Class<?> clazz) {
        try {
            if (clazz.isAnnotationPresent(jakarta.persistence.Entity.class)) {
                jakarta.persistence.Entity entityAnnotation = clazz.getAnnotation(jakarta.persistence.Entity.class);
                String name = entityAnnotation.name();

                if (name != null && !name.trim().isEmpty()) {
//                    log.debug("Found @Entity(name='{}') for class {}", name, clazz.getSimpleName());
                    return name;
                }
            }

//            log.debug("Using class simple name for {}", clazz.getSimpleName());
            return clazz.getSimpleName();

        } catch (Exception e) {
            log.error("Error resolving entity name for {}, falling back to simple name", clazz.getSimpleName(), e);
            return clazz.getSimpleName();
        }
    }

    /**
     * Find an EntityMetamodel based on the name param passed
     */
    public EntityMetaModel getEntityByName(String name) {

        // First, let's see what entities we have
        cachedEntities.forEach(entityMetaModel -> {
            EntityType<?> entityType = entityMetaModel.getEntityClass();
            Class<?> javaType = entityType.getJavaType();
        });

        EntityMetaModel result = cachedEntities.stream()
                .filter(entityMetaModel -> {
                    EntityType<?> entityType = entityMetaModel.getEntityClass();
                    String jpaEntityName = entityType.getName();
                    Class<?> javaType = entityType.getJavaType();
                    String simpleClassName = javaType.getSimpleName();

//                    log.info("Comparing '{}' with JPA name '{}' and simple class '{}'",
//                            name, jpaEntityName, simpleClassName);

                    boolean matches = name.equalsIgnoreCase(jpaEntityName) ||
                            name.equalsIgnoreCase(simpleClassName);

                    return matches;
                })
                .findFirst()
                .orElse(null);

//        if (result == null) {
//            log.error("No entity found with name: {}", name);
//        }

        return result;

    }


}
