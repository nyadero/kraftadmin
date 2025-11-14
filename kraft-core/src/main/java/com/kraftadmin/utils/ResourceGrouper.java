//package com.kraftadmin.utils;
//
//import com.kraftadmin.annotations.KraftAdminResource;
//import com.kraftadmin.models1.ResourceMetadata;
//import com.kraftadmin.models1.ResourceName;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ResourceGrouper {
//    public static Map<String, List<ResourceMetadata>> groupResources(List<Class<?>> entityClasses) {
//        Map<String, List<ResourceMetadata>> grouped = new LinkedHashMap<>();
//
//
//        for (Class<?> entityClass : entityClasses) {
//            if (entityClass.isAnnotationPresent(KraftAdminResource.class)) {
//                KraftAdminResource kraftAdminResource = entityClass.getAnnotation(KraftAdminResource.class);
//                String group = kraftAdminResource.group().isEmpty() ? entityClass.getSimpleName() : kraftAdminResource.group();
//
//                ResourceMetadata metadata1 = new ResourceMetadata(
//                        new ResourceName(kraftAdminResource.name().isEmpty() ? entityClass.getSimpleName() : kraftAdminResource.name(), entityClass.getSimpleName()),
//                        group,
//                        kraftAdminResource.icon(),
//                        kraftAdminResource.editable()
//                );
//
//                grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(metadata1);
//            }
//
//            for (Field field : entityClass.getDeclaredFields()) {
//                if (field.isAnnotationPresent(KraftAdminResource.class)) {
//                    KraftAdminResource res = field.getAnnotation(KraftAdminResource.class);
//                    String group1 = res.group().isEmpty() ? entityClass.getSimpleName() : res.group();
//
//                    ResourceMetadata metadata = new ResourceMetadata(
//                            new ResourceName(res.name().isEmpty() ? field.getName() : res.name(), res.name().isEmpty() ? field.getName() : res.name()),
//                            group1,
//                            res.icon(),
//                            res.editable()
//                    );
//
//                    grouped.computeIfAbsent(group1, k -> new ArrayList<>()).add(metadata);
//                }
//            }
//        }
//
//        return grouped;
//    }
//}


//package com.kraftadmin.utils;
//
//import com.kraftadmin.annotations.KraftAdminResource;
//import com.kraftadmin.models1.ResourceMetadata;
//import com.kraftadmin.models1.ResourceName;
//
//import jakarta.persistence.*;
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.util.*;
//
//public class ResourceGrouper {
//
//    public static Map<String, List<ResourceMetadata>> groupResources(List<Class<?>> entityClasses) {
//        Map<String, List<ResourceMetadata>> grouped = new LinkedHashMap<>();
//
//        // Count how many times each class is referenced in relationships
//        Map<Class<?>, Integer> referenceCount = new HashMap<>();
//
//        for (Class<?> source : entityClasses) {
//            for (Field field : source.getDeclaredFields()) {
//                Class<?> targetType = getRelationTargetType(field);
//                if (targetType != null && entityClasses.contains(targetType)) {
//                    referenceCount.put(targetType, referenceCount.getOrDefault(targetType, 0) + 1);
//                }
//            }
//        }
//
//        for (Class<?> entityClass : entityClasses) {
/// /            if annotation is present try to group entities with the same group name together and if not present try to use the most common related entity name as qroup name
//            if (!entityClass.isAnnotationPresent(KraftAdminResource.class)) continue;
//
//            KraftAdminResource res = entityClass.getAnnotation(KraftAdminResource.class);
//
//            // Group based on the most commonly related entity (by highest reference count)
//            String group = res.group();
//
//            if (group.isEmpty()) {
//                Optional<Map.Entry<Class<?>, Integer>> mostReferenced = referenceCount.entrySet()
//                        .stream()
//                        .filter(entry -> entry.getKey() != entityClass)
//                        .max(Map.Entry.comparingByValue());
//
//                group = mostReferenced.map(entry -> entry.getKey().getSimpleName()).orElse(entityClass.getSimpleName());
//            }
//
//
//            ResourceMetadata metadata = new ResourceMetadata(
//                    new ResourceName(
//                            res.name().isEmpty() ? entityClass.getSimpleName() : res.name(),
//                            entityClass.getSimpleName()
//                    ),
//                    group,
//                    res.icon(),
//                    res.editable()
//            );
//
//            grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(metadata);
//        }
//
//        return grouped;
//    }
//
//    private static Class<?> getRelationTargetType(Field field) {
//        if (field.isAnnotationPresent(OneToMany.class)) {
//            ParameterizedType type = (ParameterizedType) field.getGenericType();
//            return (Class<?>) type.getActualTypeArguments()[0];
//        } else if (field.isAnnotationPresent(ManyToOne.class) ||
//                field.isAnnotationPresent(OneToOne.class) ||
//                field.isAnnotationPresent(ManyToMany.class)) {
//            return field.getType();
//        }
//        return null;
//    }
//}

