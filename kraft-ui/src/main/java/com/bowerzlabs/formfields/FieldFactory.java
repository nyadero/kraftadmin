package com.bowerzlabs.formfields;

import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.fields.TextField;
import com.bowerzlabs.formfields.strategies.*;
import jakarta.persistence.metamodel.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class FieldFactory {
    private final List<FormFieldStrategy> strategies;
    private static final Logger log = LoggerFactory.getLogger(FieldFactory.class);

    public FieldFactory() {
        this.strategies = List.of(
                new TextFieldStrategy(),
                new EnumFieldStrategy(),
                new NumberFieldStrategy(),
                new EmbeddedFieldStrategy(),
//                new RelationshipFieldStrategy(),
                new FileFieldStrategy(),
                new ImageFieldStrategy(),
                new DateFieldStrategy(),
                new TimeFieldStrategy(),
                new TextAreaFieldStrategy(),
                new ColorFieldStrategy(),
                new PasswordFieldStrategy(),
                new EmailFieldStrategy(),
                new TelFieldStrategy(),
                new URLFieldStrategy(),
                new WYSIWYGFieldStrategy(),
                new SearchableSelectFieldStrategy(),
                new DateTimeFieldStrategy(),
                new CheckboxFieldStrategy(),
                new RadioFieldStrategy(),
                new MultiSelectFieldStrategy(),
                new TagInputFieldStrategy(),
                new CurrencyInputStrategy()
        );
    }


    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
        try {
            for (FormFieldStrategy strategy : strategies) {
                if (strategy.supports(field, dbObjectSchema)) {
                    return strategy.createField(field, dbObjectSchema, inputName, isSearch, subTypes);
                }
            }
        } catch (Exception e) {
            log.error("Error generating form field for '{}': {}", field.getName(), e.getMessage(), e);
            return new TextField(
                    field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
            );
//            throw new UnsupportedOperationException("No FormFieldStrategy found for field: " + field.getName());
        }
        return new TextField(
                field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
        );
    }
}
