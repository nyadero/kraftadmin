package com.kraftadmin;

import jakarta.persistence.Id;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Reflects entity class for ID, fields, relationships
 */
public class EntityMetaModel {
    private static final Logger log = LoggerFactory.getLogger(EntityMetaModel.class);
    private final EntityType<?> entityClass;
    private final String entityName;
    private final List<EntityType<?>> subTypes;
    private final boolean superClass;

    public EntityMetaModel(EntityType<?> entityClass, List<EntityType<?>> subTypes) {
        this.entityClass = entityClass;
        this.entityName = entityClass.getJavaType().getSimpleName();
        this.subTypes = subTypes;
        superClass = true;
    }

    public Field getIdField() {
        for (Field field : entityClass.getJavaType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }else {
                break;
            }
        }
        EntityMetaModel.log.info("No @Id field in {}", entityClass.getJavaType().getSimpleName());
        return null;
    }

    public Field getCreationTimestampField(){
        for (Field field : entityClass.getJavaType().getDeclaredFields()) {
            if (field.isAnnotationPresent(CreationTimestamp.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new RuntimeException("No @CreationTimestamp field in " + entityClass.getJavaType().getSimpleName());
    }

    public Object convertId(String idString) throws NoSuchFieldException, IllegalAccessException {
        Field idField = getIdField();
        return EntityUtil.convertValue(idField.getType(), idString);
    }

    public EntityType<?> getEntityClass() {
        return entityClass;
    }

    public List<EntityType<?>> getSubTypes() {
        return subTypes;
    }


    public String getEntityName() {
        return entityName;
    }

    public boolean isSuperClass() {
        return superClass;
    }
}