package com.kraftadmin.utils;

import com.kraftadmin.EntityMetaModel;
import com.kraftadmin.annotations.KraftAdminResource;
import com.kraftadmin.kraft_jpa_entities.ResourceMetadata;
import com.kraftadmin.kraft_jpa_entities.ResourceName;
import jakarta.persistence.ManyToOne;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class ResourceGrouper {

    public static Map<String, List<ResourceMetadata>> groupResources(List<EntityMetaModel> entityClasses) {
        Map<String, List<ResourceMetadata>> grouped = new LinkedHashMap<>();
        Map<Class<?>, Integer> referenceCount = new HashMap<>();

        // Count how many times each class is referenced in relationships
        for (EntityMetaModel source : entityClasses) {
            for (Field field : source.getEntityClass().getJavaType().getDeclaredFields()) {
                Class<?> targetType = getRelationTargetType(field);
                if (targetType != null && entityClasses.contains(targetType)) {
                    referenceCount.put(targetType, referenceCount.getOrDefault(targetType, 0) + 1);
                }
            }
        }

        for (EntityMetaModel entityClass : entityClasses) {
            String name = entityClass.getEntityClass().getJavaType().getSimpleName();
            String group = name;
            String icon = "default-icon";
            boolean editable = false;

            if (entityClass.getEntityClass().getJavaType().isAnnotationPresent(KraftAdminResource.class)) {
                KraftAdminResource res = entityClass.getEntityClass().getJavaType().getAnnotation(KraftAdminResource.class);
                name = res.name().isEmpty() ? name : res.name();
                icon = res.icon().isEmpty() ? icon : res.icon();
                editable = res.editable();

                if (!res.group().isEmpty()) {
                    group = res.group();
                }
            }

            // If no explicit group, use the most common related entity
//            if (group.equals(entityClass.getJavaType().getSimpleName())) {
//                Optional<Map.Entry<EntityType<?>, Integer>> mostReferenced = referenceCount.entrySet()
//                        .stream()
//                        .filter(entry -> entry.getKey() != entityClass)
//                        .max(Map.Entry.comparingByValue());
//
//                group = mostReferenced.map(entry -> entry.getKey().getJavaType().getSimpleName()).orElse(group);
//            }

            ResourceMetadata metadata = new ResourceMetadata(
                    new ResourceName(name, entityClass.getEntityClass().getJavaType().getSimpleName()),
                    group,
                    icon,
                    editable
            );

            grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(metadata);
        }

        return grouped;
    }

    private static Class<?> getRelationTargetType(Field field) {
        if (field.isAnnotationPresent(ManyToOne.class)) {
            if (field.getGenericType() instanceof ParameterizedType type) {
                return (Class<?>) type.getActualTypeArguments()[0];
            }
        }
//        else if (field.isAnnotationPresent(ManyToOne.class) ||
//                field.isAnnotationPresent(OneToOne.class) ||
//                field.isAnnotationPresent(ManyToMany.class)) {
//            return field.getType();
//        }
        return null;
    }
}

