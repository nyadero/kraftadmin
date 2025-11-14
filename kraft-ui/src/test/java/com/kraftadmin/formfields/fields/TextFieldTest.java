package com.kraftadmin.formfields.fields;

import com.kraftadmin.utils.KraftUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextFieldTest {

    private TextField textField;

    @BeforeEach
    void setUp() {
        textField = new TextField(
                "Name",
                "Enter your name",
                true,
                "Nyadero Brian Odhiambo",
                "name",
                Map.of("required", "true", "min", "2", "max", "20"),
                Map.of("min", "Too short", "max", "Too long")
        );
    }

    @Test
    void formatLabel() {
        assertEquals("Name", KraftUtils.formatLabel(textField.getLabel()));
    }

    @Test
    void setName() {
        textField.setName("name");
        assertEquals("name", textField.getName());
    }

    @Test
    void setLabel() {
        textField.setLabel("Name");
        assertEquals("Name", textField.getLabel());
    }

    @Test
    void getWrapperClass() {

    }

    @Test
    void setWrapperClass() {
    }

    @Test
    void setValue() {
        textField.setValue("Nyadero Brian Odhiambo");
        assertEquals("Nyadero Brian Odhiambo", textField.getValue());
    }

    @Test
    void getLabel() {
        assertEquals("Name", textField.getLabel());
    }

    @Test
    void getType() {
        assertEquals("text", textField.getType());
    }

    @Test
    void getName() {
        assertEquals("name", textField.getName());
    }

    @Test
    void getPlaceholder() {
        assertEquals("Enter your name", textField.getPlaceholder());
    }

    @Test
    void getRequired() {
        assertTrue(textField.getRequired());
    }

    @Test
    void getValue() {
        assertEquals("Nyadero Brian Odhiambo", textField.getValue());
    }

    @Test
    void getValidationErrors() {
        assertEquals(Map.of("min", "Too short", "max", "Too long"), textField.getValidationErrors());
    }

    @Test
    void getValidationRules() {
        assertEquals(Map.of("required", "true", "min", "2", "max", "20"), textField.getValidationRules());
    }

    @Test
    void getModelData() {
        assertEquals(Map.of(
                        "name", "name",
                        "placeholder", "Enter your name",
                        "label", "Name",
                        "displayField", "Nyadero Brian Odhiambo",
                        "required", true,
                        "validationRules", Map.of("required", "true", "min", "2", "max", "20"),
                        "validationErrors", Map.of("min", "Too short", "max", "Too long")
                ),
                Map.of(
                        "name", textField.getName(),
                        "placeholder", textField.getPlaceholder(),
                        "label", textField.getLabel(),
                        "displayField", textField.getValue(),
                        "required", textField.getRequired(),
                        "validationRules", textField.getValidationRules(),
                        "validationErrors", textField.getValidationErrors()
                ));
    }

}