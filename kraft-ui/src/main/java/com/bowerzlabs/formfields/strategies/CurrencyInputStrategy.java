package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import jakarta.persistence.metamodel.EntityType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CurrencyInputStrategy implements FormFieldStrategy {
    @Override
    public boolean supports(Field field, DbObjectSchema dbObjectSchema) {
        if (field.isAnnotationPresent(FormInputType.class)) {
            FormInputType formInputType = field.getAnnotation(FormInputType.class);
            return formInputType.value().equals(FormInputType.Type.CURRENCY);
        }
        return false;
    }

    @Override
    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }

}
