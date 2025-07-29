package com.bowerzlabs.formfields.fields;

import com.bowerzlabs.utils.KraftUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ColorFieldTest {

    private ColorField colorField;

    @BeforeEach
    void setUp() {
        colorField = new ColorField(
                "Favourite Color",
                "Enter your favorite color",
                false,
                "blue",
                "color",
                Map.of("max", "256"),
                Map.of("size", "10")
        );
    }

    @Test
    void formatLabel() {
        assertEquals("Favourite Color", KraftUtils.formatLabel(colorField.getLabel()));
    }

    @Test
    void test_set_name_should_fail() {
        colorField.setName("favColor");
        assertEquals("favColors", colorField.getName());
    }

    @Test
    void test_set_name_should_pass() {
        colorField.setName("favColor");
        assertEquals("favColor", colorField.getName());
    }

    @Test
    void test_set_label_should_fail() {
        colorField.setLabel("Enter your favourite color");
        assertEquals("Enter Color", colorField.getLabel());
    }

    @Test
    void test_set_label_should_pass() {
        colorField.setLabel("Enter Color");
        assertEquals("Enter Color", colorField.getLabel());
    }

    @Test
    void set_value_should_fail() {
        colorField.setValue("red");
        assertEquals("red", colorField.getValue());
    }

    @Test
    void set_value_should_pass() {
        colorField.setValue("blue");
        assertEquals("blue", colorField.getValue());
    }

    @Test
    void get_label_should_fail() {
        assertEquals("Enter color", colorField.getLabel());
    }

    @Test
    void get_label_should_pass() {
        assertEquals("Favourite Color", colorField.getLabel());
    }

    @Test
    void get_type_should_fail() {
        assertEquals("text", colorField.getType());
    }

    @Test
    void get_type_should_pass() {
        assertEquals("color", colorField.getType());
    }

    @Test
    void get_name_should_fail() {
        assertEquals("favCol", colorField.getName());
    }

    @Test
    void get_name_should_pass() {
        assertEquals("color", colorField.getName());
    }

    @Test
    void get_placeholder_should_fail() {
        assertEquals("Enter color", colorField.getPlaceholder());
    }

    @Test
    void get_placeholder_should_pass() {
        assertEquals("Enter your favorite color", colorField.getPlaceholder());
    }

    @Test
    void get_required_should_fail() {
        assertTrue(colorField.getRequired());
    }

    @Test
    void get_required_should_pass() {
        assertFalse(colorField.getRequired());
    }

    @Test
    void get_value_should_fail() {
        assertEquals("yellow", colorField.getValue());
    }

    @Test
    void get_value_should_pass() {
        assertEquals("blue", colorField.getValue());
    }

    @Test
    void get_validation_errors_should_fail() {
        assertEquals(Map.of("sizes", "56"), colorField.getValidationErrors());
    }

    @Test
    void get_validation_errors_should_pass() {
        assertEquals(Map.of("max", "256"), colorField.getValidationErrors());
    }

    @Test
    void get_validation_rules_should_fail() {
        assertEquals(Map.of(), colorField.getValidationRules());
    }

    @Test
    void get_validation_rules_should_pass() {
        assertEquals(Map.of("size", "10"), colorField.getValidationRules());
    }

    @Test
    void get_model_data_should_fail() {
        assertEquals(
                Map.of(
                        "labels", colorField.getLabel(),
                        "placeholder", colorField.getPlaceholder(),
                        "name", colorField.getName(),
                        "value", colorField.getValue(),
                        "required", colorField.getRequired(),
                        "type", colorField.getType(),
                        "validationRules", colorField.getValidationRules(),
                        "validationErrors", colorField.getValidationErrors()
                ),
                colorField.getModelData()
        );
    }

    @Test
    void get_model_data_should_pass() {
        assertEquals(
                Map.of(
                        "label", colorField.getLabel(),
                        "placeholder", colorField.getPlaceholder(),
                        "name", colorField.getName(),
                        "value", colorField.getValue(),
                        "required", colorField.getRequired(),
                        "type", colorField.getType(),
                        "validationRules", colorField.getValidationRules(),
                        "validationErrors", colorField.getValidationErrors()
                ),
                colorField.getModelData()
        );
    }
}