package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface FormFieldStrategy {
    boolean supports(Field field, DbObjectSchema dbObjectSchema);

    FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException;
}
