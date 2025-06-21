package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.util.List;

public class MultiSelectFieldStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        return field.isAnnotationPresent(ManyToMany.class) || field.isAnnotationPresent(ManyToOne.class);
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        return null;
    }
}
