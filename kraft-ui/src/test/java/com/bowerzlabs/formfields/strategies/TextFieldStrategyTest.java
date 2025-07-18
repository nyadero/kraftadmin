package com.bowerzlabs.formfields.strategies;

import com.bowerzlabs.annotations.FormInputType;
import com.bowerzlabs.database.DbObjectSchema;
import com.bowerzlabs.formfields.FormField;
import com.bowerzlabs.formfields.fields.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFieldStrategyTest {

    private TextFieldStrategy strategy;
    private DbObjectSchema schema;

    @BeforeEach
    void setUp() {
        strategy = new TextFieldStrategy();
//        schema = new DbObjectSchema(new EntityMetaModel());
//        schema.setValidationErrors(new HashMap<>());
//        schema.setValidationRules(new HashMap<>());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void supports() {
    }

    @Test
    void createField() {
    }

    @Test
    void supports_textField_shouldReturnTrue() throws NoSuchFieldException {
        Field field = DummyEntity.class.getDeclaredField("name");
        assertTrue(strategy.supports(field, schema));
    }

    @Test
    void supports_listField_shouldReturnFalse() throws NoSuchFieldException {
        Field field = DummyEntity.class.getDeclaredField("listField");
        assertFalse(strategy.supports(field, schema));
    }

    @Test
    void supports_annotatedNonTextField_shouldReturnFalse() throws NoSuchFieldException {
        Field field = DummyEntity.class.getDeclaredField("notText");
        assertFalse(strategy.supports(field, schema));
    }

    @Test
    void createField_shouldCreateTextField() throws NoSuchFieldException {
        Field field = DummyEntity.class.getDeclaredField("name");
        FormField formField = strategy.createField(field, schema, "inputName", false, Collections.emptyList());

        assertNotNull(formField);
        assertInstanceOf(TextField.class, formField);

        TextField textField = (TextField) formField;
        assertEquals("Name", textField.getLabel());
        assertEquals("Enter Name", textField.getPlaceholder());
        assertEquals("inputName", textField.getName());
        assertEquals("text", textField.getType());
        assertFalse(textField.getRequired());
    }

    public static class DummyEntity {
        public String name;

        @FormInputType(FormInputType.Type.NUMBER)
        public String notText;

        public List<String> listField;
    }
}