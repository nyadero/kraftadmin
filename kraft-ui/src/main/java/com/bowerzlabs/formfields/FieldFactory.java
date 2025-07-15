//package com.bowerzlabs.formfields;
//
//import com.bowerzlabs.database.DbObjectSchema;
//import com.bowerzlabs.formfields.fields.TextField;
//import com.bowerzlabs.formfields.strategies.*;
//import jakarta.persistence.metamodel.EntityType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Map;
//
//public class FieldFactory {
//    private final List<FormFieldStrategy> strategies;
//    private static final Logger log = LoggerFactory.getLogger(FieldFactory.class);
//
//    public FieldFactory() {
//        this.strategies = List.of(
//                new TextFieldStrategy(),
//                new EnumFieldStrategy(),
//                new NumberFieldStrategy(),
//                new EmbeddedFieldStrategy(),
/// /                new RelationshipFieldStrategy(),
//                new FileFieldStrategy(),
//                new ImageFieldStrategy(),
//                new DateFieldStrategy(),
//                new TimeFieldStrategy(),
//                new TextAreaFieldStrategy(),
//                new ColorFieldStrategy(),
//                new PasswordFieldStrategy(),
//                new EmailFieldStrategy(),
//                new TelFieldStrategy(),
//                new URLFieldStrategy(),
//                new WYSIWYGFieldStrategy(),
//                new SearchableSelectFieldStrategy(),
//                new DateTimeFieldStrategy(),
//                new CheckboxFieldStrategy(),
//                new RadioFieldStrategy(),
//                new MultiSelectFieldStrategy(),
//                new TagInputFieldStrategy(),
//                new CurrencyInputStrategy()
//        );
//    }
//
//
//    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
////        log.info("strategies {}", strategies);
//        FormField formField = null;
//        try {
//            for (FormFieldStrategy strategy : strategies) {
////                log.info("strategy type {}", strategy.supports(field, dbObjectSchema));
//                if (strategy.supports(field, dbObjectSchema)) {
//                    return  strategy.createField(field, dbObjectSchema, inputName, isSearch, subTypes);
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error generating form field for '{}': {}", field.getName(), e.getMessage(), e);
//            return new TextField(
//                    field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
//            );
//        }
////        return new TextField(
////                field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
////        );
////        return formField;
//        return null;
//    }
//}


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
                new TagInputFieldStrategy(),
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
                new CurrencyInputStrategy(),
                new CollectionTextFieldStrategy(),
                new TextFieldStrategy()
        );
    }

    public FormField createField(Field field, DbObjectSchema dbObjectSchema, String inputName, boolean isSearch, List<EntityType<?>> subTypes) {
//        log.info("Creating field for: {} of type: {}", field.getName(), field.getType().getSimpleName());

        FormField formField = null;
        try {
            for (FormFieldStrategy strategy : strategies) {
//                log.info("Checking strategy: {} for field: {}", strategy.getClass().getSimpleName(), field.getName());
                if (strategy.supports(field, dbObjectSchema)) {
//                    log.info("Strategy {} supports field {}", strategy.getClass().getSimpleName(), field.getName());
                    FormField result = strategy.createField(field, dbObjectSchema, inputName, isSearch, subTypes);
//                    log.info("Created field: {} for field: {}", result != null ? result.getClass().getSimpleName() : "null", field.getName());
                    return result;
                }
            }
        } catch (Exception e) {
//            log.error("Error generating form field for '{}': {}", field.getName(), e.getMessage(), e);
            return new TextField(
                    field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
            );
        }

        // If no strategy matches, return a default TextField instead of null
        log.warn("No strategy found for field: {} of type: {}, creating default TextField", field.getName(), field.getType().getSimpleName());
        return new TextField(
                field.getName(), "Enter " + field.getName(), false, "", field.getName(), Map.of(), Map.of()
        );
    }

}