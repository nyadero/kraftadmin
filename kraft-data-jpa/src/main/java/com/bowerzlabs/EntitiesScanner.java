package com.bowerzlabs;

import com.bowerzlabs.annotations.InternalAdminResource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
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

    private final List<Class<?>> cachedEntities;


    public EntitiesScanner(EntityManager entityManager) {
//        get the entity name from @Table/@Entity name value
        this.cachedEntities = entityManager.getMetamodel().getEntities()
                .stream()
                .map(EntityType::getJavaType)
//                .filter(this::isInternalResource)
                .sorted(Comparator.comparing(Class::getSimpleName))
                .collect(Collectors.toList());
    }

    /**
     * Return all entities declared in the parent application
     */
    public List<Class<?>> getAllEntityClasses() {
        return cachedEntities;
    }


    /**
    * Return a single entity using its name
     */
    public Class<?> getEntityByName(String name){
        return cachedEntities.stream()
                .filter(entityClass -> {
                    String singular = entityClass.getSimpleName();
                    String pluralized = plural(singular);
                    return name.equalsIgnoreCase(singular) || name.equalsIgnoreCase(pluralized);
                })
//                .filter(this::isInternalResource)
                .findFirst()
                .orElse(null);
    }

    /**
     *     checks if the entity package name does not start with com.bowerzlabs.models
      */
    private boolean isInternalResource(Class<?> clazz) {
        return !clazz.isAnnotationPresent(InternalAdminResource.class);
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
